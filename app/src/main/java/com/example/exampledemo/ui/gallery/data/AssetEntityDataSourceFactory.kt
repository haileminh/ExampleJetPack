package com.example.exampledemo.ui.gallery.data

import android.content.Context
import androidx.paging.DataSource
import com.example.exampledemo.ui.gallery.entity.AssetEntity

class AssetEntityDataSourceFactory(val context: Context) : DataSource.Factory<Int, AssetEntity>() {
    override fun create(): DataSource<Int, AssetEntity> {
        return AssetEntityDataSource(context)
    }
}