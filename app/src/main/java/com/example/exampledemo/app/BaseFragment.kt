package com.example.exampledemo.app

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<BD : ViewDataBinding, VM : BaseViewModel> : Fragment() {
    private var rootView: View? = null
    protected lateinit var mBinding: BD
    protected lateinit var  mViewModel: VM

    private var binded: Boolean = false

    abstract fun getLayoutID(): Int

    abstract fun getViewModelClass(): Class<VM>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, getLayoutID(), container, false)
            mViewModel = ViewModelProvider(this).get(getViewModelClass())
            mBinding.lifecycleOwner = this

            rootView = mBinding.root

        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!binded) {
            onBinding()
            binded = true
        }

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return@setOnKeyListener onBackPressed()
            }

            false
        }
    }

    open fun onBinding() {

    }

    open fun onBackPressed(): Boolean {
        return false
    }


}