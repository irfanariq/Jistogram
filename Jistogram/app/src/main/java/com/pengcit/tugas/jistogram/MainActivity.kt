package com.pengcit.tugas.jistogram

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.round

class MainActivity : Activity() {

    private val GALLERY = 1
    private val MAX_HEIGHT = 400
    private val MAX_WIDTH = 600
    private val RED = 1
    private val GREEN = 2
    private val BLUE = 4
    private val GRAYSCALE = 7

    private fun setTextXml() {
        selectImageBtn.setText("Select Image")
        hello.setText("Jistogram Image")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTextXml()
        selectImageBtn.setOnClickListener { selectImageFromGallery() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                var imgHeight = bitmap.height
                var imgWidth = bitmap.width

                if (imgHeight > MAX_HEIGHT){
                    imgWidth = imgWidth * MAX_HEIGHT / imgHeight
                    imgHeight = MAX_HEIGHT
                }
                if (imgWidth > MAX_WIDTH){
                    imgHeight = imgHeight * MAX_WIDTH / imgWidth
                    imgWidth = MAX_WIDTH
                }

                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imgWidth, imgHeight, true)
                imageHistogram.setImageBitmap(resizedBitmap)

                createHistogramFromImage(resizedBitmap)
            }
        }
    }

    
    private fun createHistogramFromImage(bitmap: Bitmap?) {
        val myService: ExecutorService = Executors.newFixedThreadPool(4)

        val sumPixel = bitmap!!.width * bitmap.height
        imageTitle.setText("Size : " + bitmap.width + " x " + bitmap.height)
        var red = myService.submit(Callable<Array<Int>> {
            calculatePixel(bitmap, RED)
        })

        var green = myService.submit(Callable<Array<Int>> {
            calculatePixel(bitmap, GREEN)
        })

        var blue = myService.submit(Callable<Array<Int>> {
            calculatePixel(bitmap, BLUE)
        })

        var grayscale =myService.submit(Callable<Array<Int>> {
            calculatePixel(bitmap, GRAYSCALE)
        })

        drawHistogram(red.get(), RED, sumPixel)
        drawHistogram(green.get(), GREEN, sumPixel)
        drawHistogram(blue.get(), BLUE, sumPixel)
        drawHistogram(grayscale.get(), GRAYSCALE, sumPixel)
    }

    private fun drawHistogram(array: Array<Int>, channelId: Int, sumPixel: Int) {

        val entries = ArrayList<Entry>()
        for (i in 0..255) {
//            entries.add(Entry(i.toFloat(), (array[i].toFloat() / sumPixel.toFloat()) ))
            entries.add(Entry(i.toFloat(), (array[i].toFloat()) ))
        }

        if (channelId == RED) {
            redHistogramTitle.setText("Red Done")
            redHistogram.setPinchZoom(true)
            redHistogram.setScaleEnabled(true)
            redHistogram.xAxis.axisMinimum = 0f
            redHistogram.axisLeft.axisMinimum = 0f
            redHistogram.axisRight.axisMinimum = 0f

            val dataSet = LineDataSet(entries, "Red"); // add entries to dataset
            dataSet.setColor(Color.RED);

            val lineData = LineData(dataSet)

            redHistogram.data = lineData
            redHistogram.invalidate()
        }
        if (channelId == GREEN) {
            greenHistogramTitle.setText("Green Done")
            greenHistogram.setPinchZoom(true)
            greenHistogram.setScaleEnabled(true)
            greenHistogram.xAxis.axisMinimum = 0f
            greenHistogram.axisLeft.axisMinimum = 0f
            greenHistogram.axisRight.axisMinimum = 0f

            val dataSet = LineDataSet(entries, "Green"); // add entries to dataset
            dataSet.setColor(Color.GREEN);

            val lineData = LineData(dataSet)

            greenHistogram.data = lineData
            greenHistogram.invalidate()
        }
        if (channelId == BLUE){
            blueHistogramTitle.setText("Blue Done")
            blueHistogram.setPinchZoom(true)
            blueHistogram.setScaleEnabled(true)
            blueHistogram.xAxis.axisMinimum = 0f
            blueHistogram.axisLeft.axisMinimum = 0f
            blueHistogram.axisRight.axisMinimum = 0f

            val dataSet = LineDataSet(entries, "Blue"); // add entries to dataset
            dataSet.setColor(Color.BLUE);

            val lineData = LineData(dataSet)

            blueHistogram.data = lineData
            blueHistogram.invalidate()
        }
        if (channelId == GRAYSCALE) {
            grayscaleHistogramTitle.setText("Grayscale Done")
            grayscaleHistogram.setPinchZoom(true)
            grayscaleHistogram.setScaleEnabled(true)
            grayscaleHistogram.xAxis.axisMinimum = 0f
            grayscaleHistogram.axisLeft.axisMinimum = 0f
            grayscaleHistogram.axisRight.axisMinimum = 0f

            val dataSet = LineDataSet(entries, "Grayscale"); // add entries to dataset
            dataSet.setColor(Color.GRAY);

            val lineData = LineData(dataSet)

            grayscaleHistogram.data = lineData
            grayscaleHistogram.invalidate()
        }
    }

    private fun calculatePixel(bitmap: Bitmap?, channelId: Int): Array<Int> {
        var ret = Array(256, {i  ->  0})

        var imgHeight = bitmap!!.height
        var imgWidth = bitmap.width

        Log.d("size", "width : " + imgWidth + "| height : " + imgHeight)

//        if ((channelId and RED) != 0) {
//            Log.d("warna", "red")
//        }
//        if ((channelId and GREEN) != 0) {
//            Log.d("warna", "green")
//        }
//        if ((channelId and BLUE) != 0) {
//            Log.d("warna", "blue")
//        }

        for(i in 0..imgHeight-1) {
            for(j in 0..imgWidth-1) {
                var color = bitmap.getPixel(j,i)
                var colorSum = Array(0, {0})

                if ((channelId and RED) != 0) {
                    colorSum += Color.red(color)
                }
                if ((channelId and GREEN) != 0) {
                    colorSum += Color.green(color)
                }
                if ((channelId and BLUE) != 0) {
                    colorSum += Color.blue(color)
                }
                var value = round((colorSum.sum() / colorSum.size).toDouble())
                ret[value.toInt()] += 1
            }
        }

        Log.d("read pixel", "Done read pixel")
        return ret
    }

    fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }
}
