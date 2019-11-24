package com.example.exampledemo.ui.gallery.entity

data class AssetEntity(
    val id: String,
    val path: String,
    val createDt: Long,
    val width: Int,
    val height: Int,
    val displayName: String,
    val modifiedDate: Long
)