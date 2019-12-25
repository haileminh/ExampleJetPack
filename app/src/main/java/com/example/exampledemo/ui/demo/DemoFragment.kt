package com.example.exampledemo.ui.demo

import androidx.navigation.Navigation
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.databinding.FragmentDemoBinding
import kotlinx.android.synthetic.main.fragment_demo.*

class DemoFragment : BaseFragment<FragmentDemoBinding, DemoViewModel>() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_demo
    }

    override fun getViewModelClass(): Class<DemoViewModel> {
        return DemoViewModel::class.java
    }

    override fun onBinding() {
        btnCanvas.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_demoFragment_to_drawFragment)
        }

        btnCountry.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_demoFragment_to_countryFragment)
        }
    }
}
