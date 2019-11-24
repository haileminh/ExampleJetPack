package com.example.exampledemo.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.exampledemo.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (nav_main_host_fragment.childFragmentManager.fragments.size == 1) {
            val frag = nav_main_host_fragment.childFragmentManager.fragments[0]
            if (frag is MainFragment && frag.onBackPressed()) {
                return
            }
        }
    }
}
