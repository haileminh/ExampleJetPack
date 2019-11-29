package com.example.exampledemo.ui.gallery.data

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.example.exampledemo.common.Constants
import com.example.exampledemo.ui.gallery.entity.AssetEntity
import java.util.*

class AssetEntityDataSource(val context: Context) : PageKeyedDataSource<Int, AssetEntity>() {

    private val dao = DBUtils()
    private var timeStamp: Long = 0


    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, AssetEntity>
    ) {
        timeStamp = Date().time
        Log.d(Constants.APP_NAME, "AssetEntityDataSource loadInitial ${params.requestedLoadSize}")

        val list = dao.getAssets(context, 0, params.requestedLoadSize, timeStamp)

        callback.onResult(
            list,
            null,
            if (list.size < params.requestedLoadSize) null else 1
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, AssetEntity>) {
        Log.d(Constants.APP_NAME, "AssetEntityDataSource loadAfter ${params.key}")

        val list = dao.getAssets(context, params.key, params.requestedLoadSize, timeStamp)
        callback.onResult(
            list,
            if (list.size < params.requestedLoadSize) null else (params.key + 1)
        )

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, AssetEntity>) {

    }
}