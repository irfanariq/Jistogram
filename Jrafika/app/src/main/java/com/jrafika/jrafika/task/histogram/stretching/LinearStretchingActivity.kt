package com.jrafika.jrafika.task.histogram.stretching

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.processor.ImageGrayscaler
import com.jrafika.jrafika.processor.ImageStretcher
import com.jrafika.jrafika.task.histogram.DisplayHistogramFragment
import com.jrafika.jrafika.task.histogram.ImageTask
import kotlinx.android.synthetic.main.task_layout.*

class LinearStretchingActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.linear_streching_title)

        val grayscaledImageFragment = ImageResultFragment()
        val grayscaledHistogramFragment = DisplayHistogramFragment.newInstance(resources.getString(R.string.grayscaled_histogram))
        val stretchedImageFragment = ImageResultFragment()
        val stretchedHistogramFragment = DisplayHistogramFragment.newInstance(getString(R.string.stretched_histogram))
        val optionFragment = LinearStretchingOptionFragment()

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment, ImageGrayscaler(), listOf(grayscaledHistogramFragment)).execute(it)
            taskViewPager.setCurrentItem(1)
        }

        optionFragment.proceedFunction = { inputMin, inputMax, outputMin, outputMax ->
            val img = grayscaledImageFragment.getImage()
            if (img != null) {
                ImageTask(
                        stretchedImageFragment,
                        ImageStretcher(
                                inputMin, inputMax, outputMin, outputMax
                        ),
                        listOf(stretchedHistogramFragment)
                ).execute(img)
                taskViewPager.setCurrentItem(5)
            }
        }

        taskViewPager.offscreenPageLimit = 6
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        grayscaledHistogramFragment,
                        optionFragment,
                        stretchedImageFragment,
                        stretchedHistogramFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 6
            }
        }
        taskViewPager.setCurrentItem(0)
    }
}
