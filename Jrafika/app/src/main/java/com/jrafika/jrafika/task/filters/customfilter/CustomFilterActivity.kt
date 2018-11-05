package com.jrafika.jrafika.task.filters.customfilter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.processor.Convoluter
import com.jrafika.jrafika.processor.ImageGrayscaler
import com.jrafika.jrafika.task.histogram.ImageTask
import kotlinx.android.synthetic.main.task_layout.*

class CustomFilterActivity: BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.custom_filter_title)

        val importImageFragment = ImportImageFragment()
        val grayscaledImageFragment = ImageResultFragment()
        val customFilterOptionFragment = CustomFilterOptionFragment()
        val resultImageFragment = ImageResultFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment, ImageGrayscaler())
                    .execute(it)
            grayscaledImageFragment.imageLoadedListener = {
                val image = it
                customFilterOptionFragment.proceedFunction = {
                    val kernel = arrayOf(
                            floatArrayOf(it[0], it[1], it[2]),
                            floatArrayOf(it[3], it[4], it[5]),
                            floatArrayOf(it[6], it[7], it[8])
                    )
                    ImageTask(resultImageFragment, Convoluter(kernel)).execute(image)
                    taskViewPager.setCurrentItem(3)
                }
            }
            taskViewPager.setCurrentItem(2)
        }

        taskViewPager.offscreenPageLimit = 4
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        customFilterOptionFragment,
                        resultImageFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 4
            }
        }
        taskViewPager.setCurrentItem(0)
    }

}