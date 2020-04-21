package com.example.exampledemo.ui.demo

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.widget.PopupWindowCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.databinding.FragmentDemoBinding
import com.example.exampledemo.databinding.PopupListDataBinding
import com.example.exampledemo.model.Country
import com.example.exampledemo.ui.demo.popupwindow.PopupWindowAdapter
import kotlinx.android.synthetic.main.fragment_demo.*

class DemoFragment : BaseFragment<FragmentDemoBinding, DemoViewModel>() {

    private var listCountries = ArrayList<Country>()


    override fun getLayoutID(): Int {
        return R.layout.fragment_demo
    }

    override fun getViewModelClass(): Class<DemoViewModel> {
        return DemoViewModel::class.java
    }

    override fun onBinding() {
        mBinding.viewModel = mViewModel

        btnCanvas.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_demoFragment_to_chatFragment)
        }

        btnCountry.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_demoFragment_to_countryFragment)
        }

        btnDemo.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_demoFragment_to_eggTimerFragment2)
        }

        context?.let {
            mViewModel.loadData(it)
        }

        mViewModel.countriesLiveData.observe(this, Observer { list ->
            list.let {
                this.listCountries = list
            }
        })

        btnPopupWindow.setOnClickListener {
            val binding = PopupListDataBinding.inflate(LayoutInflater.from(it.context))
            val popup = PopupWindow(
                binding.root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                it.context.resources.displayMetrics.densityDpi * 500 / 160,
                true
            )
            val rcvCountry = binding.rcvCountry
            val popupWindowAdapter = PopupWindowAdapter(listCountries)
            val manager =
                LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration =
                DividerItemDecoration(it.context, manager.orientation)
            rcvCountry.apply {
                layoutManager = manager
                addItemDecoration(dividerItemDecoration)
                adapter = popupWindowAdapter
            }

            popup.isOutsideTouchable = true
            PopupWindowCompat.showAsDropDown(popup, it, 0, 0, Gravity.CENTER)
        }
    }
}
