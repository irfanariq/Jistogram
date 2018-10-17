package com.jrafika.jrafika.task.stretching

import android.os.AsyncTask
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.core.Histogram
import com.jrafika.jrafika.core.Image
import com.jrafika.jrafika.core.Util

class LinearStretchCalculationTask(
        grayscaledImageFragment: ImageResultFragment,
        imageResultFragment: ImageResultFragment) : AsyncTask<Image, Unit, Pair<Image, Image>>() {

    val grayscaledImageFragment = grayscaledImageFragment
    val imageResultFragment = imageResultFragment

    override fun doInBackground(vararg img: Image?): Pair<Image, Image> {
        val imgPrimary = img[0]
        val grayscaledImage = Util.imageRGBToGrayscale(imgPrimary)
        val equalizedHistogram = Histogram.equalizeHistogram(grayscaledImage)
        return Pair(grayscaledImage, equalizedHistogram)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        grayscaledImageFragment.setLoading()
        imageResultFragment.setLoading()
    }

    override fun onPostExecute(result: Pair<Image, Image>?) {
        super.onPostExecute(result)
        grayscaledImageFragment.setImage(result!!.first)
        imageResultFragment.setImage(result!!.second)
    }
}