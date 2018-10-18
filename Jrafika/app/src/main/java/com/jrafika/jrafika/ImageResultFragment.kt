package com.jrafika.jrafika

import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.jrafika.jrafika.core.Image

open class ImageResultFragment: Fragment(), ImageDisplayer {

    var resultImageView: ImageView? = null
    var copyImageButton: FloatingActionButton? = null
    var progressBar: ProgressBar? = null

    var imageLoadedListener: ((Image) -> Unit)? = null
    var currentImage: Image? = null

    override fun setInput(image: Image) {}

    fun setBitmap(bitmap: Bitmap) {
        currentImage = Image.fromBitmap(bitmap)
        copyImageButton!!.show()
        progressBar!!.visibility = View.INVISIBLE
        progressBar!!.invalidate()
        resultImageView!!.setImageBitmap(bitmap)
        if (imageLoadedListener != null)
            imageLoadedListener!!(currentImage!!)
    }

    override fun setImage(image: Image) {
        currentImage = image
        copyImageButton!!.show()
        progressBar!!.visibility = View.INVISIBLE
        progressBar!!.invalidate()
        resultImageView!!.setImageBitmap(image.toBitmap())
        if (imageLoadedListener != null)
            imageLoadedListener!!(currentImage!!)
    }

    fun getImage(): Image? {
        return currentImage
    }

    override fun setLoading() {
        progressBar!!.visibility = View.VISIBLE
        progressBar!!.invalidate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.image_result_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resultImageView= view.findViewById(R.id.resultImage)
        copyImageButton = view.findViewById(R.id.copyImageButton)
        progressBar = view.findViewById(R.id.progressBar)

        copyImageButton!!.setOnClickListener {
            if (currentImage != null) {
                val settings = context!!.getSharedPreferences("com.jrafika.jrafika.images", 0)
                with(settings.edit()) {
                    putString("clipboardImage", currentImage.toString())
                    commit()
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }
}