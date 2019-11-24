package com.example.exampledemo.ui.main

import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.common.Constants
import com.example.exampledemo.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    override fun getLayoutID(): Int {
        return R.layout.fragment_main
    }

    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun onBinding() {
        mBinding.mainViewModel = mViewModel

        val finalHost = NavHostFragment.create(R.navigation.nav_top)

        childFragmentManager.beginTransaction()
            .replace(R.id.nav_top_host_fragment, finalHost)
            .setPrimaryNavigationFragment(finalHost)
            .commit()

        mBinding.bottomNavigation.setLifecycleOwner(this)

        bottom_navigation.setOnSelectedChange {
            when (it) {
                Constants.TAB_HOME -> {
                    activity?.findNavController(R.id.nav_top_host_fragment)
                        ?.navigate(
                            R.id.homeFragment, null, NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(R.id.homeFragment, true)
                                .build()
                        )
                }

                Constants.TAB_SEARCH -> {
                    activity?.findNavController(R.id.nav_top_host_fragment)
                        ?.navigate(R.id.searchFragment)
                }

                Constants.TAB_PROFILE -> {
                    activity?.findNavController(R.id.nav_top_host_fragment)
                        ?.navigate(R.id.searchFragment)
                }

                else -> return@setOnSelectedChange
            }
        }
    }

    override fun onBackPressed(): Boolean {
        val mainDes: Int? =
            activity?.findNavController(R.id.nav_main_host_fragment)?.currentDestination?.id

        val topDes: Int? =
            activity?.findNavController(R.id.nav_top_host_fragment)?.currentDestination?.id

        if (mainDes == R.id.mainFragment && (topDes == R.id.searchFragment)) {
            bottom_navigation.setSelected(Constants.TAB_HOME)
            return true
        }

        return false
    }
}
