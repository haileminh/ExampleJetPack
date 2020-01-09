package com.example.exampledemo.binding

import android.text.format.DateUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.common.util.DataUtils
import java.io.File

@BindingAdapter("imageFile")
fun ImageView.setImageFile(
    file: File?
) {
    if (file == null || !file.exists() || !file.isFile) return
    Glide
        .with(this)
        .load(file)
        .into(this)
}

/**
 * Converts milliseconds to formatted mm:ss
 *
 * @param value, time in milliseconds.
 */
@BindingAdapter("elapsedTime")
fun TextView.setElapsedTime(value: Long) {
    val seconds = value / 1000
    text = if (seconds < 60) seconds.toString() else DateUtils.formatElapsedTime(seconds)
}