package com.example.exampledemo.ui.demo.country

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.common.Constants
import com.example.exampledemo.databinding.FragmentCountryBinding
import com.example.exampledemo.ui.camera.CountryAdapter

class CountryFragment : BaseFragment<FragmentCountryBinding, CountryViewModel>() {

    private val mAdapter = CountryAdapter()

    override fun getLayoutID(): Int {
        return R.layout.fragment_country
    }

    override fun getViewModelClass(): Class<CountryViewModel> {
        return CountryViewModel::class.java
    }

    override fun onBinding() {
        mBinding.viewModel = mViewModel

        context?.let {
            mViewModel.loadData(it)
        }

        mBinding.recyclerView.adapter = mAdapter

        mViewModel.countriesLiveData.observe(this, Observer { list ->
            list.let {
                mAdapter.updateList(list)
                configEventTouchAlphabet(mAdapter.getAlphabets())
            }
        })

//        mBinding.recyclerView.addItemDecoration(HeaderItemDecoration(mBinding.recyclerView) { index ->
//            mAdapter.isHeader(index)
//        })
    }

    private var alphabetLineHeight = 0
    private var currentIndex = -1
    @SuppressLint("ClickableViewAccessibility")
    private fun configEventTouchAlphabet(list: ArrayList<String>) {
        var text = "#"
        list.forEach { txt ->
            text = "$text\n$txt"
        }
        mBinding.tvAlphabet.text = text
        val bounds = Rect()
        mBinding.tvAlphabet.getLineBounds(1, bounds)

        alphabetLineHeight = mBinding.tvAlphabet.lineHeight

        mBinding.tvAlphabet.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                currentIndex = -1
            }
            val index = (motionEvent.y / alphabetLineHeight).toInt() - 1
            if (index != currentIndex) {
                currentIndex = index
                mAdapter.getPositionOfAlphabet(index)?.let { position ->
                    Log.d(
                        Constants.APP_NAME,
                        "POSITION $position, $index ${mAdapter.getAlphabets()[index]}"
                    )
                    (mBinding.recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                        position,
                        0
                    )
                }
            }
            return@setOnTouchListener true
        }
    }


}
