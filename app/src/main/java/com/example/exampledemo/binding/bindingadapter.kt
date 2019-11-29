package com.example.exampledemo.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
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