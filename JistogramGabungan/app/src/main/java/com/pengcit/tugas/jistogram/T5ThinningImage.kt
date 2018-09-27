package com.pengcit.tugas.jistogram

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_t5__thinning_image.*

class T5ThinningImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t5__thinning_image)
        rawImage.visibility = View.INVISIBLE
        preprediction.visibility = View.INVISIBLE
        thinImageLayout.visibility = View.INVISIBLE
        thinImageLayoutOnly.visibility = View.INVISIBLE
        thresholdText.text = thresholdSeekbar.progress.toString()
        thresholdSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                thresholdText.text = thresholdSeekbar.progress.toString()
            }
        })
        selectImageBtn.setOnClickListener {
            IntentHelper.showPictureDialog(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var oldGmbr = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        if (requestCode == IntentHelper.GALLERY) {
            if (data != null) {
                val contentURI = data.data
                oldGmbr = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
            }
        } else if (requestCode == IntentHelper.CAMERA) {

            this.contentResolver.notifyChange(IntentHelper.mImageUri, null)
            val cr = this.contentResolver
            try {
                oldGmbr = android.provider.MediaStore.Images.Media.getBitmap(cr, IntentHelper.mImageUri)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        }

        if (oldGmbr.height > 1 && oldGmbr.width > 1) {
            var imgHeight = oldGmbr.height
            var imgWidth = oldGmbr.width

            Log.d("size", "width " + imgWidth + " | height " + imgHeight)

            if (imgHeight > ImageHelper.MAX_HEIGHT) {
                imgWidth = imgWidth * ImageHelper.MAX_HEIGHT / imgHeight
                imgHeight = ImageHelper.MAX_HEIGHT
            }
            if (imgWidth > ImageHelper.MAX_WIDTH) {
                imgHeight = imgHeight * ImageHelper.MAX_WIDTH / imgWidth
                imgWidth = ImageHelper.MAX_WIDTH
            }

            rawImage.visibility = View.VISIBLE
            val resizedBitmap = Bitmap.createScaledBitmap(oldGmbr, imgWidth, imgHeight, true)
            sizeImage.setText("Size : " + imgWidth + " x " + imgHeight)
            oldImage.setImageBitmap(resizedBitmap)
            blackWhiteImageBtn.setOnClickListener {
                var blackWhiteGambar = blackWhiteImg(resizedBitmap, thresholdSeekbar.progress)
                blackWhiteImage.setImageBitmap(blackWhiteGambar.first)
                preprediction.visibility = View.VISIBLE
                Toast.makeText(this, "BlackWhite Done", Toast.LENGTH_SHORT).show()
                thinningBtn.setOnClickListener {
                    thinningImage(blackWhiteGambar)
                    thinImageLayout.visibility = View.VISIBLE
                    thinImageLayoutOnly.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun thinningImage(imageMat: Pair<Bitmap, Array<Array<Int>>>) {
        val imgHeight = imageMat!!.first.height
        val imgWidth = imageMat.first.width
        var matriks = imageMat.second
        var bwImage = imageMat.first

        var change = true
        while (change) {
            change = false
            var mustDeleted = arrayListOf<Pair<Int, Int>>()
            for(y in 0..imgHeight-1){
                for(x in 0..imgWidth-1){
                    if(checkDelete1(matriks, y, x)){
//                        Log.d("delete 1", "delete " + x + " '" + y)
                        change = true
                        mustDeleted.add(Pair(x,y))
                    }
                }
            }
            for(i in 0..mustDeleted.size-1){
                matriks[mustDeleted[i].second][mustDeleted[i].first] = 255
            }
            mustDeleted = arrayListOf()
            for(y in 0..imgHeight-1){
                for(x in 0..imgWidth-1){
                    if(checkDelete2(matriks, y, x)){
//                        Log.d("delete 2", "delete " + x + " '" + y)
                        change = true
                        mustDeleted.add(Pair(x,y))
                    }
                }
            }
            for(i in 0..mustDeleted.size-1){
                matriks[mustDeleted[i].second][mustDeleted[i].first] = 255
            }
        }

        var thinnedImage = Bitmap.createBitmap(bwImage)
        var thinnedImageOnly = Bitmap.createBitmap(bwImage.width, bwImage.height, bwImage.config)

        val scale = 1
        val canvas = Canvas(thinnedImage)

        val paintBox = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBox.color = Color.CYAN
//        paintBox.strokeWidth = 2F
//        paintBox.setStyle(Paint.Style.STROKE)
        for (y in 0..imgHeight-1){
            for(x in 0..imgWidth-1){
                if(matriks[y][x] == 0){
//                    canvas.drawPoint(x.toFloat(), y.toFloat(), paintBox)
                    thinnedImage.setPixel(x,y,Color.CYAN)
                    thinnedImageOnly.setPixel(x,y,Color.BLACK)
                }
            }
        }
        Toast.makeText(this, "Thinning Done", Toast.LENGTH_SHORT).show()
        thinImage.setImageBitmap(thinnedImage)
        thinImageOnly.setImageBitmap(thinnedImageOnly)
    }

    private fun checkDelete1(matriks: Array<Array<Int>>, y: Int, x: Int): Boolean {
        if (matriks[y][x] != 0)
            return false
        val height = matriks.size
        val width = matriks[height-1].size
        val p2 = if (y > 0) matriks[y-1][x] else 255
        val p3 = if (y > 0 && x < width-1) matriks[y-1][x+1] else 255
        val p4 = if (x < width-1) matriks[y][x+1] else 255
        val p5 = if (y < height-1 && x < width-1) matriks[y+1][x+1] else 255
        val p6 = if (y < height-1) matriks[y+1][x] else 255
        val p7 = if (y < height-1 && x > 0) matriks[y+1][x-1] else 255
        val p8 = if (x > 0) matriks[y][x-1  ] else 255
        val p9 = if (y > 0 && x > 0) matriks[y-1][x-1] else 255
        val ps = arrayListOf(p2,p3,p4,p5,p6,p7,p8,p9)
        var np = 0
        ps.forEach{x -> if (x == 0) np +=1}
        var sp = 0
        for(i in 0..ps.size-1){
            var j = if (i != ps.size-1) (i + 1) else 0
            sp += if (ps[i] == 0 && ps[j] == 255) 1 else 0
        }
        val ret = (2 <= np) && (np <= 6) && (sp == 1) && (p4 == 255 || p6 == 255 || (p2 == 255 && p8 == 255))
//        Log.d("check delete 1", "np " + np + "sp '" + sp  + "ret '" + ret )
        return ret
    }

    private fun checkDelete2(matriks: Array<Array<Int>>, y: Int, x: Int): Boolean {
        if (matriks[y][x] != 0)
            return false
        val height = matriks.size
        val width = matriks[height-1].size
        val p2 = if (y > 0) matriks[y-1][x] else 255
        val p3 = if (y > 0 && x < width-1) matriks[y-1][x+1] else 255
        val p4 = if (x < width-1) matriks[y][x+1] else 255
        val p5 = if (y < height-1 && x < width-1) matriks[y+1][x+1] else 255
        val p6 = if (y < height-1) matriks[y+1][x] else 255
        val p7 = if (y < height-1 && x > 0) matriks[y+1][x-1] else 255
        val p8 = if (x > 0) matriks[y][x-1  ] else 255
        val p9 = if (y > 0 && x > 0) matriks[y-1][x-1] else 255
        val ps = arrayListOf(p2,p3,p4,p5,p6,p7,p8,p9)
        var np = 0
        ps.forEach{x -> if (x == 0) np +=1}
        var sp = 0
        for(i in 0..ps.size-1){
            var j = if (i != ps.size-1) (i + 1) else 0
            sp += if (ps[i] == 0 && ps[j] == 255) 1 else 0
        }
        val ret = (2 <= np) && (np <= 6) && (sp == 1) && (p2 == 255 || p8 == 255 || (p4 == 255 && p6 == 255))
//        Log.d("check delete 1", "np " + np + "sp '" + sp  + "ret '" + ret )
        return ret
    }

    private fun blackWhiteImg(image: Bitmap?, threshold: Int): Pair<Bitmap, Array<Array<Int>>> {
        var bwImage = Bitmap.createBitmap(image!!.width, image.height, image.config)
        var matriks = Array(image.height, {Array(image.width, {0})})
        for (i in 0..image.height-1) {
            for (j in 0..image.width - 1) {
                var oldPixel = image.getPixel(j,i)
                var grayPixel = ImageHelper.rgbToGrayscale(Color.red(oldPixel), Color.green(oldPixel), Color.blue(oldPixel))
                var newPixel = Color.rgb(0, 0, 0)
                matriks[i][j] = 0
                if (grayPixel > threshold) {
                    newPixel = Color.rgb(255, 255, 255)
                    matriks[i][j] = 255
                }
                bwImage.setPixel(j,i,newPixel)
            }
        }
        return Pair(bwImage, matriks)
    }

}
