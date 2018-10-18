package com.jrafika.jrafika.task

import android.os.AsyncTask
import com.jrafika.jrafika.core.Histogram
import com.jrafika.jrafika.core.Image
import com.jrafika.jrafika.task.histogram.DisplayHistogramFragment

class HistogramTask(resultFragment: Array<DisplayHistogramFragment>): AsyncTask<Image, Unit, List<IntArray>>() {

    val resultFragment = resultFragment

    override fun onPreExecute() {
        super.onPreExecute()
        for (fragment in resultFragment) {
            fragment.showLoadingHistogram()
        }
    }

    override fun doInBackground(vararg params: Image?): List<IntArray> {
        val histograms = Histogram.calculateHistogram(params[0])
        val result = ArrayList<IntArray>()
        for (i in 0..histograms.size - 1) {
            val hist = IntArray(histograms[i]!!.size)
            for (pix in 0..histograms[i]!!.size - 1) {
                hist[pix] = histograms[i]!![pix]
            }
            result.add(hist)
        }
        return result
    }

    override fun onPostExecute(result: List<IntArray>?) {
        super.onPostExecute(result)
        if (result != null) {
            for (i in 0..result!!.size - 1) {
                if (i >= resultFragment.size) {
                    break
                }
                val fragment = resultFragment[i]
                fragment.showHistogram(result!!.get(i).toTypedArray())
            }
        }
    }
}