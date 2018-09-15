package com.pengcit.tugas.jistogram

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

object ImageHelper {

    val RED = 0
    val GREEN = 1
    val BLUE = 2
    val GRAY = 3

    val MAX_HEIGHT = 1200
    val MAX_WIDTH = 800

    fun convertRgbToGrayscale(image: Bitmap):Bitmap {
        var newImage = Bitmap.createBitmap(image!!.width, image.height, image.config)

        for (i in 0..image.height-1) {
            for (j in 0..image.width - 1) {
                var oldPixel = image.getPixel(j,i)
                var grayPixel = rgbToGrayscale(Color.red(oldPixel), Color.green(oldPixel), Color.blue(oldPixel))
                var newPixel = Color.rgb(grayPixel, grayPixel, grayPixel)
                newImage.setPixel(j,i,newPixel)
            }
        }
        return newImage
    }

    fun calculateImageMatrixHistogramGray(resizedBitmap: Bitmap?): Array<Int> {
        var ret = Array(256, {0})
        val heigth = resizedBitmap!!.height
        val width = resizedBitmap.width
        Log.d("array of array", ret.toString() )
        for(i in 0..heigth-1) {
            for(j in 0..width-1){
                val pixel = resizedBitmap.getPixel(j,i)
                ret[rgbToGrayscale(Color.red(pixel), Color.green(pixel), Color.blue(pixel))] += 1
            }
        }
        return ret
    }

    fun calculateImageMatrixHistogramRGB(resizedBitmap: Bitmap?): Array<Array<Int>> {
        var ret = Array(256, {Array(3, {i -> 0})})
        val heigth = resizedBitmap!!.height
        val width = resizedBitmap.width
        Log.d("array of array", ret.toString() )
        for(i in 0..heigth-1) {
            for(j in 0..width-1){
                val pixel = resizedBitmap.getPixel(j,i)
                ret[Color.red(pixel)][RED] += 1
                ret[Color.green(pixel)][GREEN] += 1
                ret[Color.blue(pixel)][BLUE] += 1
            }
        }
        return ret
    }

    fun rgbToGrayscale(red: Int, green: Int, blue: Int): Int {
        return Math.floor((0.299 * red ) + (0.587 * green) + (0.114 * blue)).toInt()
    }

    fun equalizeRGB(resizedBitmap: Bitmap?): Array<Bitmap>{
        var newImageHe = Bitmap.createBitmap(resizedBitmap!!.width, resizedBitmap.height, resizedBitmap.config)
        var newImageLs = Bitmap.createBitmap(resizedBitmap!!.width, resizedBitmap.height, resizedBitmap.config)
        var ret = arrayOf(newImageHe, newImageLs)

        var arrayToCumulativeValue = Array(256, {Array(3, {i -> 0})})
        var histogram = calculateImageMatrixHistogramRGB(resizedBitmap)
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
                val newRedHe = Math.floor((arrayToCumulativeValue[Color.red(oldPixel)][0] / totalFrequency.toDouble()) * 255)
                val newGreenHe = Math.floor((arrayToCumulativeValue[Color.green(oldPixel)][1] / totalFrequency.toDouble()) * 255)
                val newBlueHe = Math.floor((arrayToCumulativeValue[Color.blue(oldPixel)][2] / totalFrequency.toDouble()) * 255)
                val newPixelHe = Color.rgb(newRedHe.toInt(), newGreenHe.toInt(), newBlueHe.toInt())
//                Linear Stretching
                val newRedLs = Math.floor(((Color.red(oldPixel) - inMin[0]) * ((outMax - outIn).toDouble() / (inMax[0] - inMin[0]))) + outIn)
                val newGreenLs = Math.floor(((Color.green(oldPixel) - inMin[1]) * ((outMax - outIn).toDouble() / (inMax[1] - inMin[1]))) + outIn)
                val newBlueLs = Math.floor(((Color.blue(oldPixel) - inMin[2]) * ((outMax - outIn).toDouble() / (inMax[2] - inMin[2]))) + outIn)
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

    fun createGraphDataRGBGray(histogram: Array<Array<Int>>): ArrayList<LineData> {
        val entries = Array(4, {i -> ArrayList<Entry>() })
        for (i in 0..255) {
            var sum = 0
            for (j in 0..2) {
                sum += histogram[i][j]
                entries[j].add(Entry(i.toFloat(), (histogram[i][j].toFloat()) ))
            }
            entries[3].add(Entry(i.toFloat(), (sum.toFloat() / 3)))
        }
        
        val redDataSet = LineDataSet(entries[RED], "Red"); // add entries to dataset
        redDataSet.setColor(Color.RED)
        val greenDataSet = LineDataSet(entries[GREEN], "Green"); // add entries to dataset
        greenDataSet.setColor(Color.GREEN)
        val blueDataSet = LineDataSet(entries[BLUE], "Blue"); // add entries to dataset
        blueDataSet.setColor(Color.BLUE)
        val grayDataSet = LineDataSet(entries[GRAY], "GrayScale"); // add entries to dataset
        grayDataSet.setColor(Color.GRAY)
        
        val redLineData = LineData(redDataSet)
        val greenLineData = LineData(greenDataSet)
        val blueLineData = LineData(blueDataSet)
        val grayLineData = LineData(grayDataSet)
        
        val ret = ArrayList<LineData>()
        ret.add(redLineData)
        ret.add(greenLineData)
        ret.add(blueLineData)
        ret.add(grayLineData)
        return ret
    }

    fun createGraphData(array: Array<Int>, label: String, color: Int): LineData {
        var entries = ArrayList<Entry>()
        for (i in 0..array.size - 1) {
            entries.add(Entry(i.toFloat(), array[i].toFloat()))
        }
        val dataSet = LineDataSet(entries, label)
        dataSet.setColor(color)

        return LineData(dataSet)
    }

    fun drawHistogram(data: LineData, titleView: TextView, chart: LineChart, title: String, colorChart: Int) {
        titleView.setText(title)
        chart.setPinchZoom(true)
        chart.setScaleEnabled(true)
        chart.xAxis.axisMinimum = 0f
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.axisMinimum = 0f

        chart.data = data
        chart.invalidate()
    }

    fun createGraphData(array: Array<Double>, label: String, color: Int): LineData {
        var entries = ArrayList<Entry>()
        for (i in 0..array.size - 1) {
            entries.add(Entry(i.toFloat(), array[i].toFloat()))
        }
        val dataSet = LineDataSet(entries, label)
        dataSet.setColor(color)

        return LineData(dataSet)
    }
}
