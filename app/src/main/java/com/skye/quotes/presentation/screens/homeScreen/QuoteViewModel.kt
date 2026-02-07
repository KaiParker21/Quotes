package com.skye.quotes.presentation.screens.homeScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skye.quotes.QuotesApplication
import com.skye.quotes.data.QuotesRepository
import com.skye.quotes.network.Quote
import kotlinx.coroutines.launch

sealed interface QuoteUiState{
    data class Success(val quote: Quote): QuoteUiState
    object Loading: QuoteUiState
    data class Error(val error: String): QuoteUiState
}


class QuoteViewModel(
    private val quotesRepository: QuotesRepository
): ViewModel() {

    var quoteUiState: QuoteUiState by mutableStateOf(QuoteUiState.Loading)
        private set

    fun reloadQuote() {
        getRandomQuote()
    }

    init {
        getRandomQuote()
    }

    fun getRandomQuote() {
        viewModelScope.launch {
            quoteUiState = QuoteUiState.Loading
            try {
                val result = quotesRepository.getRandomQuote()
                quoteUiState = QuoteUiState.Success(result[0])
            } catch (e: Exception) {
                quoteUiState = QuoteUiState.Error(e.message.toString())
                Log.e("HomeScreenViewModel", e.message.toString())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as QuotesApplication)
                val quotesRepository = application.container.quotesRepository
                QuoteViewModel(quotesRepository)
            }
        }
    }
}