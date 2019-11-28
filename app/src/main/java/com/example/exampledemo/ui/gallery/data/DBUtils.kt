package com.example.exampledemo.ui.gallery.data

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.example.exampledemo.common.Constants
import com.example.exampledemo.ui.gallery.entity.AssetEntity
import java.io.File

class DBUtils {
    fun getAssets(context: Context, page: Int, pageSize: Int, timeStamp: Long): List<AssetEntity> {
        val list = ArrayList<AssetEntity>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        Log.d(Constants.APP_NAME, "Load URI: $uri time_stamp $timeStamp page $page size $pageSize")

        val args = ArrayList<String>()

        val dateSelection = "AND ${MediaStore.Images.Media.DATE_TAKEN} <= ?"
        args.add(timeStamp.toString())

        val selection =
            "${MediaStore.Images.ImageColumns.BUCKET_ID} IS NOT NULL  $dateSelection $sizeWhere"

        val sortOrder =
            "${MediaStore.Images.Media.DATE_TAKEN} DESC LIMIT $pageSize OFFSET ${page * pageSize}"

        val cursor = context.contentResolver.query(
            uri,
            storeImageKeys,
            selection,
            args.toTypedArray(),
            sortOrder
        )
            ?: return emptyList()

        while (cursor.moveToNext()) {
            val id = cursor.getString(MediaStore.MediaColumns._ID)
            val path = cursor.getString(MediaStore.MediaColumns.DATA)
            val date = cursor.getLong(MediaStore.Images.Media.DATE_TAKEN)
            val modifiedDate = cursor.getLong(MediaStore.MediaColumns.DATE_MODIFIED)
            val width = cursor.getInt(MediaStore.MediaColumns.WIDTH)
             val height = cursor.getInt(MediaStore.MediaColumns.HEIGHT)
            val displayName = File(path).name

            val asset = AssetEntity(id, path, date, width, height, displayName, modifiedDate)

            list.add(asset)
        }

        cursor.close()

        return list
    }

    private val sizeWhere: String
        get() {
            return "AND ${MediaStore.MediaColumns.WIDTH} > 0 AND ${MediaStore.MediaColumns.HEIGHT} > 0"
        }

    companion object {

        val storeImageKeys = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_TAKEN
        )
    }

    private fun Cursor.getInt(columnName: String): Int {
        return getInt(getColumnIndex(columnName))
    }

    private fun Cursor.getString(columnName: String): String {
        return getString(getColumnIndex(columnName))
    }

    private fun Cursor.getLong(columnName: String): Long {
        return getLong(getColumnIndex(columnName))
    }
}