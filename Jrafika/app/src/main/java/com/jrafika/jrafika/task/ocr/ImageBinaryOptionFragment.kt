package com.jrafika.jrafika.task.ocr

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import com.jrafika.jrafika.ImageDisplayer
import com.jrafika.jrafika.R
import com.jrafika.jrafika.core.Image
import com.jrafika.jrafika.processor.ImageBitmapper
import com.jrafika.jrafika.task.histogram.ImageTask

class ImageBinaryOptionFragment: Fragment(), ImageDisplayer {

    var bitmapImageResult: ImageView? = null
    var proceedButton: Button? = null
    var thresholdSeekbar: SeekBar? = null
    var imageLoadedListener: ((Image) -> Unit)? = null
    var proceedFunction: ((Image) -> Unit)? = null

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
    }

    override fun setLoading() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bitmap_option_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bitmapImageResult = view.findViewById(R.id.bitmapImageResult)
        proceedButton = view.findViewById(R.id.proceedButton)
        thresholdSeekbar = view.findViewById(R.id.thresholdSeekbar)

        val thisFragment = this
        thresholdSeekbar!!.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (inputImage != null) {
                    ImageTask(thisFragment, ImageBitmapper(thresholdSeekbar!!.progress))
                            .execute(inputImage!!)
                }
            }
        })

        proceedButton!!.setOnClickListener { v ->
            if (outputImage != null && proceedFunction != null) {
                proceedFunction!!(outputImage!!)
            }
        }
    }

}