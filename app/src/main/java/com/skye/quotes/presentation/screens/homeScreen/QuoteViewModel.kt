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
import okio.IOException

sealed interface QuoteUiState{
    data class Success(val quote: Quote): QuoteUiState
    object Loading: QuoteUiState
    data class Error(val error: String): QuoteUiState
}


class QuoteViewModel(
    private val quotesRepository: QuotesRepository
): ViewModel() {

    var randomQuoteUiState: QuoteUiState by mutableStateOf(QuoteUiState.Loading)
        private set

    var todayQuoteUiState: QuoteUiState by mutableStateOf(QuoteUiState.Loading)
        private set

    init {
        getTodayQuote()
        getRandomQuote()
    }

    fun reloadQuote() {

        getRandomQuote()
    }

    fun getRandomQuote() {
        viewModelScope.launch {
            randomQuoteUiState = QuoteUiState.Loading
            try {
                val result = quotesRepository.getRandomQuote()
                randomQuoteUiState = QuoteUiState.Success(result[0])
            } catch (e: IOException) {
                randomQuoteUiState = QuoteUiState.Error("Network error: ${e.message ?: "Couldn't get quote"}")
            } catch (e: Exception) {
                randomQuoteUiState = QuoteUiState.Error("Error: ${e.message ?: "Unknown error occurred"}")
            }
        }
    }

    fun getTodayQuote() {
        viewModelScope.launch {
            todayQuoteUiState = QuoteUiState.Loading
            try {
                Log.d("QuoteViewModel", "Fetching today's quote")
                val result = quotesRepository.getTodayQuote()
                Log.d("QuoteViewModel", "Today's quote fetched: $result")
                todayQuoteUiState = QuoteUiState.Success(quote = result[0])
                Log.d("QuoteViewModel", "Today's quote UI state updated: $todayQuoteUiState")
            } catch (e: IOException) {
                todayQuoteUiState = QuoteUiState.Error("Network error: ${e.message ?: "Couldn't get quote"}")
            } catch (e: Exception) {
                todayQuoteUiState = QuoteUiState.Error("Error: ${e.message ?: "Unknown error occurred"}")
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