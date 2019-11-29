package com.example.exampledemo.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.app.SakeDialog
import com.example.exampledemo.common.Constants
import com.example.exampledemo.databinding.FragmentCameraBinding
import com.example.exampledemo.ui.ShareViewModel
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors


class CameraFragment : BaseFragment<FragmentCameraBinding, CameraViewModel>(), LifecycleOwner {

    private var mPermissionCameraDialog: SakeDialog? = null
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val executor = Executors.newSingleThreadExecutor()
    private var mImageCapture: ImageCapture? = null

    private lateinit var mPreview: Preview

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

        ShareViewModel.get(activity!!).apply {
            selectedFile.value = null
            selectedFile.removeObserver(onSelectedFile)
            selectedFile.observe(this@CameraFragment, onSelectedFile)
        }

        mViewModel.action.observe(this, Observer { action ->
            if (action == CameraViewModel.TAKE_PHOTO_STATE) {
                takePicture()
                mViewModel.action.value = Constants.ACTION_NONE
            }
        })


        // Request camera permissions
        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                activity!!,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
            mPreviewWidth = right - left
            mPreviewHeight = bottom - top

            if (mPreviewWidth > 0 && mPreviewHeight > 0) {
                //TODO
            }

            updateTransform()
        }

    }

    override fun onResume() {
        super.onResume()

        if (mPreviewWidth > 0 && mPreviewHeight > 0) {
            viewFinder.post { startCamera() }
        }
    }

    private fun startCamera() {
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
                "CameraPreview View ${viewFinder.width}x${viewFinder.height}"
            )

            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
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

    private fun takePicture() {
//        val file = File(
//            context?.externalCacheDir,
//            "${System.currentTimeMillis()}.jpg"
//        )
//        mImageCapture?.takePicture(file, executor, object : ImageCapture.OnImageSavedListener {
//
//            override fun onImageSaved(file: File) {
//                val msg = "Photo capture succeeded: ${file.absolutePath}"
//                Log.d("CameraXApp", msg)
//                viewFinder.post {
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onError(
//                imageCaptureError: ImageCapture.ImageCaptureError,
//                message: String,
//                cause: Throwable?
//            ) {
//                val msg = "Photo capture failed: $message"
//                Log.e("CameraXApp", msg, cause)
//                viewFinder.post {
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        })

        mImageCapture?.takePicture(executor, object : ImageCapture.OnImageCapturedListener() {
            override fun onCaptureSuccess(image: ImageProxy?, rotationDegrees: Int) {

                image?.image?.apply {
                    val buffer = planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    // route image
                    if (rotationDegrees != 0) {
                        val rotationMatrix = Matrix()
                        rotationMatrix.postRotate(rotationDegrees.toFloat())
                        bitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.width,
                            bitmap.height,
                            rotationMatrix,
                            true
                        )

                    }

                    val deltaHeight: Int
                    // crop image
                    val newWidth: Int
                    val newHeight: Int
                    if (mPreviewWidth.toFloat() / mPreviewHeight > bitmap.width.toFloat() / bitmap.height) {
                        newWidth = bitmap.width
                        newHeight = bitmap.width * mPreviewHeight / mPreviewWidth
                        deltaHeight =
                            resources.displayMetrics.densityDpi * 50 * bitmap.width / mPreviewWidth / 160
                    } else {
                        newWidth = bitmap.height * mPreviewWidth / mPreviewHeight
                        newHeight = bitmap.height
                        deltaHeight =
                            resources.displayMetrics.densityDpi * 50 * bitmap.height / mPreviewHeight / 160
                    }
                    bitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        newWidth,
                        newHeight - deltaHeight
                    )

                    val file = File.createTempFile("sakeist", null)

                    val fOut = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    fOut.flush()
                    fOut.close()

                    Log.d(
                        Constants.APP_NAME,
                        "Save Image ${newWidth}x${newHeight - deltaHeight} TO Temporary ${file.absolutePath} ${file.length() / 1024f / 1024f}kB"
                    )

                    Log.d("FILE ", "====> $file")
//                    mViewModel.preview(file)

                }
                image?.close()

            }

            override fun onError(
                imageCaptureError: ImageCapture.ImageCaptureError,
                message: String,
                cause: Throwable?
            ) {
                Log.d(Constants.APP_NAME, "Take Picture has ERROR $message", cause)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        CameraX.unbindAll()
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    private fun updateTransform(po: Preview.PreviewOutput) {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
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

        viewFinder.setTransform(matrix)
    }


    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context!!, it) == PackageManager.PERMISSION_GRANTED
    }


    private val onSelectedFile = Observer<File?> { file ->
        if (file != null) {
            mViewModel.preview(file)
        }
    }

    override fun onBackPressed(): Boolean {
        if (mViewModel.viewState.value == CameraViewModel.TOOK_PHOTO_STATE) {
            mViewModel.onClosePreview()
            return true
        }
        return super.onBackPressed()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}

