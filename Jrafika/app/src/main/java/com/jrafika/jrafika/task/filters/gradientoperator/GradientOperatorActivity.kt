package com.jrafika.jrafika.task.filters.gradientoperator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.processor.*
import com.jrafika.jrafika.task.filters.medianfilter.MedianFilterOptionFragment
import com.jrafika.jrafika.task.histogram.ImageTask
import kotlinx.android.synthetic.main.task_layout.*

class GradientOperatorActivity: BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.gradient_operator_title)

        val importImageFragment = ImportImageFragment()
        val sobelHorizontalFragment = ImageResultFragment()
        val sobelVerticalFragment = ImageResultFragment()
        val laplacianFragment = ImageResultFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(sobelHorizontalFragment, SobelHorizontalFilterer())
                    .then(ImageTask(sobelHorizontalFragment, SobelHorizontalFilterer()))
                    .then(ImageTask(sobelVerticalFragment, SobelVerticalFilterer()))
                    .then(ImageTask(laplacianFragment, LaplacianFilterer()))
                    .execute(it)
            taskViewPager.setCurrentItem(1)
        }

        taskViewPager.offscreenPageLimit = 4
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        sobelHorizontalFragment,
                        sobelVerticalFragment,
                        laplacianFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 4
            }
        }
        taskViewPager.setCurrentItem(0)
    }

}