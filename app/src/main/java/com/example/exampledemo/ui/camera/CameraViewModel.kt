package com.example.exampledemo.ui.camera

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseViewModel
import java.io.File

class CameraViewModel : BaseViewModel() {

    val viewState = MutableLiveData<Int>(HELP_STATE)
    val file = MutableLiveData<File>()
    var preState = HELP_STATE


    fun onShowHelp() {
        preState = HELP_STATE
        viewState.value = HELP_STATE
    }

    fun onCloseHelp() {
        preState = PREVIEW_STATE
        viewState.value = PREVIEW_STATE
    }

    fun onClosePreview() {
        viewState.value = preState
    }

    fun preview(file: File) {
        viewState.value = TOOK_PHOTO_STATE
        this.file.value = file
    }

    fun onTakePhoto() {
        if (viewState.value != PREVIEW_STATE || action.value == TAKE_PHOTO_STATE) return
        action.value = TAKE_PHOTO_STATE
    }

    fun onChoosePicture(v: View) {
        Navigation.findNavController(v).navigate(R.id.galleryFragment)
    }


    companion object {
        const val HELP_STATE = 10
        const val PREVIEW_STATE = 11
        const val TAKE_PHOTO_STATE = 12
        const val TOOK_PHOTO_STATE = 13
    }
}
