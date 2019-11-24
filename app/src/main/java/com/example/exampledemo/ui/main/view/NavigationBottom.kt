package com.example.exampledemo.ui.main.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.exampledemo.R
import com.example.exampledemo.common.Constants
import com.example.exampledemo.databinding.ViewBottomNavigationBinding

class NavigationBottom : ConstraintLayout {

    private lateinit var binding: ViewBottomNavigationBinding
    private val viewModel = NavigationBottomViewModel()


    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        binding.lifecycleOwner = lifecycleOwner
    }

    private fun init(attrs: AttributeSet?) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.view_bottom_navigation,
            this,
            true
        )
        binding.viewModel = viewModel
    }


    fun setOnSelectedChange(listener: (position: Int) -> Unit) {
        viewModel.onSelectedChange = listener
    }

    fun setSelected(index: Int) {
        viewModel.currentTab.value = index
        viewModel.onSelectedChange?.let { tabSelected -> tabSelected(index) }
    }

    internal class NavigationBottomViewModel : ViewModel() {
        var onSelectedChange: ((Int) -> Unit)? = null
        val currentTab = MutableLiveData(0)

        fun onSelect(v: View) {
            val newTab = mapId[v.id] ?: Constants.TAB_HOME
//            if (newTab == currentTab.value && newTab != Constants.TAB_HOME) return

            when (newTab) {
                Constants.TAB_HOME -> {
                    currentTab.value = Constants.TAB_HOME
                    onSelectedChange?.let { tabSelected -> tabSelected(Constants.TAB_HOME) }
                }

                Constants.TAB_SEARCH -> {
                    currentTab.value = Constants.TAB_SEARCH
                    onSelectedChange?.let { tabSelected -> tabSelected(Constants.TAB_SEARCH) }
                }

                Constants.TAB_PROFILE -> {
                    currentTab.value = Constants.TAB_PROFILE
                    onSelectedChange?.let { tabSelected -> tabSelected(Constants.TAB_PROFILE) }
                }

                Constants.TAB_CAMERA -> {
                    Navigation.findNavController(v)
                        .navigate(R.id.action_mainFragment_to_cameraFragment)
                }
            }
        }

        companion object {
            @SuppressLint("UseSparseArrays")
            val mapId = HashMap<Int, Int>()

            init {
                mapId[R.id.item_home] = Constants.TAB_HOME
                mapId[R.id.item_search] = Constants.TAB_SEARCH
                mapId[R.id.item_camera] = Constants.TAB_CAMERA
                mapId[R.id.item_quiz] = Constants.TAB_QUIZ
                mapId[R.id.item_profile] = Constants.TAB_PROFILE
            }
        }
    }
}
