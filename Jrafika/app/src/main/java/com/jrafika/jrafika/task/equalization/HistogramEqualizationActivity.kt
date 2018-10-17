package com.jrafika.jrafika.task.histogram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.task.equalization.EqualizedHistogramFragment
import com.jrafika.jrafika.task.equalization.GrayscaledImageFragment
import kotlinx.android.synthetic.main.histogram_layout.*

class HistogramEqualizationActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.histogram_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val grayscaledImageFragment = GrayscaledImageFragment()
        val equalizedHistogramFragment = EqualizedHistogramFragment()

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            HistogramEqualizationCalculationTask(
                    grayscaledImageFragment,
                    equalizedHistogramFragment
            ).execute(it)
        }

        histogramViewPager.offscreenPageLimit = 3
        histogramViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        equalizedHistogramFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 3
            }
        }
        histogramViewPager.setCurrentItem(0)
    }
}
