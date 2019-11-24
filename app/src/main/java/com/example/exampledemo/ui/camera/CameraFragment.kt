package com.example.exampledemo.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.app.SakeDialog
import com.example.exampledemo.common.Constants
import com.example.exampledemo.databinding.FragmentCameraBinding
import com.example.exampledemo.db.Storage
import kotlinx.android.synthetic.main.fragment_camera.*


class CameraFragment : BaseFragment<FragmentCameraBinding, CameraViewModel>() {

    private var mPermissionCameraDialog: SakeDialog? = null
    private lateinit var mPreview: Preview
    private var mImageCapture: ImageCapture? = null

    private var mPreviewWidth = 0
    private var mPreviewHeight = 0

    override fun getLayoutID(): Int {
        return R.layout.fragment_camera
    }

    override fun getViewModelClass(): Class<CameraViewModel> {
        return CameraViewModel::class.java
    }

    override fun onBinding() {
        mBinding.viewModel = mViewModel

        mViewModel.action.observe(this, Observer { action ->
            if (action == CameraViewModel.TAKE_PHOTO_STATE) {
                takePicture()
                mViewModel.action.value = Constants.ACTION_NONE
            }
        })

        cameraPreview.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            mPreviewWidth = right - left
            mPreviewHeight = bottom - top

            if (mPreviewWidth > 0 && mPreviewHeight > 0) {
                checkCameraPermission()
            }
        }
    }

    private fun takePicture() {

    }

    override fun onResume() {
        super.onResume()
        if (mPreviewWidth > 0 && mPreviewHeight > 0) {
            checkCameraPermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraX.unbindAll()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_CODE_CAMERA_PERMISSIONS) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        Storage.instance.saveShouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        mPermissionCameraDialog?.dismiss()
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupCamera()
            return
        }

        if (!Storage.instance.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA_PERMISSIONS)
            return
        }

        showCameraPermissionPopup()
    }

    private fun setupCamera() {
        Log.d(Constants.APP_NAME, "setup Camera")
        if (context == null) return
        CameraX.unbindAll()
        CameraX.ErrorListener { error, message ->
            Log.d(Constants.APP_NAME, "CameraX has an error $error $message")
        }
        if (!CameraX.hasCameraWithLensFacing(CameraX.LensFacing.BACK)) return

        // Preview
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.BACK)
            setTargetResolution(Size(mPreviewWidth, mPreviewHeight))
        }.build()

        mPreview = Preview(previewConfig)
        mPreview.setOnPreviewOutputUpdateListener {

            Log.d(
                Constants.APP_NAME,
                "CameraPreview Texture size ${it.textureSize} Ratio ${Rational(
                    it.textureSize.width,
                    it.textureSize.height
                )}"
            )
            Log.d(
                Constants.APP_NAME,
                "CameraPreview View ${cameraPreview.width}x${cameraPreview.height}"
            )

            val parent = cameraPreview.parent as ViewGroup
            parent.removeView(cameraPreview)
            parent.addView(cameraPreview, 0)

            cameraPreview.surfaceTexture = it.surfaceTexture
            updateTransform(it)
        }

        // Capture
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setLensFacing(CameraX.LensFacing.BACK)
//                setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                setTargetResolution(Size(mPreviewWidth, mPreviewHeight))
            }.build()

        // Build the image capture use case and attach button click listener
        mImageCapture = ImageCapture(imageCaptureConfig)

        CameraX.bindToLifecycle(this, mPreview, mImageCapture)
    }

    private fun updateTransform(po: Preview.PreviewOutput) {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = cameraPreview.width / 2f
        val centerY = cameraPreview.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (cameraPreview.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        val width: Int
        val height: Int
        if (po.rotationDegrees == Surface.ROTATION_0 || po.rotationDegrees == Surface.ROTATION_180) {
            width = po.textureSize.width
            height = po.textureSize.height
        } else {
            width = po.textureSize.height
            height = po.textureSize.width
        }

        Log.d(
            Constants.APP_NAME,
            "PREVIEW FROM ${width}x$height TO ${mPreviewWidth}x$mPreviewHeight | rotationDegrees: $rotationDegrees"
        )

        if (mPreviewWidth.toFloat() / mPreviewHeight > width.toFloat() / height) {
            // scale HEIGHT
            // ratio = desired height / real height
            // desiredHeight = height * mPreviewWidth / width
            matrix.postScale(1.0f, height.toFloat() * mPreviewWidth / width / mPreviewHeight)
        } else {
            // scale WIDTH
            // ratio = desired width / real width
            // desiredWith = width * mPreviewHeight / height
            matrix.postScale(width.toFloat() * mPreviewHeight / height / mPreviewWidth, 1.0f)
        }

        cameraPreview.setTransform(matrix)
    }

    private fun showCameraPermissionPopup() {
        if (mPermissionCameraDialog == null) {

            mPermissionCameraDialog = SakeDialog()
                .setTitle(R.string.tit_request_camera_permission)
                .setMessage(R.string.msg_request_camera_permission)
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

        mPermissionCameraDialog?.show(childFragmentManager)
    }

    override fun onBackPressed(): Boolean {
        if (mViewModel.viewState.value == CameraViewModel.TOOK_PHOTO_STATE) {
            mViewModel.onClosePreview()
            return true
        }
        return super.onBackPressed()
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
    }
}
