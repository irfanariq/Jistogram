package com.jrafika.jrafika.task.histogram

import android.os.AsyncTask
import com.jrafika.jrafika.core.Histogram
import com.jrafika.jrafika.core.Image
import com.jrafika.jrafika.core.Util
import com.jrafika.jrafika.task.equalization.EqualizedHistogramFragment
import com.jrafika.jrafika.task.equalization.GrayscaledImageFragment

class HistogramEqualizationCalculationTask(
        grayscaledImageFragment: GrayscaledImageFragment,
        equalizedHistogramFragment: EqualizedHistogramFragment) : AsyncTask<Image, Unit, Pair<Image, Image>>() {

    val grayscaledImageFragment = grayscaledImageFragment
    val equalizedHistogramFragment = equalizedHistogramFragment

    override fun doInBackground(vararg img: Image?): Pair<Image, Image> {
        val imgPrimary = img[0]
        val grayscaledImage = Util.imageRGBToGrayscale(imgPrimary)
        val equalizedHistogram = Histogram.equalizeHistogram(grayscaledImage)
        return Pair(grayscaledImage, equalizedHistogram)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        grayscaledImageFragment.setLoading()
        equalizedHistogramFragment.setLoading()
    }

    override fun onPostExecute(result: Pair<Image, Image>?) {
        super.onPostExecute(result)
        grayscaledImageFragment.setImage(result!!.first)
        equalizedHistogramFragment.setImage(result!!.second)
    }
}