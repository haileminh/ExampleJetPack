package com.example.exampledemo.ui.gallery.entity

data class GalleryEntity(
    val id: String,
    val name: String,
    var length: Int,
    val typeInt: Int,
    var isAll: Boolean = false
)