package com.example.exampledemo.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.exampledemo.databinding.ItemAssetEntityBinding
import com.example.exampledemo.ui.gallery.entity.AssetEntity
import java.io.File

class AssetEntityAdapter :
    PagedListAdapter<AssetEntity, AssetEntityAdapter.AssetEntityViewHolder>(DIFF_CALLBACK) {

    var listener: OnSelectedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetEntityViewHolder {
        return AssetEntityViewHolder(
            ItemAssetEntityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AssetEntityViewHolder, position: Int) {
        holder.bindTo(getItem(position), this.listener)
    }

    fun setOnSelectedListener(listener: OnSelectedListener?) {
        this.listener = listener
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<AssetEntity>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(
                oldConcert: AssetEntity,
                newConcert: AssetEntity
            ) = oldConcert.id == newConcert.id

            override fun areContentsTheSame(
                oldConcert: AssetEntity,
                newConcert: AssetEntity
            ) = oldConcert == newConcert
        }
    }

    class AssetEntityViewHolder(val binding: ItemAssetEntityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(entity: AssetEntity?, listener: OnSelectedListener?) {
            entity?.path?.let { path ->
                Glide.with(binding.ivThumb)
                    .load(File(path))
                    .into(binding.ivThumb)
                binding.assetEntityItem.setOnClickListener {
                    listener?.onSelect(path)
                }
            }
        }
    }

    interface OnSelectedListener {
        fun onSelect(path: String)
    }
}