package com.skye.quotes.network

import retrofit2.http.GET

interface QuoteApiService {
    @GET("random")
    suspend fun getRandomQuote(): List<Quote>

    @GET("today")
    suspend fun getTodayQuote(): List<Quote>

}
