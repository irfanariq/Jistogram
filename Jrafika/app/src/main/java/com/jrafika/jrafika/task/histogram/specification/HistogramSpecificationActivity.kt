package com.jrafika.jrafika.task.histogram.specification

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.ImageGrayscaler
import com.jrafika.jrafika.core.ImageSpecification
import com.jrafika.jrafika.task.histogram.DisplayHistogramFragment
import com.jrafika.jrafika.task.histogram.ImageTask
import kotlinx.android.synthetic.main.task_layout.*

class HistogramSpecificationActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.histogram_specification_title)

        val grayscaledImageFragment = ImageResultFragment()
        val originalHistogramFragment = DisplayHistogramFragment.newInstance(
                resources.getString(R.string.original_histogram)
        )
        val optionFragment = HistogramSpecificationOptionFragment()
        val resultImageHistogram = ImageResultFragment()
        val specifiedlHistogramFragment = DisplayHistogramFragment.newInstance(
                resources.getString(R.string.specified_histogram)
        )

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment, ImageGrayscaler(), listOf(originalHistogramFragment)).execute(it)
            taskViewPager.setCurrentItem(1)
        }

        optionFragment.proceedFunction = { targetHistogram ->
            val img = grayscaledImageFragment.getImage()
            if (img != null) {
                ImageTask(
                        resultImageHistogram,
                        ImageSpecification(targetHistogram.toTypedArray()),
                        listOf(specifiedlHistogramFragment)
                ).execute(img)
                taskViewPager.setCurrentItem(4)
            }
        }

        taskViewPager.offscreenPageLimit = 6
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        originalHistogramFragment,
                        optionFragment,
                        resultImageHistogram,
                        specifiedlHistogramFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 6
            }
        }
        taskViewPager.setCurrentItem(0)
    }
}
