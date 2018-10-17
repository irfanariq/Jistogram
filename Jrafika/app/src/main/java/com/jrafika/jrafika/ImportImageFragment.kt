package com.jrafika.jrafika

import android.content.ClipboardManager
import android.content.Context
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
import android.R.array
import android.R.id.edit
import android.content.SharedPreferences



class ImportImageFragment: Fragment() {

    var selectedImageView: ImageView? = null
    var addImageButton: FloatingActionButton? = null
    var copyImageButton: FloatingActionButton? = null
    var pasteImageButton: FloatingActionButton? = null

    var imageImportedListener: ((com.jrafika.jrafika.core.Image) -> Unit)? = null

    val MAX_WIDTH = 800
    val MAX_HEIGHT = 600

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image: Image? = ImagePicker.getFirstImageOrNull(data)
            if (selectedImageView != null && image != null) {
                val bitmap = BitmapFactory.decodeFile(image.path)

                var dest_width = bitmap.width
                var dest_height = bitmap.height
                if (bitmap.width > MAX_WIDTH) {
                    dest_width = MAX_WIDTH
                    dest_height = bitmap.height / bitmap.width * dest_width
                }
                if (bitmap.height > MAX_HEIGHT) {
                    dest_height = MAX_HEIGHT
                    dest_width = bitmap.width / bitmap.height * dest_height
                }
                val bitmapSmall = Bitmap.createScaledBitmap(bitmap, dest_width, dest_height, false);
                val coreImage = com.jrafika.jrafika.core.Image.fromBitmap(bitmapSmall)

                val settings = context!!.getSharedPreferences("images", 0)
                val editor = settings.edit()
                editor.putString("currentImage", bitmapSmall.toString())

                copyImageButton!!.show()

                selectedImageView!!.setImageBitmap(bitmapSmall)
                if (imageImportedListener != null)
                    imageImportedListener!!(coreImage)
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

        addImageButton!!.setOnClickListener {
            ImagePicker.create(this)
                    .single()
                    .showCamera(true)
                    .start()
        }

        copyImageButton!!.setOnClickListener {
            val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }

        super.onViewCreated(view, savedInstanceState)
    }
}