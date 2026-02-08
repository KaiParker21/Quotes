package com.skye.quotes.data

import com.skye.quotes.network.Quote
import com.skye.quotes.network.QuoteApiService

interface QuotesRepository {
    suspend fun getRandomQuote(): List<Quote>
    suspend fun getTodayQuote(): List<Quote>
}

class NetworkQuoteRepository(
    private val quoteApiService: QuoteApiService
): QuotesRepository {
    override suspend fun getRandomQuote(): List<Quote> = quoteApiService.getRandomQuote()
    override suspend fun getTodayQuote(): List<Quote> = quoteApiService.getTodayQuote()
}