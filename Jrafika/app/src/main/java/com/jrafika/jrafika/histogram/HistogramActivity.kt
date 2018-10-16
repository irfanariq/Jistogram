package com.jrafika.jrafika.histogram

import android.os.Bundle
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.R
import kotlinx.android.synthetic.main.histogram_layout.*

class HistogramActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.histogram_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        histogramViewPager.offscreenPageLimit = 5
        val swipeAdapter = HistogramSwipeAdapter(supportFragmentManager)
        histogramViewPager.adapter = swipeAdapter
        histogramViewPager.setCurrentItem(0)
    }
}
