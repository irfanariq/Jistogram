package com.jrafika.jrafika.task.ocr.skeleton

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.*
import com.jrafika.jrafika.task.histogram.ImageTask
import com.jrafika.jrafika.task.ocr.ImageBinaryOptionFragment
import kotlinx.android.synthetic.main.task_layout.*

class ImageSkeletonActivity: BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(getString(R.string.skeleton_title))

        val grayscaledImageFragment = ImageResultFragment()
        val blackWhiteOptionFragment = ImageBinaryOptionFragment()
        val thinnedImage = ImageResultFragment()
        val predictionImageFragment = ImageResultFragment()

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment, ImageGrayscaler())
                    .then(ImageTask(blackWhiteOptionFragment, ImageBitmapper(125)))
                    .execute(it)
            taskViewPager.setCurrentItem(2)
        }

        blackWhiteOptionFragment.proceedFunction = { bwImage ->
            ImageTask(thinnedImage, ImageNoiseRemover())
                    .then(ImageTask(thinnedImage, ImageThinningDisplayer()))
                    .execute(bwImage)

            ImageTask(predictionImageFragment, ImageNoiseRemover())
                    .then(ImageTask(predictionImageFragment, ImageThinner()))
                    .then(ImageTask(predictionImageFragment, SkeletonPredictor()))
                    .execute(bwImage)
            taskViewPager.setCurrentItem(3)
        }

        taskViewPager.offscreenPageLimit = 5
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        blackWhiteOptionFragment,
                        thinnedImage,
                        predictionImageFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 5
            }
        }
        taskViewPager.setCurrentItem(0)
    }
}