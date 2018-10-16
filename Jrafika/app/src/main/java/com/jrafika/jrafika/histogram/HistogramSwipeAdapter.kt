package com.jrafika.jrafika.histogram

import android.graphics.Bitmap
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.ImportImageFragment

class HistogramSwipeAdapter(
        fm: FragmentManager,
        imageLoadedListener: ((Bitmap) -> Unit)?)
    : FragmentStatePagerAdapter(fm) {

    val imageLoadedListener = imageLoadedListener

    override fun getItem(p0: Int): Fragment {
        return when (p0) {
            1 -> RedHistogramFragment()
            2 -> GreenHistogramFragment()
            3 -> BlueHistogramFragment()
            else -> {
                val fragment = ImportImageFragment()
                fragment.imageImportedListener = this.imageLoadedListener
                fragment
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }

}