package com.pengcit.tugas.jistogram

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_t3_jistogram_specification.*
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import koma.end
import koma.extensions.*
import koma.mat
import koma.matrix.Matrix

class T3JistogramSpecification : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t3_jistogram_specification)

        input1Text.text = input1.progress.toString()
        input2Text.text = input2.progress.toString()
        input3Text.text = input3.progress.toString()
        input4Text.text = input4.progress.toString()
        seekbars.visibility = View.INVISIBLE
        histograms.visibility = View.INVISIBLE
        setListenerSeekBar()
        selectImageBtn.setOnClickListener { IntentHelper.showPictureDialog(this) }
    }

    private fun analyzeImage(image: Bitmap): Bitmap? {

        var equalizer_y = arrayListOf<Int>()
        equalizer_y.add(input1.progress)
        equalizer_y.add(input2.progress)
        equalizer_y.add(input3.progress)
        equalizer_y.add(input4.progress)
        var equalizer_n = equalizer_y.size
        var equalizer_x = arrayListOf<Int>()
        for(i in 0..equalizer_n-2) {
            equalizer_x.add(Math.floor(i * 256 / (equalizer_n - 1).toDouble()).toInt())
        }
        equalizer_x.add(255)

        var equalizer_fn = arrayListOf<Matrix<Double>>()
        for(i in 0..equalizer_n - 2) {
            var aPoint = arrayListOf(equalizer_x[i], equalizer_y[i])
            var bPoint = arrayListOf(equalizer_x[i+1], equalizer_y[i+1])

            var lhsMat = mat[0, 1, (2*aPoint[0]), (3*Math.pow(aPoint[0].toDouble(), 2.0)) end
                    0, 1, (2*bPoint[0]), (3*Math.pow(bPoint[0].toDouble(),2.0)) end
                    1, aPoint[0], Math.pow(aPoint[0].toDouble(),2.0), Math.pow(aPoint[0].toDouble(), 3.0) end
                    1, bPoint[0], Math.pow(bPoint[0].toDouble(),2.0), Math.pow(bPoint[0].toDouble(),3.0)
            ]
            var rhsMat = mat[0, 0, aPoint[1], bPoint[1]].T
            var temp = lhsMat.solve(rhsMat)
            equalizer_fn.add(temp)
        }

        // LOG HASIL =================================
        for(i in 0..equalizer_n-2) {
            Log.d("matrix", i.toString() + " rows " + equalizer_fn[i].numRows() + " cols " + equalizer_fn[i].numCols())
            var msg = ""
            for(x in 0..equalizer_fn[i].numRows()-1) {
                for(y in 0..equalizer_fn[i].numCols()-1) {
                    msg += equalizer_fn[i].get(x,y)
                }
                msg += " - "
            }
            Log.d("isi mat", msg)
        }
        // LOG HASIL =================================

        var desiredHistogram = Array<Double>(256, {i -> 0.0})
        for (i in 0..255) {
            var tempX = Double.NaN
            for (j in equalizer_n-2 downTo 0) {
                if (i >= equalizer_x[j]) {
                    tempX = equalizer_fn[j].get(0,0) + equalizer_fn[j].get(1,0) * i + Math.pow(i.toDouble(), 2.0)  * equalizer_fn[j].get(2,0) + equalizer_fn[j].get(3,0) * Math.pow(i.toDouble(),3.0)
//                    Log.d("tempx", "i " + i + " " + "j " + j + " v " + tempX)
                    break
                }
            }
            if (tempX == Double.NaN) {
                tempX = equalizer_y[equalizer_n - 1].toDouble()
//                Log.d("tempx", "i " + i + " " + "j " + equalizer_y[equalizer_n - 1] + " v " + tempX)
            }
            desiredHistogram[i] = tempX
//            Log.d("log", "i " + i + " v " + desiredHistogram[i])
        }

        var oldHistogram = ImageHelper.calculateImageMatrixHistogramGray(image)
        var cummulativeRawHistogram = countCummulativeHistogram(oldHistogram)
        var cummulativeDestHistogram = countCummulativeHistogram(desiredHistogram)

        var scaleFactor = cummulativeRawHistogram[cummulativeRawHistogram.size - 1] / cummulativeDestHistogram[cummulativeDestHistogram.size - 1]
        Log.d("scale","scale " + scaleFactor)

        for (i in 0..255) {
            cummulativeDestHistogram[i] = cummulativeDestHistogram[i] * scaleFactor
        }

        var lookUpTable = Array<Int>(256, {0})
        for(i in 0..255) {
            var tempHi = cummulativeRawHistogram[i]
            var min = Double.MAX_VALUE
            var minIdx = -1
            for(j in 0..255) {
                if (min > Math.abs(tempHi - cummulativeDestHistogram[j])) {
                    minIdx = j
                    min = Math.abs(tempHi - cummulativeDestHistogram[j])
                }
            }
            lookUpTable[i] = minIdx
            Log.d("lookup", "from " + i + " v " + lookUpTable[i])
        }

        var newImage = Bitmap.createBitmap(image!!.width, image.height, image.config)

        var newHistogram = Array<Int>(256, {0})
        for (i in 0..image.height-1) {
            for (j in 0..image.width - 1) {
                var oldPixel = image.getPixel(j,i)
                var newPixel = Color.rgb(lookUpTable[Color.red(oldPixel)], lookUpTable[Color.green(oldPixel)], lookUpTable[Color.blue(oldPixel)])
                newHistogram[ImageHelper.rgbToGrayscale(lookUpTable[Color.red(oldPixel)], lookUpTable[Color.green(oldPixel)], lookUpTable[Color.blue(oldPixel)])] += 1
                newImage.setPixel(j,i,newPixel)
            }
        }

        // GAMBAR GRAFIK HISTOGRAM =========================================
//        val newHistogram = ImageHelper.calculateImageMatrixHistogramGray(newImage)
        val dataOld = ImageHelper.createGraphData(oldHistogram, "Old Histogram", Color.GRAY)
        val dataNew = ImageHelper.createGraphData(newHistogram, "New Histogram", Color.GRAY)
        val destFunctionData = ImageHelper.createGraphData(desiredHistogram, "Destination Function", Color.GRAY)

        ImageHelper.drawHistogram(dataOld, oldHistogramTitle, oldHistogramGraph, "Old Histogram", Color.GRAY)
        ImageHelper.drawHistogram(dataNew, newHistogramTitle, newHistogramGraph, "New Histogram", Color.GRAY)
        ImageHelper.drawHistogram(destFunctionData, destFunctionTitle, destFunction, "Destination Function", Color.GRAY)
        // GAMBAR GRAFIK HISTOGRAM =========================================

        return newImage
    }

    fun countCummulativeHistogram(histogram: Array<Double>):Array<Double> {
        var cummulative_sum = 0.0
        var cummulative_arr = Array<Double>(histogram.size, {i -> 0.0})
        for (i in 0..histogram.size-1) {
            cummulative_sum += histogram[i]
            Log.d("cmm", "sum " + i + " " + cummulative_sum)
            cummulative_arr[i] = cummulative_sum
        }
        return cummulative_arr
    }

    fun countCummulativeHistogram(histogram: Array<Int>):Array<Int> {
        var cummulative_sum = 0
        var cummulative_arr = Array<Int>(histogram.size, {i -> 0})
        for (i in 0..histogram.size-1) {
            cummulative_sum += histogram[i]
            Log.d("cmm", "sum " + i + " " + cummulative_sum)
            cummulative_arr[i] = cummulative_sum
        }
        return cummulative_arr
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

            setListenerSeekBar()
            seekbars.visibility = View.VISIBLE
            histograms.visibility = View.VISIBLE

            val resizedBitmap = Bitmap.createScaledBitmap(oldGmbr, imgWidth, imgHeight, true)
            oldImage.setImageBitmap(resizedBitmap)
            var grayscaleImage = ImageHelper.convertRgbToGrayscale(resizedBitmap)
            oldImageGray.setImageBitmap(grayscaleImage)
            specificationBtn.setOnClickListener{
                var equalizedImage = analyzeImage(grayscaleImage)
                newImageGray.setImageBitmap(equalizedImage)
            }
        }
    }

    private fun setListenerSeekBar() {
        input1.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                input1Text.text = input1.progress.toString()
            }
        })
        input2.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                input2Text.text = input2.progress.toString()
            }
        })
        input3.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                input3Text.text = input3.progress.toString()
            }
        })
        input4.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                input4Text.text = input4.progress.toString()
            }
        })
    }
}

