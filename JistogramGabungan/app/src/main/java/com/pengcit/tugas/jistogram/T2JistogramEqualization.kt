package com.pengcit.tugas.jistogram

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_t2_jistogram_equalization.*

class T2JistogramEqualization : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t2_jistogram_equalization)

        selectImageBtn.setOnClickListener { IntentHelper.showPictureDialog(this) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var oldGmbr = Bitmap.createBitmap(1,1, Bitmap.Config.RGB_565)
        if (requestCode == IntentHelper.GALLERY) {
            if (data != null) {
                val contentURI = data.data
                oldGmbr = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

            }
        }else if (requestCode == IntentHelper.CAMERA) {

            this.contentResolver.notifyChange(IntentHelper.mImageUri, null)
            val cr = this.contentResolver
            try {
                oldGmbr= android.provider.MediaStore.Images.Media.getBitmap(cr, IntentHelper.mImageUri)

            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        }

        if (oldGmbr.height > 1 && oldGmbr.width > 1) {
            var imgHeight = oldGmbr.height
            var imgWidth = oldGmbr.width

            Log.d("size" , "width " + imgWidth + " | height " + imgHeight)

            if (imgHeight > ImageHelper.MAX_HEIGHT){
                imgWidth = imgWidth * ImageHelper.MAX_HEIGHT / imgHeight
                imgHeight = ImageHelper.MAX_HEIGHT
            }
            if (imgWidth > ImageHelper.MAX_WIDTH){
                imgHeight = imgHeight * ImageHelper.MAX_WIDTH / imgWidth
                imgWidth = ImageHelper.MAX_WIDTH
            }

            val resizedBitmap = Bitmap.createScaledBitmap(oldGmbr, imgWidth, imgHeight, true)
            oldImage.setImageBitmap(resizedBitmap)

            tugas2Title.setText("Size : " + resizedBitmap .width + " x " + resizedBitmap .height)

            var equalizedImage= ImageHelper.equalize(resizedBitmap)
            newImageHE.setImageBitmap(equalizedImage[0])
            newImageLS.setImageBitmap(equalizedImage[1])
            setDoneText()
        }
    }

    private fun setDoneText() {
        noImageText.setText("Result")
        oldImageTitle.setText("Before")
        newImageTitleHE.setText("After Histogram Equalization")
        newImageTitleLS.setText("After Linear Stretching")
    }
}
