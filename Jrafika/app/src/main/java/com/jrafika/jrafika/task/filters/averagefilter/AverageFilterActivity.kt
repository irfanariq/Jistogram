package com.jrafika.jrafika.task.filters.averagefilter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.processor.AverageFilterer
import com.jrafika.jrafika.processor.ImageGrayscaler
import com.jrafika.jrafika.task.histogram.ImageTask
import kotlinx.android.synthetic.main.task_layout.*

class AverageFilterActivity: BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.average_filter_title)

        val importImageFragment = ImportImageFragment()
        val grayscaledImageFragment = ImageResultFragment()
        val resultImageFragment = ImageResultFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment, ImageGrayscaler())
                    .then(ImageTask(resultImageFragment, AverageFilterer()))
                    .execute(it)
            taskViewPager.setCurrentItem(2)
        }

        taskViewPager.offscreenPageLimit = 3
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        resultImageFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 3
            }
        }
        taskViewPager.setCurrentItem(0)
    }

}