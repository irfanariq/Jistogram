package com.jrafika.jrafika.task.histogram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.ImageEqualizer
import com.jrafika.jrafika.core.ImageGrayscaler
import kotlinx.android.synthetic.main.histogram_layout.*

class HistogramEqualizationActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.histogram_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.histogram_equalization_title)

        val grayscaledImageFragment = ImageResultFragment()
        val grayscaledHistogramFragment = DisplayHistogramFragment.newInstance(resources.getString(R.string.grayscaled_histogram))
        val equalizedImageFragment = ImageResultFragment()
        val equalizedHistogramFragment = DisplayHistogramFragment.newInstance(getString(R.string.equalized_histogram))

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment,ImageGrayscaler(), listOf(grayscaledHistogramFragment))
                    .then(ImageTask(equalizedImageFragment, ImageEqualizer(), listOf(equalizedHistogramFragment)))
                    .execute(it)
            histogramViewPager.setCurrentItem(1)
        }

        histogramViewPager.offscreenPageLimit = 5
        histogramViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        grayscaledHistogramFragment,
                        equalizedImageFragment,
                        equalizedHistogramFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 5
            }
        }
        histogramViewPager.setCurrentItem(0)
    }
}
