package com.skye.quotes.network

import retrofit2.http.GET

interface QuoteApiService {
    @GET("random")
    suspend fun getQuote(): List<Quote>
}
