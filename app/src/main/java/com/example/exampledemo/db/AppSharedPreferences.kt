package com.example.exampledemo.db

import android.content.Context
import android.content.SharedPreferences
import com.example.exampledemo.common.Constants
import java.lang.Exception


class AppSharedPreferences private constructor(ctx: Context) {
    private var prefs: SharedPreferences = ctx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private var instance: AppSharedPreferences? = null

        fun init(context: Context): AppSharedPreferences {
            if (instance != null) return instance!!
            instance = AppSharedPreferences(context)
            return instance!!
        }

        fun getInstance(): AppSharedPreferences {
            if (instance == null){
                throw  Exception("need init before use")
            }
            return instance!!
        }
    }


    fun set(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defValue: String = ""): String {
        return prefs.getString(key, defValue) ?: defValue
    }

    fun set(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defValue: Int = 0): Int {
        return prefs.getInt(key, defValue)
    }

    fun set(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    /**
     * Clear all value in SharedPreferences.
     */
    fun clearDataPref() {
        prefs.edit().clear().apply()
    }
}

