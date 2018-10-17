package com.jrafika.jrafika

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image

class ImportImageFragment: Fragment() {

    var selectedImageView: ImageView? = null
    var addImageButton: FloatingActionButton? = null
    var copyImageButton: FloatingActionButton? = null
    var pasteImageButton: FloatingActionButton? = null

    var imageImportedListener: ((com.jrafika.jrafika.core.Image) -> Unit)? = null
    var currentImage: com.jrafika.jrafika.core.Image? = null

    val MAX_WIDTH = 800
    val MAX_HEIGHT = 600

    fun onGetBitmap(bitmap: Bitmap) {
        var dest_width = bitmap.width
        var dest_height = bitmap.height
        if (dest_width > MAX_WIDTH) {
            dest_width = MAX_WIDTH
            dest_height = Math.round(bitmap.height.toFloat() / bitmap.width.toFloat() * dest_width.toFloat())
        }
        if (dest_height > MAX_HEIGHT) {
            dest_height = MAX_HEIGHT
            dest_width = Math.round(bitmap.width.toFloat() / bitmap.height.toFloat() * dest_height.toFloat())
        }
        val bitmapSmall = Bitmap.createScaledBitmap(bitmap, dest_width, dest_height, false);
        currentImage = com.jrafika.jrafika.core.Image.fromBitmap(bitmapSmall)

        copyImageButton!!.show()

        selectedImageView!!.setImageBitmap(bitmapSmall)
        if (imageImportedListener != null)
            imageImportedListener!!(currentImage!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image: Image? = ImagePicker.getFirstImageOrNull(data)
            if (selectedImageView != null && image != null) {
                val bitmap = BitmapFactory.decodeFile(image.path)
                onGetBitmap(bitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.import_image_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addImageButton = view.findViewById(R.id.addImageButton)
        selectedImageView= view.findViewById(R.id.selectedImage)
        copyImageButton = view.findViewById(R.id.copyImageButton)
        pasteImageButton = view.findViewById(R.id.pasteImageButton)

        val settings = context!!.getSharedPreferences("com.jrafika.jrafika.images", 0)
        if (settings.contains("clipboardImage")) {
            pasteImageButton!!.show()
        }

        addImageButton!!.setOnClickListener {
            ImagePicker.create(this)
                    .single()
                    .showCamera(true)
                    .start()
        }

        copyImageButton!!.setOnClickListener {
            if (currentImage != null) {
                val settings = context!!.getSharedPreferences("com.jrafika.jrafika.images", 0)
                with(settings.edit()) {
                    putString("clipboardImage", currentImage.toString())
                    commit()
                }
                pasteImageButton!!.show()
            }
        }

        pasteImageButton!!.setOnClickListener {
            val settings = context!!.getSharedPreferences("com.jrafika.jrafika.images", 0)
            val bitmapBase64 = settings.getString("clipboardImage", null)
            Log.d("wow", "load image " + bitmapBase64)
            if (bitmapBase64 == null) {
                Toast.makeText(getContext(), "Paste image error", Toast.LENGTH_LONG).show()
            } else {
                val image = com.jrafika.jrafika.core.Image.fromBase64String(bitmapBase64)
                onGetBitmap(image.toBitmap())
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }
}