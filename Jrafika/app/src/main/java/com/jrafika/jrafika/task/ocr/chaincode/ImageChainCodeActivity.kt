package com.jrafika.jrafika.task.ocr.chaincode

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.processor.ChainCodePredictor
import com.jrafika.jrafika.processor.ImageBitmapper
import com.jrafika.jrafika.processor.ImageGrayscaler
import com.jrafika.jrafika.task.histogram.ImageTask
import com.jrafika.jrafika.task.ocr.ImageBinaryOptionFragment
import kotlinx.android.synthetic.main.task_layout.*

class ImageChainCodeActivity: BaseActivity() {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.chain_code_title)

        val grayscaledImageFragment = ImageResultFragment()
        val blackWhiteOptionFragment = ImageBinaryOptionFragment()
        val predictedImage = ImageResultFragment()

        val importImageFragment = ImportImageFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(grayscaledImageFragment, ImageGrayscaler())
                    .then(ImageTask(blackWhiteOptionFragment, ImageBitmapper(125)))
                    .execute(it)
            taskViewPager.setCurrentItem(2)
        }

        blackWhiteOptionFragment.proceedFunction = { bwImage ->
            ImageTask(predictedImage, ChainCodePredictor()).execute(bwImage)
            taskViewPager.setCurrentItem(3)
        }

        taskViewPager.offscreenPageLimit = 4
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        grayscaledImageFragment,
                        blackWhiteOptionFragment,
                        predictedImage
                )[p0]
            }
            override fun getCount(): Int {
                return 4
            }
        }
        taskViewPager.setCurrentItem(0)
    }
}