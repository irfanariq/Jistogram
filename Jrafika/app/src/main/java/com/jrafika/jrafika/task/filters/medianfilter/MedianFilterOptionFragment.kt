package com.jrafika.jrafika.task.filters.medianfilter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.jrafika.jrafika.ImageDisplayer
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.Image
import com.jrafika.jrafika.processor.MedianFilterer
import com.jrafika.jrafika.task.histogram.ImageTask

class MedianFilterOptionFragment: Fragment(), ImageDisplayer {

    var bitmapImageResult: ImageView? = null
    var kernelSeekbar: SeekBar? = null
    var progressBar: ProgressBar? = null
    var kernelText: TextView? = null
    var imageLoadedListener: ((Image) -> Unit)? = null

    var inputImage: Image? = null
    var outputImage: Image? = null

    override fun setInput(image: Image) {
        inputImage = image
    }

    override fun setImage(image: Image) {
        outputImage = image.clone()
        bitmapImageResult!!.setImageBitmap(outputImage!!.toBitmap())
        if (imageLoadedListener != null)
            imageLoadedListener!!(outputImage!!)
        progressBar!!.visibility = View.INVISIBLE
        progressBar!!.invalidate()
        kernelSeekbar!!.isEnabled = true
        kernelSeekbar!!.invalidate()
    }

    override fun setLoading() {
        progressBar!!.visibility = View.VISIBLE
        progressBar!!.invalidate()
        kernelSeekbar!!.isEnabled = false
        kernelSeekbar!!.invalidate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.median_filter_option_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bitmapImageResult = view.findViewById(R.id.bitmapImageResult)
        kernelSeekbar = view.findViewById(R.id.kernelSeekbar)
        progressBar = view.findViewById(R.id.progressBar)
        kernelText = view.findViewById(R.id.kernelText)

        val thisFragment = this
        kernelSeekbar!!.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                kernelText!!.setText("" + (2 * kernelSeekbar!!.progress + 1))
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (inputImage != null) {
                    ImageTask(thisFragment, MedianFilterer(2 * kernelSeekbar!!.progress + 1))
                            .execute(inputImage!!)
                }
            }
        })
    }

}