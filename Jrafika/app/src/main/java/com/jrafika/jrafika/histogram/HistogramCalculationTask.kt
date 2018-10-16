package com.jrafika.jrafika.histogram

import android.graphics.Bitmap
import android.os.AsyncTask
import com.jrafika.jrafika.core.Histogram
import com.jrafika.jrafika.core.Image

class HistogramCalculationTask(
        redHistogramFragment: RedHistogramFragment,
        greenHistogramFragment: GreenHistogramFragment,
        blueHistogramFragment: BlueHistogramFragment) : AsyncTask<Bitmap, Unit, Map<Int, Array<Int>>>() {

    val redHistogramFragment = redHistogramFragment
    val greenHistogramFragment = greenHistogramFragment
    val blueHistogramFragment = blueHistogramFragment

    override fun doInBackground(vararg img: Bitmap?): Map<Int, Array<Int>> {
        val image = Image.fromBitmap(img[0])
        return Histogram.calculateHistogram(image)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        redHistogramFragment.showLoadingHistogram()
        greenHistogramFragment.showLoadingHistogram()
        blueHistogramFragment.showLoadingHistogram()
    }

    override fun onPostExecute(histogram: Map<Int, Array<Int>>?) {
        super.onPostExecute(histogram)
        redHistogramFragment.showHistogram(histogram!!.get(0)!!)
        greenHistogramFragment.showHistogram(histogram!!.get(1)!!)
        blueHistogramFragment.showHistogram(histogram!!.get(2)!!)
    }
}