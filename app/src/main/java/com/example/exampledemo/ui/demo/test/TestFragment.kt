package com.example.exampledemo.ui.demo.test

import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.databinding.FragmentTestBinding

class TestFragment : BaseFragment<FragmentTestBinding, TestViewModel>() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_test
    }

    override fun getViewModelClass(): Class<TestViewModel> {
        return TestViewModel::class.java
    }

    override fun onBinding() {
        mBinding.viewModel = mViewModel
    }

}
