package com.example.exampledemo.ui

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File

class ShareViewModel : ViewModel() {
    val selectedFile = MutableLiveData<File?>(null)

    companion object {
        fun get(act: FragmentActivity): ShareViewModel {
            return ViewModelProvider(act).get(ShareViewModel::class.java)
        }
    }
}