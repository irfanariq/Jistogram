package com.jrafika.jrafika.task.face

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.BaseActivity
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.ImportImageFragment
import com.jrafika.jrafika.R
import com.jrafika.jrafika.processor.FaceLocalizer
import com.jrafika.jrafika.processor.FaceRecognizer
import com.jrafika.jrafika.processor.SkinDetector
import com.jrafika.jrafika.task.histogram.ImageTask
import kotlinx.android.synthetic.main.task_layout.*

class FaceRecognizerActivity : BaseActivity()  {

    override val contentViewId: Int
        get() = R.layout.task_layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.face_recognizer_title)

        val importImageFragment = ImportImageFragment()
        val resultImageFragment = ImageResultFragment()
        importImageFragment.imageImportedListener = {
            ImageTask(resultImageFragment, FaceRecognizer()).execute(it)
            taskViewPager.setCurrentItem(2)
        }

        taskViewPager.offscreenPageLimit = 2
        taskViewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return arrayOf(
                        importImageFragment,
                        resultImageFragment
                )[p0]
            }
            override fun getCount(): Int {
                return 2
            }
        }
        taskViewPager.setCurrentItem(0)
    }
}
