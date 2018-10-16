package com.jrafika.jrafika.histogram

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.jrafika.jrafika.ImportImageFragment

class HistogramSwipeAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

    override fun getItem(p0: Int): Fragment {
        return when (p0) {
            1 -> RedHistogramFragment()
            2 -> GreenHistogramFragment()
            3 -> BlueHistogramFragment()
            else -> ImportImageFragment()
        }
    }

    override fun getCount(): Int {
        return 4
    }

}