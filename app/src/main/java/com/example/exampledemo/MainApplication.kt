package com.example.exampledemo

import android.app.Application
import com.example.exampledemo.db.AppSharedPreferences

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppSharedPreferences.init(this)
    }
}
