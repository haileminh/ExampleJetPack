package com.example.exampledemo.db

import com.example.exampledemo.common.Constants


class Storage private constructor() {
    private object Holder {
        var INSTANCE = Storage()
    }

    companion object {
        val instance: Storage by lazy { Holder.INSTANCE }
    }

    fun saveShouldShowRequestPermissionRationale(perrmission: String) {
        AppSharedPreferences.getInstance().set("${Constants.KEY_PERMISSION}_$perrmission", true)
    }

    fun shouldShowRequestPermissionRationale(perrmission: String): Boolean {
        return AppSharedPreferences.getInstance()
            .getBoolean("${Constants.KEY_PERMISSION}_$perrmission")
    }

}
