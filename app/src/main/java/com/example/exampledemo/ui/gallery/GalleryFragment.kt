package com.example.exampledemo.ui.gallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.app.SakeDialog
import com.example.exampledemo.databinding.FragmentGalleryBinding
import com.example.exampledemo.db.Storage
import com.example.exampledemo.ui.ShareViewModel
import com.example.exampledemo.ui.gallery.entity.AssetEntity
import java.io.File

class GalleryFragment : BaseFragment<FragmentGalleryBinding, GalleryViewModel>() {

    private var mPermissionStorageDialog: SakeDialog? = null
    lateinit var sharedViewModel: ShareViewModel
    private val adapter = AssetEntityAdapter()

    override fun getLayoutID(): Int {
        return R.layout.fragment_gallery
    }

    override fun getViewModelClass(): Class<GalleryViewModel> {
        return GalleryViewModel::class.java
    }

    override fun onBinding() {
        super.onBinding()
        sharedViewModel = ShareViewModel.get(activity!!)

        mBinding.rcvGallery.layoutManager = GridLayoutManager(context, 3)
        mBinding.rcvGallery.adapter = adapter

        mBinding.rcvGallery.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, itemPosition: Int,
                parent: RecyclerView
            ) {
                resources.displayMetrics.densityDpi.let {
                    val small = (5 * it / 160)
                    val normal = (10 * it / 160)
                    val large = 15 * it / 160
                    when (itemPosition % 3) {
                        0 -> outRect.set(large, small, small, normal)
                        1 -> outRect.set(normal, small, normal, normal)
                        2 -> outRect.set(small, small, large, normal)
                    }
                }
            }
        })

        adapter.setOnSelectedListener(object : AssetEntityAdapter.OnSelectedListener {
            override fun onSelect(path: String) {
                sharedViewModel.selectedFile.value = File(path)
                findNavController().popBackStack()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        checkStoragePermission()
    }

    private fun checkStoragePermission() {
        mPermissionStorageDialog?.dismiss()
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mViewModel.initData(context!!)
            mViewModel.assetEntityList.observe(this, Observer<PagedList<AssetEntity>> {
                adapter.submitList(it)
            })
            return
        }

        if (!Storage.instance.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSIONS
            )
            return
        }

        showStoragePermissionPopup()
    }

    private fun showStoragePermissionPopup() {
        if (mPermissionStorageDialog == null) {

            mPermissionStorageDialog = SakeDialog()
                .setTitle(R.string.tit_request_storage_permission)
                .setMessage(R.string.msg_request_storage_permission)
                .setLeftButton(R.string.act_cancel, object : SakeDialog.OnClickListener {
                    override fun onClick() {
                        Navigation.findNavController(mBinding.root).popBackStack()
                    }

                })
                .setRightButton(R.string.act_setting, object : SakeDialog.OnClickListener {
                    override fun onClick() {
                        context?.let { ctx ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", ctx.packageName, null)
                            )
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                })
        }

        mPermissionStorageDialog?.show(childFragmentManager)
    }

    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSIONS = 20
    }

}
