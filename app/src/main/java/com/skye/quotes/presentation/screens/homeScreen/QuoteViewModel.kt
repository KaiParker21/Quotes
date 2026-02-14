package com.skye.quotes.presentation.screens.homeScreen

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

sealed interface QuoteUiState{
    data class Success(val quote: Quote): QuoteUiState
    object Loading: QuoteUiState
    data class Error(val error: String): QuoteUiState
    data class RateLimited(val secondsRemaining: Int) : QuoteUiState
}


class QuoteViewModel(
    private val quotesRepository: QuotesRepository
): ViewModel() {

    var randomQuoteUiState: QuoteUiState by mutableStateOf(QuoteUiState.Loading)
        private set

    var todayQuoteUiState: QuoteUiState by mutableStateOf(QuoteUiState.Loading)
        private set

    private var countdownJob: Job? = null

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
                val quotes = quotesRepository.getRandomQuote()
                val quote = quotes.firstOrNull()
                    ?: throw IllegalStateException("Empty response")
                randomQuoteUiState = QuoteUiState.Success(quote)
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    handleRateLimit()
                    return@launch
                }
                randomQuoteUiState =
                    QuoteUiState.Error("Server error (${e.code()})")
            } catch (_: IOException) {
                randomQuoteUiState =
                    QuoteUiState.Error("Network connection problem")
            } catch (_: Exception) {
                randomQuoteUiState =
                    QuoteUiState.Error("Unexpected error occurred")
            }
        }
    }


    fun getTodayQuote() {
        viewModelScope.launch {
            todayQuoteUiState = QuoteUiState.Loading
            try {
                val quotes = quotesRepository.getTodayQuote()
                val quote = quotes.firstOrNull()
                    ?: throw IllegalStateException("Empty response")
                todayQuoteUiState = QuoteUiState.Success(quote)
            } catch (_: IOException) {
                todayQuoteUiState =
                    QuoteUiState.Error("Network connection problem")
            } catch (_: Exception) {
                todayQuoteUiState =
                    QuoteUiState.Error("Something went wrong")
            }
        }
    }


    private fun handleRateLimit() {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            for (i in 30 downTo 1) {
                randomQuoteUiState = QuoteUiState.RateLimited(i)
                delay(1000)
            }

            getRandomQuote()
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