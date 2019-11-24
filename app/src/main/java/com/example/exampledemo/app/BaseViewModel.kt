package com.example.exampledemo.app

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exampledemo.common.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {

    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private var latestClickTime: Long = 0
    val action: MutableLiveData<Int> = MutableLiveData(Constants.ACTION_NONE)

    /**
     * Check multi click
     */
    fun checkClicked(): Boolean {
        if (SystemClock.elapsedRealtime() - latestClickTime < 600) return true
        latestClickTime = SystemClock.elapsedRealtime()
        return false
    }

    /**
     * Add subscribed to CompositeDisposable Manager
     */
    fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun onCleared() {
        mCompositeDisposable.clear()
        super.onCleared()
    }
}
