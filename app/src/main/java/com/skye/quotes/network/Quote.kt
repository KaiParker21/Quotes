package com.skye.quotes.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    @SerialName("q")
    val quote: String,
    @SerialName("a")
    val author: String,
    @SerialName("h")
    val html: String
)
