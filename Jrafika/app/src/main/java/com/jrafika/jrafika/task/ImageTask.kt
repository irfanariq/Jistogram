package com.jrafika.jrafika.task.histogram

import android.os.AsyncTask
import com.jrafika.jrafika.ImageDisplayer
import com.jrafika.jrafika.core.Image
import com.jrafika.jrafika.core.ImageProcessor
import com.jrafika.jrafika.task.HistogramTask

class ImageTask(
        resultFragment: ImageDisplayer? = null,
        processor: ImageProcessor? = null,
        histogramFragment: List<DisplayHistogramFragment>? = null,
        next: ImageTask? = null) : AsyncTask<Image, Unit, Image>() {

    val resultFragment = resultFragment
    val processor = processor
    var next = next
    val histogramFragment = histogramFragment

    fun then(next: ImageTask?): ImageTask {
        if (this.next != null) {
            this.next!!.then(next);
        } else {
            this.next = next
        }
        return this
    }

    override fun doInBackground(vararg img: Image?): Image {
        val imgPrimary = img[0]
        if (resultFragment != null) {
            resultFragment.setInput(imgPrimary!!)
        }
        if (processor != null) {
            return processor.proceed(imgPrimary)
        } else {
            return imgPrimary!!.clone()
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        if (resultFragment != null) {
            resultFragment.setLoading()
        }
    }

    override fun onPostExecute(result: Image) {
        super.onPostExecute(result)
        if (resultFragment != null) {
            resultFragment.setImage(result)
        }
        if (histogramFragment != null) {
            HistogramTask(histogramFragment!!.toTypedArray()).execute(result)
        }
        if (next != null) {
            next!!.execute(result);
        }
    }
}