package com.example.exampledemo.ui.gallery

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.exampledemo.app.BaseViewModel
import com.example.exampledemo.ui.gallery.data.AssetEntityDataSourceFactory
import com.example.exampledemo.ui.gallery.entity.AssetEntity

class GalleryViewModel : BaseViewModel() {
    lateinit var assetEntityList: LiveData<PagedList<AssetEntity>>

    private lateinit var soureFactory: AssetEntityDataSourceFactory

    fun initData(context: Context) {
        soureFactory = AssetEntityDataSourceFactory(context)
        val config = PagedList.Config.Builder()
            .setPageSize(60)
            .setInitialLoadSizeHint(60)
            .setPrefetchDistance(1)
            .setEnablePlaceholders(false)
            .build()

        assetEntityList = LivePagedListBuilder(soureFactory, config).build()
    }

}