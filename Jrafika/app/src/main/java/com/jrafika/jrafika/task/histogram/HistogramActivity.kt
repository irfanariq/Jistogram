package com.jrafika.jrafika.task.histogram

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.processor.ImageGrayscaler
import kotlinx.android.synthetic.main.task_layout.*

class HistogramActivity : BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

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
            taskViewPager.setCurrentItem(1)
        }

        taskViewPager.offscreenPageLimit = 7
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
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
        taskViewPager.setCurrentItem(0)
    }
}
