package com.jrafika.jrafika.task.histogram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.ImageGrayscaler
import kotlinx.android.synthetic.main.histogram_layout.*

class HistogramActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.histogram_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.histogram_title)

        val redHistogramFragment = DisplayHistogramFragment.newInstance(resources.getString(R.string.red_histogram))
        val greenHistogramFragment = DisplayHistogramFragment.newInstance(resources.getString(R.string.green_histogram))
        val blueHistogramFragment = DisplayHistogramFragment.newInstance(resources.getString(R.string.blue_histogram))
        val grayscaledImageFragment = ImageResultFragment()
        val grayscaledHistogramFragment = DisplayHistogramFragment.newInstance(resources.getString(R.string.grayscaled_histogram))

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(null, null, listOf(
                    redHistogramFragment,
                    greenHistogramFragment,
                    blueHistogramFragment
            )).then(ImageTask(
                    grayscaledImageFragment,
                    ImageGrayscaler(),
                    listOf(grayscaledHistogramFragment)
            )).execute(it)
            histogramViewPager.setCurrentItem(1)
        }

        histogramViewPager.offscreenPageLimit = 7
        histogramViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        redHistogramFragment,
                        greenHistogramFragment,
                        blueHistogramFragment,
                        grayscaledImageFragment,
                        grayscaledHistogramFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 6
            }
        }
        histogramViewPager.setCurrentItem(0)
    }
}
