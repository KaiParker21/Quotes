package com.skye.quotes.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skye.quotes.network.QuoteApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val quotesRepository: QuotesRepository
}

class DefaultAppContainer(): AppContainer {

    private val baseUrl = "https://zenquotes.io/api/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: QuoteApiService by lazy {
        retrofit.create(QuoteApiService::class.java)
    }

    override val quotesRepository: QuotesRepository by lazy {
        NetworkQuoteRepository(retrofitService)
    }
}