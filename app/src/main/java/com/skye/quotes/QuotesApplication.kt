package com.skye.quotes

import android.app.Application
import com.skye.quotes.data.AppContainer
import com.skye.quotes.data.DefaultAppContainer

class QuotesApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}