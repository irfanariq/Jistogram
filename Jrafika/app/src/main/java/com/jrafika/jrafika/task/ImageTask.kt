package com.jrafika.jrafika.task.histogram

import android.os.AsyncTask
import com.jrafika.jrafika.ImageResultFragment
import com.jrafika.jrafika.core.Image
import com.jrafika.jrafika.core.ImageProcessor

class ImageTask(
        resultFragment: ImageResultFragment,
        processor: ImageProcessor,
        next: ImageTask? = null) : AsyncTask<Image, Unit, Image>() {

    val resultFragment = resultFragment
    val processor = processor
    var next = next

    fun then(next: ImageTask?): ImageTask {
        this.next = next
        return this
    }

    override fun doInBackground(vararg img: Image?): Image {
        val imgPrimary = img[0]
        val resultImage = processor.proceed(imgPrimary)
        return resultImage
    }

    override fun onPreExecute() {
        super.onPreExecute()
        resultFragment.setLoading()
    }

    override fun onPostExecute(result: Image) {
        super.onPostExecute(result)
        resultFragment.setImage(result)
        if (next != null) {
            next!!.execute(result);
        }
    }
}