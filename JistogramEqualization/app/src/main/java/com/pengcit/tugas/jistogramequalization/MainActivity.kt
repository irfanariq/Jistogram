package com.pengcit.tugas.jistogramequalization

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.Math.floor

class MainActivity : AppCompatActivity() {

    private val GALLERY = 1
    private val CAMERA = 2
    private val MAX_HEIGHT = 1200
    private val MAX_WIDTH = 800
    private var mImageUri : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectImageBtn.setOnClickListener { showPictureDialog() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var oldGmbr = Bitmap.createBitmap(1,1,Bitmap.Config.RGB_565)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                oldGmbr = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

            }
        }else if (requestCode == CAMERA) {

            this.contentResolver.notifyChange(mImageUri, null)
            val cr = this.contentResolver
            try {
                oldGmbr= android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri)

            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        }

        if (oldGmbr.height > 1 && oldGmbr.width > 1) {
            var imgHeight = oldGmbr.height
            var imgWidth = oldGmbr.width

            Log.d("size" , "width " + imgWidth + " | height " + imgHeight)

            if (imgHeight > MAX_HEIGHT){
                imgWidth = imgWidth * MAX_HEIGHT / imgHeight
                imgHeight = MAX_HEIGHT
            }
            if (imgWidth > MAX_WIDTH){
                imgHeight = imgHeight * MAX_WIDTH / imgWidth
                imgWidth = MAX_WIDTH
            }

            val resizedBitmap = Bitmap.createScaledBitmap(oldGmbr, imgWidth, imgHeight, true)
            oldImage.setImageBitmap(resizedBitmap)

            hello.setText("Size : " + resizedBitmap .width + " x " + resizedBitmap .height)

            var equalizedImage= equalize(resizedBitmap)
            newImageHE.setImageBitmap(equalizedImage[0])
            newImageLS.setImageBitmap(equalizedImage[1])
        }
    }

    private fun equalize(resizedBitmap: Bitmap?): Array<Bitmap>{
        var newImageHe = Bitmap.createBitmap(resizedBitmap!!.width, resizedBitmap.height, resizedBitmap.config)
        var newImageLs = Bitmap.createBitmap(resizedBitmap!!.width, resizedBitmap.height, resizedBitmap.config)
        var ret = arrayOf(newImageHe, newImageLs)

        var arrayToCumulativeValue = Array(256, {Array(3, {i -> 0})})
        var histogram = calculateImageMatrixHistogram(resizedBitmap)
        var cumulativeValue = Array(3, {i -> 0})
        var totalFrequency = resizedBitmap.height * resizedBitmap.width
        var inMin = Array(3, {i -> -1})
        var inMax = Array(3, {i -> -1})
        val outIn = 0
        val outMax = 255

        for (i in 0..255) {
            for (j in 0..2) {
                cumulativeValue[j] += histogram[i][j]
                arrayToCumulativeValue[i][j] = cumulativeValue[j]
                if (inMin[j] == -1 && histogram[i][j] != 0) {
                    inMin[j] = i
                }
                if (inMax[j] < i && histogram[i][j] != 0) {
                    inMax[j] = i
                }
            }
        }
        Log.d("max", "r " + inMax[0] + " | g " + inMax[1] + " | b " + inMax[2])
        Log.d("min", "r " + inMin[0] + " | g " + inMin[1] + " | b " + inMin[2])

        for (i in 0..resizedBitmap.height-1) {
            for (j in 0..resizedBitmap.width-1) {
                val oldPixel = resizedBitmap.getPixel(j, i)
//                Histogram Equalization
                val newRedHe = floor((arrayToCumulativeValue[Color.red(oldPixel)][0] / totalFrequency.toDouble() ) * 255)
                val newGreenHe = floor((arrayToCumulativeValue[Color.green(oldPixel)][1] / totalFrequency.toDouble() ) * 255)
                val newBlueHe = floor((arrayToCumulativeValue[Color.blue(oldPixel)][2] / totalFrequency.toDouble() )* 255)
                val newPixelHe = Color.rgb(newRedHe.toInt(), newGreenHe.toInt(), newBlueHe.toInt())
//                Linear Stretching
                val newRedLs = floor(((Color.red(oldPixel) - inMin[0]) * ((outMax - outIn).toDouble() / (inMax[0] - inMin[0]))) + outIn)
                val newGreenLs = floor(((Color.green(oldPixel) - inMin[1]) * ((outMax - outIn).toDouble() / (inMax[1] - inMin[1]))) + outIn)
                val newBlueLs = floor(((Color.blue(oldPixel) - inMin[2]) * ((outMax - outIn).toDouble() / (inMax[2] - inMin[2]))) + outIn)
                val newPixelLs = Color.rgb(newRedLs.toInt(), newGreenLs.toInt(), newBlueLs.toInt())
                newImageHe.setPixel( j, i, newPixelHe)
                newImageLs.setPixel( j, i, newPixelLs)
            }
        }

        ret.set(0,newImageHe)
        ret.set(1,newImageLs)
        Log.d("DONE", "===============================UDAH SELESAI ===================")
        return ret
    }

    private fun calculateImageMatrixHistogram(resizedBitmap: Bitmap?): Array<Array<Int>> {
        var ret = Array(256, {Array(3, {i -> 0})})
        val heigth = resizedBitmap!!.height
        val width = resizedBitmap.width
        Log.d("array of array", ret.toString() )
        for(i in 0..heigth-1) {
            for(j in 0..width-1){
                val pixel = resizedBitmap.getPixel(j,i)
                ret[Color.red(pixel)][0] += 1
                ret[Color.green(pixel)][1] += 1
                ret[Color.blue(pixel)][2] += 1
            }
        }
        return ret
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> selectImageFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var photo : File? = null
        try {
            photo = createTemporaryFile("picture", ".jpg");
            photo.delete();
        }
        catch(e : Exception) {
            Log.v("Error", "Can't create file to take picture!")
        }
        mImageUri = Uri.fromFile(photo)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        //start camera intent
        startActivityForResult(intent, CAMERA);
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    @Throws(Exception::class)
    private fun createTemporaryFile(part: String, ext: String): File {
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.getAbsolutePath() + "/.temp/")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(part, ext, tempDir)
    }
}
