package com.pengcit.tugas.jistogram

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_t4_chain_code_arial_number.*
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.*


class T4ChainCodeArialNumber : AppCompatActivity() {

    val template0 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,2,2,3,3,3,3,3,3,3,4,3,3,4,4,3,3,4,4,4,4,4,4,4,3,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,5,5,4,4,4,4,4,5,5,4,4,4,4,5,4,4,5,5,5,5,5,5,5,6,6,5,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,7,7,6,7,7,7,7,7,7,0,7,7,0,0,7,7,0,0,0,7,7,0,0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,1,0,0,1,1,1,1,1,1,1,2,2)
    val template1 = arrayListOf(2,2,2,2,2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,3,3,2,2,2,2,2,2,2,2,2,2,4,4,4,4,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,5,5,5,6,5,5,5,5,6,5,5,0,0,0,0,0,2,1,1,1,1,2,1,1,1,2)
    val template2 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,2,3,2,3,3,3,3,4,4,4,4,3,4,4,5,4,4,5,4,5,4,5,5,5,5,5,5,6,5,5,5,5,5,6,5,5,5,5,5,4,5,5,4,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,4,4,4,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,0,0,0,0,1,0,1,1,1,0,0,1,1,1,1,2,1,1,1,1,1,2,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,7,7,7,7,7,6,6,6,6,6,6,6,5,6,5,5,5,4,5,4,5,7,6,6,0,0,0,1,0,0,1,1,2,1,2)
    val template3 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,3,2,3,3,3,4,3,4,4,4,4,4,4,4,4,5,5,5,5,6,5,5,3,2,2,3,2,3,3,3,4,4,3,4,4,5,4,4,4,5,4,5,5,6,5,6,5,6,6,6,6,6,6,6,6,6,6,6,6,7,6,7,6,7,0,7,7,0,0,7,1,2,2,2,2,4,4,3,4,3,2,3,3,2,2,2,2,2,2,2,2,1,2,1,1,0,1,0,0,0,0,0,0,7,7,7,6,7,6,6,6,6,7,6,6,6,0,0,0,2,2,2,2,2,2,1,2,1,1,1,1,0,0,0,0,0,7,0,7,7,6,7,6,6,6,6,6,6,6,5,5,5,5,4,5,6,6,6,6,0,1,0,0,1,1,1,1,2,2)
    val template4 = arrayListOf(2,2,2,2,2,2,2,2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,3,2,2,2,2,2,2,2,4,4,4,4,6,6,6,6,6,6,6,5,4,4,4,4,4,4,4,4,4,4,4,4,4,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,7,7,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,0,0,0,0,1,1,0,0,1,1,1,0,1,1,1,0,0,1,1,1,0,0,1,1,1,0,1,1,1,1,1,0,0,1,1,1,0,1,1,1,0,0,1,1,1,0,0)
    val template5 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,4,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,5,4,4,5,4,4,4,4,4,4,4,4,4,3,2,2,1,2,2,2,2,2,2,2,2,3,2,2,3,3,3,4,3,4,4,3,4,4,4,4,5,4,4,4,4,5,5,5,6,5,6,5,6,6,6,6,6,6,6,6,6,6,7,6,7,6,7,7,0,7,0,0,2,1,2,4,3,4,3,3,2,3,2,2,2,2,2,2,2,2,1,1,1,0,1,0,0,0,0,0,0,0,7,0,7,7,6,7,6,6,6,6,6,6,5,6,5,6,5,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0)
    val template6 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,3,3,2,3,3,3,3,3,4,4,6,6,6,6,6,0,0,7,7,6,6,7,6,6,6,6,6,6,6,5,5,5,5,5,5,5,4,5,4,4,4,4,5,4,4,4,4,4,3,3,1,1,1,1,1,2,2,1,1,2,2,2,2,2,2,2,3,2,2,3,2,3,3,3,3,4,3,3,4,4,4,3,3,4,4,4,5,5,4,4,4,4,4,5,4,5,5,5,5,5,6,6,5,6,6,6,6,6,6,6,6,6,6,6,6,6,7,6,7,7,7,7,7,0,0,7,0,7,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,1,1,0,1,0,0,1,1,2,1,1,2)
    val template7 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,4,4,4,4,4,5,5,4,5,5,5,4,5,5,4,5,5,5,4,5,5,4,5,4,4,5,4,5,5,4,5,5,4,4,5,5,4,4,4,4,5,4,4,4,4,4,4,5,4,4,4,4,4,4,4,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1,0,0,0,1,1,0,0,1,1,0,1,0,0,1,0,0,1,0,1,1,0,1,1,1,0,1,1,1,0,1,1,1,0,0,7,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,0,0,0)
    val template8 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,2,3,3,3,3,3,3,3,4,4,4,3,3,4,4,4,5,4,4,4,4,5,4,4,5,5,5,6,5,5,4,4,3,2,2,3,3,3,3,3,3,4,4,4,4,4,3,3,4,4,4,5,4,4,4,4,4,5,5,4,5,5,5,5,6,5,5,6,5,5,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,7,7,6,7,7,7,7,7,7,7,0,7,7,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1,1,2,2,2,2,1,7,7,7,6,6,7,7,7,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1,1,1,2,2,2)
    val template9 = arrayListOf(2,2,2,2,2,2,2,2,2,2,2,2,3,3,2,3,3,3,3,4,3,4,3,4,4,4,3,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,5,5,4,4,5,4,5,5,4,5,5,5,5,5,6,5,6,6,6,6,6,6,6,6,6,6,6,6,6,7,6,7,7,7,7,0,7,7,0,2,2,1,1,3,3,4,3,2,2,3,2,2,2,2,2,2,2,1,1,2,1,1,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,7,5,5,4,5,6,5,5,6,6,5,6,6,6,6,6,6,6,7,6,6,7,7,7,6,0,7,7,7,0,0,7,7,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1,1,2,1,1,2)
    val arialChainCode = arrayListOf(template0, template1, template2, template3, template4, template5, template6, template7, template8, template9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t4_chain_code_arial_number)

        rawImage.visibility = View.INVISIBLE
        preprediction.visibility = View.INVISIBLE
        resultPrediction.visibility = View.INVISIBLE
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

            oldImage.setImageBitmap(resizedBitmap)
            blackWhiteImageBtn.setOnClickListener {
                var blackWhiteGambar = blackWhiteImg(resizedBitmap, thresholdSeekbar.progress)
                blackWhiteImage.setImageBitmap(blackWhiteGambar.first)
                preprediction.visibility = View.VISIBLE
                recognizeBtn.setOnClickListener {
                    recognize(blackWhiteGambar, resizedBitmap)
                }
            }

        }
    }

    private fun recognize(imageMat: Pair<Bitmap, Array<Array<Int>>>, realImage: Bitmap) {
        val imgHeight = imageMat!!.first.height
        val imgWidth = imageMat.first.width
        var matriks = imageMat.second

        var boundaries = mutableMapOf<Int, Pair<Pair<Int, Int>, Pair<Int, Int>>>()
        var cluster = 0

        for (y in 0..imgHeight-1) {
            for (x in 0..imgWidth - 1) {
                if (matriks[y][x] == -1){
                    cluster += 1
                    Log.d("debug", "FloodFill cluster " + cluster)
                    boundaries.put(cluster, floodfill(matriks, y, x, -1, cluster))
                }
            }
        }

        Toast.makeText(this, "FloodFill Done", Toast.LENGTH_SHORT).show()
        Log.d("debug", "FloodFill Done")

        // Generate Chain Code
        var chainCodes = mutableMapOf<Int, Array<Int>>()

        for (y in 0..imgHeight-1) {
            for (x in 0..imgWidth - 1) {
                if(matriks[y][x] > 0 && !chainCodes.containsKey(abs(matriks[y][x])) && checkBorder(matriks, y, x)){
                    val clusterId = matriks[y][x]
                    Log.d("debug", "GenChainCode cluster " + clusterId)
                    val chainCode = generateChainCode(matriks, y, x)
                    chainCodes.put(clusterId, chainCode)
                }
            }
        }
        Toast.makeText(this, "Chain Code Done", Toast.LENGTH_SHORT).show()
        Log.d("debug", "Chain Code Done")
        // Generate Chain Code Done

        // CHECK CHAIN CODE
        Log.d("debug", "Cluster" + cluster)
        for(clus in 1..cluster) {
            var msg = "["
            for(i in 0..chainCodes.get(clus)!!.size-1) {
                msg += chainCodes.get(clus)!![i]
                msg += ","
            }
            msg += "]"
            Log.d("chainCode", "cluster = " + clus + " || size = " + chainCodes.get(clus)!!.size + " || " + msg )
        }
        // CHECK CHAIN CODE

        var thresholdArea = imgHeight * imgWidth * 0.0001
        var predictedCluster = MutableList(0, {0})
        var predictResult = mutableMapOf<Int, Int>()

//        val myService: ExecutorService = Executors.newFixedThreadPool(4)
        for(clus in 1..cluster) {
            var bound = boundaries[clus]
            var upper = bound!!.first
            var lower = bound.second
            var area = (lower.first - upper.first) * (lower.second - upper.second)
            if (area > thresholdArea) {
                debugChainCode(stretchChainCode(chainCodes.get(clus)!!, 180), clus)
//                myService.execute {
//                }
                predictResult.put(clus, predict(stretchChainCode(chainCodes.get(clus)!!, 180)))
                predictedCluster.add(clus)
            }
        }

//        myService.shutdown()
//        while (!myService.isTerminated) {
//        }
        var predictImage = Bitmap.createBitmap(realImage)

        val scale = 1
        val canvas = Canvas(predictImage)
        // new antialised Paint
        val paintText = Paint(ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paintText.setColor(Color.RED)
        // text size in pixels
        paintText.setTextSize((20 * scale).toFloat())
        // text shadow
//        paintText.setShadowLayer(1f, 0f, 1f, Color.RED)

        val paintBox = Paint(ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        paintBox.color = Color.BLUE
        paintBox.strokeWidth = 1F
        paintBox.setStyle(Paint.Style.STROKE)

        for(i in 0..predictedCluster.size-1) {
            val bounds = Rect()
            val clusterId = predictedCluster[i]
            paintText.getTextBounds(predictResult.get(clusterId).toString(), 0, predictResult.get(clusterId).toString().length, bounds)
            val x = (boundaries[clusterId]!!.second.first - bounds.width())
            val y = (boundaries[clusterId]!!.first.second  + bounds.height())

            canvas.drawText(predictResult.get(clusterId).toString(), (x * scale).toFloat(), (y * scale).toFloat(), paintText)
            val upperBound = boundaries[clusterId]!!.first
            val lowerBound = boundaries[clusterId]!!.second
            canvas.drawRect(upperBound.first.toFloat(), upperBound.second.toFloat(), lowerBound.first.toFloat(), lowerBound.second.toFloat(), paintBox)
        }

        zoomagePrediction.setImageBitmap(predictImage)
        resultPrediction.visibility = View.VISIBLE

    }

    private fun getDiffChainCode(chainCode1: Array<Int>, chainCode2: Array<Int>): Int {
        var diff = 0
        for(i in 0..chainCode1.size-1) {
            diff += min(abs(chainCode1[i] - chainCode2[i]), 8-abs(chainCode1[i] - chainCode2[i]))
        }
        return diff
    }

    private fun predict(chainCode: Array<Int>): Int {
        var distance = arrayListOf<Int>()
        for (i in 0..9) {
            distance.add(i, getDiffChainCode(chainCode, arialChainCode[i].toTypedArray()))
        }
        return distance.indexOf(distance.min())
    }

    fun debugChainCode(chainCode: Array<Int>, cluster: Int){
        var msg = "["
        for(i in 0..chainCode.size-1) {
            msg += chainCode[i]
            msg += ","
        }
        msg += "]"
        Log.d("chainCode", "cluster = " + cluster + " || size = " + chainCode.size + " || " + msg )
    }

    fun stretchChainCode(oldChainCode: Array<Int>, newSize:Int):Array<Int> {
        var newChainCode = Array(newSize, {0})
        var oldSize = oldChainCode.size
        if (oldSize < newSize) {
            var scale = newSize.toFloat() / oldSize
            for(i in 0..newSize-1) {
                newChainCode[i] = oldChainCode[min(round(i/scale).toInt(),oldChainCode.size-1)]
            }
        }else if (oldSize > newSize) {
            var scale = oldSize / newSize.toFloat()
            for(i in 0..newSize-1) {
                var iFrom = round(scale*i).toInt()
                var iTo  = min(round(iFrom + scale).toInt(), oldChainCode.size)
                newChainCode[i] = oldChainCode.toList().subList(iFrom, iTo).average().toInt()
            }
        }else{
            return oldChainCode
        }
        return newChainCode
    }

    private fun generateChainCode(matriks: Array<Array<Int>>, y: Int, x: Int): Array<Int> {
        var ret = MutableList(0,{0})
        var cluster_id = matriks[y][x]
        var dx = arrayListOf(0, 1, 1, 1, 0, -1, -1, -1)
        var dy = arrayListOf(-1, -1, 0, 1, 1, 1, 0, -1)

        var pos = Pair(x,y)
        while(matriks[pos.second][pos.first] == cluster_id) {
            matriks[pos.second][pos.first] *= -1
            var oldPos = pos
            for(i in 0..dx.size-1){
                var nx = pos.first + dx[i]
                var ny = pos.second + dy[i]
                if (nx >= 0 && ny >= 0 && ny < matriks.size && nx < matriks[ny].size && matriks[ny][nx] == cluster_id && checkBorder(matriks, ny, nx)) {
                    ret.add(i)
                    pos = Pair(nx, ny)
                    break
                }
            }
            if(pos == oldPos){
                break
            }
        }
        return ret.toTypedArray()
    }

    private fun checkBorder(matriks: Array<Array<Int>>, y: Int, x: Int): Boolean {
        var height = matriks.size
        var width = matriks[height-1].size
        if(matriks[y][x] == 0)
            return false
        if(y == 0 || x == 0 || y == height - 1 || x == width - 1)
            return true
        if (arrayListOf(matriks[y+1][x], matriks[y-1][x], matriks[y][x+1], matriks[y][x-1]).contains(0))
            return true
        return false
    }

    private fun floodfill(matriks: Array<Array<Int>>, y: Int, x: Int, from: Int, cluster: Int): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        var stack : Stack<Pair<Int, Int>> = Stack()
        var upper_bound = Pair(x, y)
        var lower_bound = Pair(x, y)
        // now --> first = y || second = x
        stack.add(Pair(y, x))
        while (!stack.empty()){
            var now = stack.pop()
            if (now.first >= matriks.size || now.first < 0 || now.second >= matriks[now.first].size || now.second < 0)
                continue
            if (matriks[now.first][now.second] != from)
                continue
            upper_bound = Pair(min(upper_bound.first, now.second), min(upper_bound.second, now.first))
            lower_bound = Pair(max(lower_bound.first, now.second), max(lower_bound.second, now.first))
            matriks[now.first][now.second] = cluster
            stack.add(Pair(now.first - 1, now.second))
            stack.add(Pair(now.first + 1, now.second))
            stack.add(Pair(now.first, now.second - 1))
            stack.add(Pair(now.first, now.second + 1))
        }
        return Pair(upper_bound, lower_bound)
    }

    private fun blackWhiteImg(image: Bitmap?, threshold: Int): Pair<Bitmap, Array<Array<Int>>> {
        var bwImage = Bitmap.createBitmap(image!!.width, image.height, image.config)
        var matriks = Array(image.height, {Array(image.width, {0})})
        for (i in 0..image.height-1) {
            for (j in 0..image.width - 1) {
                var oldPixel = image.getPixel(j,i)
                var grayPixel = ImageHelper.rgbToGrayscale(Color.red(oldPixel), Color.green(oldPixel), Color.blue(oldPixel))
                var newPixel = Color.rgb(0, 0, 0)
                matriks[i][j] = -1
                if (grayPixel > threshold) {
                    newPixel = Color.rgb(255, 255, 255)
                    matriks[i][j] = 0
                }
                bwImage.setPixel(j,i,newPixel)
            }
        }
        return Pair(bwImage, matriks)
    }
}
