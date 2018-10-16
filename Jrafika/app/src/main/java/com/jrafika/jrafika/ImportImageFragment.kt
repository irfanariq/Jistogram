package com.jrafika.jrafika

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image

class ImportImageFragment: Fragment() {

    var selectedImageView: ImageView? = null
    var addImageButton: FloatingActionButton? = null

    var imageImportedListener: ((Bitmap) -> Unit)? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image: Image? = ImagePicker.getFirstImageOrNull(data)
            if (selectedImageView != null && image != null) {
                val bitmap = BitmapFactory.decodeFile(image.path)
                selectedImageView!!.setImageBitmap(bitmap)
                if (imageImportedListener != null)
                    imageImportedListener!!(bitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.import_image_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addImageButton = view.findViewById<FloatingActionButton>(R.id.addImageButton)
        selectedImageView= view.findViewById<ImageView>(R.id.selectedImage)
        addImageButton!!.setOnClickListener {
            ImagePicker.create(this)
                    .single()
                    .showCamera(true)
                    .start()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}