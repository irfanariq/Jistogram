package com.pengcit.tugas.jistogram

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

object ImageHelper {

    val RED = 1
    val GREEN = 2
    val BLUE = 3
    val GRAY = 4

    val MAX_HEIGHT = 1200
    val MAX_WIDTH = 800

    fun calculateImageMatrixHistogram(resizedBitmap: Bitmap?): Array<Array<Int>> {
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

    fun equalize(resizedBitmap: Bitmap?): Array<Bitmap>{
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
}
