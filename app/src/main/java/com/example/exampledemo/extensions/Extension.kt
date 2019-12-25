package com.example.exampledemo.extensions

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

fun Context.getStringResources(id: Int): String {
    return resources.getString(id)
}

fun Context.readRawFile(resId: Int): String? {
    val inputStream = resources.openRawResource(resId)
    val inputStreamReader = InputStreamReader(inputStream)
    val buffReader = BufferedReader(inputStreamReader)
    var line: String?
    val text = StringBuilder()
    try {
        line = buffReader.readLine()
        while (line != null) {
            text.append(line)
            text.append('\n')
            line = buffReader.readLine()
        }
    } catch (e: Exception) {
        return null
    }
    return text.toString()
}