package com.jrafika.jrafika.task.histogram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import kotlinx.android.synthetic.main.histogram_layout.*

class HistogramActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.histogram_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val redHistogramFragment = RedHistogramFragment()
        val greenHistogramFragment = GreenHistogramFragment()
        val blueHistogramFragment = BlueHistogramFragment()

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            HistogramCalculationTask(
                    redHistogramFragment,
                    greenHistogramFragment,
                    blueHistogramFragment
            ).execute(it)
        }

        histogramViewPager.offscreenPageLimit = 5
        histogramViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        redHistogramFragment,
                        greenHistogramFragment,
                        blueHistogramFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 4
            }
        }
        histogramViewPager.setCurrentItem(0)
    }
}
