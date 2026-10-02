package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.downloader.FontManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class Web2ApkApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        FontManager.initialize(this, applicationScope)
    }
}
