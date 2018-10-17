package com.jrafika.jrafika.task.specification

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.ImageGrayscaler
import com.jrafika.jrafika.core.ImageSpecification
import com.jrafika.jrafika.core.ImageStretcher
import com.jrafika.jrafika.task.histogram.ImageTask
import kotlinx.android.synthetic.main.histogram_layout.*

class HistogramSpecificationActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.histogram_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.histogram_specification_title)

        val grayscaledImageFragment = ImageResultFragment()
        val resultImageHistogram = ImageResultFragment()
        val optionFragment = HistogramSpecificationOptionFragment()

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment, ImageGrayscaler()).execute(it)
            histogramViewPager.setCurrentItem(1)
        }

        optionFragment.proceedFunction = { targetHistogram ->
            val img = grayscaledImageFragment.getImage()
            if (img != null) {
                ImageTask(
                        resultImageHistogram,
                        ImageSpecification(targetHistogram.toTypedArray())
                ).execute(img)
                histogramViewPager.setCurrentItem(3)
            }
        }

        histogramViewPager.offscreenPageLimit = 4
        histogramViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        optionFragment,
                        resultImageHistogram
                )[p0]
            }
            override fun getCount(): Int {
                return 4
            }
        }
        histogramViewPager.setCurrentItem(0)
    }
}
