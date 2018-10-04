package com.pengcit.tugas.jistogram

import android.content.Intent
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import koma.pow
import kotlinx.android.synthetic.main.activity_t6_predict_thinning.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


class T6PredictThinning : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t6_predict_thinning)

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
                    thinPredictImage(blackWhiteGambar)
                    thinImageLayout.visibility = View.VISIBLE
                    thinImageLayoutOnly.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun thinPredictImage(imageMat: Pair<Bitmap, Array<Array<Int>>>) {
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
                matriks[mustDeleted[i].second][mustDeleted[i].first] = 0
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
                matriks[mustDeleted[i].second][mustDeleted[i].first] = 0
            }
        }

        var thinnedImage = Bitmap.createBitmap(bwImage)
        var thinnedImageOnly = Bitmap.createBitmap(bwImage.width, bwImage.height, bwImage.config)

//        val scale = 1
//        val canvas = Canvas(thinnedImage)
//
//        val paintBox = Paint(Paint.ANTI_ALIAS_FLAG)
//        paintBox.color = Color.CYAN
////        paintBox.strokeWidth = 2F
////        paintBox.setStyle(Paint.Style.STROKE)
        for (y in 0..imgHeight-1){
            for(x in 0..imgWidth-1){
                if(matriks[y][x] != 0){
//                    canvas.drawPoint(x.toFloat(), y.toFloat(), paintBox)
                    thinnedImage.setPixel(x,y, Color.CYAN)
//                    thinnedImageOnly.setPixel(x,y,Color.BLACK)
                }
            }
        }
        Toast.makeText(this, "Thinning Done", Toast.LENGTH_SHORT).show()
        thinImage.setImageBitmap(thinnedImage)
        thinImageOnly.setImageBitmap(thinnedImageOnly)

        predictThinnedImage(matriks, thinnedImageOnly)
    }

    private fun predictThinnedImage(matriks: Array<Array<Int>>, thinnedImageOnly: Bitmap?) {
        val imgHeight = thinnedImageOnly!!.height
        val imgWidth = thinnedImageOnly.width

        var visited = Array(imgHeight, {Array(imgWidth, {0})})
        val dx = arrayListOf(0, 1, 1, 1, 0, -1, -1, -1)
        val dy = arrayListOf(-1, -1, 0, 1, 1, 1, 0, -1)

        var resCorners = arrayListOf<ArrayList<Pair<Int, Int>>>()
        var resIntersection = arrayListOf<ArrayList<Pair<Int, Int>>>()
        var resUpperBound = arrayListOf<Pair<Int, Int>>()
        var resLowerBound = arrayListOf<Pair<Int, Int>>()

        for (y in 0..imgHeight-1) {
            for (x in 0..imgWidth-1) {
                if (matriks[y][x] != 0) {
                    var corners = arrayListOf<Pair<Int, Int>>()
                    var intersections = arrayListOf<Pair<Int, Int>>()
                    var upperBound = Pair(imgWidth, imgHeight)
                    var lowerBound = Pair(0, 0)
                    var perimeter = 0

                    // FLOOD FILL
                    var stack : Stack<Pair<Int, Int>> = Stack()
                    stack.add(Pair(x,y))
                    while (!stack.isEmpty()) {
                        var popPair = stack.pop()
                        var cx = popPair.first
                        var cy = popPair.second
                        if (visited[cy][cx] == 1)
                            continue
                        visited[cy][cx] = 1

                        upperBound = Pair(min(upperBound.first, cx), min(upperBound.second, cy))
                        lowerBound = Pair(max(lowerBound.first, cx), max(lowerBound.second, cy))
                        perimeter += 1
                        if (isCorner(matriks, cy ,cx)) {
                            corners.add(Pair(cx, cy))
                        }
                        if (isIntersection(matriks, cy, cx)) {
                            intersections.add(Pair(cx, cy))
                        }

                        for(i in 0..dx.size-1 ) {
                            var nx = cx + dx[i]
                            var ny = cy + dy[i]
                            if (nx >= 0 && nx < imgWidth && ny >= 0 && ny < imgHeight && visited[ny][nx] == 0 && matriks[ny][nx] != 0) {
                                stack.push(Pair(nx, ny))
                            }
                        }
                    }

                    // REMOVE SOME CORNERS
                    var realCorner = arrayListOf<Pair<Int, Int>>()
                    for (iCorner in 0..corners.size - 1) {
                        realCorner.add(corners[iCorner])
                        for (iIntersect in 0..intersections.size - 1) {
                            // there is intersection point near this corner point
                            if (normalisasi(corners[iCorner], intersections[iIntersect]) < perimeter * 0.039) {
                                realCorner.remove(corners[iCorner])

                                var currentCorner = corners[iCorner]
                                Log.d("remove corner", "hapus corner" + currentCorner.first + " " + currentCorner.second)
                                var s = 1
                                // remove corner until the corner are gone
                                while (s == 1) {
                                    matriks[currentCorner.second][currentCorner.first] = 0
                                    var ns = generateNeighbour(matriks, currentCorner.second, currentCorner.first)
                                    s = 0
                                    for (iDirection in 0..ns.size - 1) {
                                        s += ns[iDirection]
                                        if (ns[iDirection] > 0) {
                                            currentCorner = Pair(currentCorner.first + dx[iDirection], currentCorner.second + dy[iDirection])
                                        }
                                    }
                                }
                                break
                            }
                        }
                    }

                    // RECALCULATE INTERSECTIONS
                    var realIntersection = arrayListOf<Pair<Int, Int>>()
                    for (i in 0..intersections.size-1){
                        if (isIntersection(matriks, intersections[i].second, intersections[i].first)) {
                            realIntersection.add(Pair(intersections[i].first, intersections[i].second))
                        }
                    }

                    if (perimeter > 20) {
                        resUpperBound.add(upperBound)
                        resLowerBound.add(lowerBound)
                        resCorners.add(realCorner)
                        resIntersection.add(realIntersection)
                    }
                }
            }
        }

        // PREDICT & DRAW
        val scale = 1
        val canvas = Canvas(thinnedImageOnly)

        val paintBox = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBox.color = Color.CYAN
        paintBox.strokeWidth = 1F
        paintBox.setStyle(Paint.Style.STROKE)

        val paintBox2 = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBox2.color = Color.RED
        paintBox2.strokeWidth = 1F
        paintBox2.setStyle(Paint.Style.STROKE)

        val paintBox3 = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBox3.color = Color.GREEN
        paintBox3.strokeWidth = 1F
        paintBox3.setStyle(Paint.Style.STROKE)

        val paintBoxText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBoxText.setColor(Color.RED)
        paintBoxText.setTextSize((20 * scale).toFloat())

        Log.d("cluster", "res corners size" + resCorners.size)
        Log.d("cluster", "res intersection size" + resIntersection.size)
        Log.d("cluster", "res upperbound size" + resUpperBound.size)
        Log.d("cluster", "res lowerboundsize" + resLowerBound.size)

        for (y in 0..imgHeight-1){
            for(x in 0..imgWidth-1){
                if(matriks[y][x] != 0){
//                    canvas.drawPoint(x.toFloat(), y.toFloat(), paintBox)
//                    thinnedImage.setPixel(x,y, Color.CYAN)
                    thinnedImageOnly.setPixel(x,y,Color.BLACK)
                }
            }
        }

        var predictions = arrayListOf<Int>()
        for(i in 0..resCorners.size-1) {
            for (j in 0..resCorners[i].size-1) {
                canvas.drawCircle(resCorners[i][j].first.toFloat() ,resCorners[i][j].second.toFloat(),5f, paintBox)
            }
            for (j in 0..resIntersection[i].size-1) {
                canvas.drawCircle(resIntersection[i][j].first.toFloat() ,resIntersection[i][j].second.toFloat(),5f, paintBox2)
            }
            canvas.drawRect(resUpperBound[i].first.toFloat(), resUpperBound[i].second.toFloat(), resLowerBound[i].first.toFloat(), resLowerBound[i].second.toFloat(), paintBox3)


            var objectWidth = resLowerBound[i].first - resUpperBound[i].first
            var objectHeight = resLowerBound[i].second - resUpperBound[i].second

            var guess = -1
            if (resCorners[i].size == 0) {
                if (resIntersection[i].size == 0) {
                    guess = 0
                } else {
                    guess = 8
                }
            }else if (resCorners[i].size == 1) {
                if (resIntersection[i].size == 1) {
                    var cornerY = resCorners[i][0].second
                    var intersectionY = resIntersection[i][0].second
                    if (cornerY < intersectionY)
                        guess = 6
                    else
                        guess = 9
                }

            }else if (resCorners[i].size == 2) {
                if (resIntersection[i].size == 1) {
                    guess = 4
                }else if (resIntersection[i].size == 0) {
                    if (resCorners[i][0].first - resUpperBound[i].first < objectWidth / 2 && resCorners[i][1].first - resUpperBound[i].first < objectWidth / 2) {
                        guess = 4
                    }else if((resCorners[i][0].first - resCorners[i][1].first) * (resCorners[i][0].second - resCorners[i][1].second) > 0) {
                        guess = 2
                    }else{
                        guess = 5
                    }
                }

            }else if (resCorners[i].size == 3) {
                if (resIntersection[i].size == 1) {
                    var intersectX = resIntersection[i][0].first
                    var cornerX = 0
                    for (j in 0..resCorners[i].size-1) {
                        cornerX = if(resCorners[i][j].first > cornerX) resCorners[i][j].first else cornerX
                    }
                    if (intersectX > cornerX) {
                        guess = 3
                    }else
                        guess = 1
                }
            }

            val bounds = Rect()
            paintBoxText.getTextBounds(guess.toString(), 0, guess.toString().length, bounds)
            val x = (resUpperBound[i].first + bounds.width())
            val y = (resLowerBound[i].second  - bounds.height())

            canvas.drawText(guess.toString(), (x * scale).toFloat(), (y * scale).toFloat(), paintBoxText)
            val upperBound = resUpperBound[i]
            val lowerBound = resLowerBound[i]
            canvas.drawRect(upperBound.first.toFloat(), upperBound.second.toFloat(), lowerBound.first.toFloat(), lowerBound.second.toFloat(), paintBox)

        }
    }

    private fun normalisasi(pair: Pair<Int, Int>, pair1: Pair<Int, Int>): Double {
        return sqrt(pow((pair.first - pair1.first), 2) + pow((pair.second - pair1.second), 2))
    }

    private fun isCorner(matriks: Array<Array<Int>>, y: Int, x: Int): Boolean {
        return matriks[y][x] != 0 && generateNeighbour(matriks, y, x).sum() == 1
    }

    private fun isIntersection(matriks: Array<Array<Int>>, y: Int, x: Int): Boolean {
        val ps = generateNeighbour(matriks, y, x)
        val n = ps.sum()
        var sp = 0
        for(i in 0..ps.size-1){
            var j = if (i != ps.size-1) (i + 1) else 0
            sp += if (ps[i] != 0 && ps[j] == 0) 1 else 0
        }
        return matriks[y][x] != 0 && n > 2 && sp > 2
    }

    private fun generateNeighbour(matriks: Array<Array<Int>>, y:Int, x: Int): ArrayList<Int> {
        val height = matriks.size
        val width = matriks[height-1].size
        val p2 = if (y > 0) matriks[y-1][x] else 0
        val p3 = if (y > 0 && x < width-1) matriks[y-1][x+1] else 0
        val p4 = if (x < width-1) matriks[y][x+1] else 0
        val p5 = if (y < height-1 && x < width-1) matriks[y+1][x+1] else 0
        val p6 = if (y < height-1) matriks[y+1][x] else 0
        val p7 = if (y < height-1 && x > 0) matriks[y+1][x-1] else 0
        val p8 = if (x > 0) matriks[y][x-1  ] else 0
        val p9 = if (y > 0 && x > 0) matriks[y-1][x-1] else 0
        val ps = arrayListOf(p2,p3,p4,p5,p6,p7,p8,p9)
        return ps;
    }

    private fun checkDelete1(matriks: Array<Array<Int>>, y: Int, x: Int): Boolean {
        if (matriks[y][x] == 0)
            return false
        val ps = generateNeighbour(matriks, y, x)
        var np = 0
        ps.forEach{x -> if (x != 0) np +=1}
        var sp = 0
        for(i in 0..ps.size-1){
            var j = if (i != ps.size-1) (i + 1) else 0
            sp += if (ps[i] != 0 && ps[j] == 0) 1 else 0
        }
        val ret = (2 <= np) && (np <= 6) && (sp == 1) && (ps[2] == 0 || ps[4] == 0 || (ps[0] == 0 && ps[6] == 0))
//        Log.d("check delete 1", "np " + np + "sp '" + sp  + "ret '" + ret )
        return ret
    }

    private fun checkDelete2(matriks: Array<Array<Int>>, y: Int, x: Int): Boolean {
        if (matriks[y][x] == 0)
            return false
        val ps = generateNeighbour(matriks, y, x)
        var np = 0
        ps.forEach{x -> if (x != 0) np +=1}
        var sp = 0
        for(i in 0..ps.size-1){
            var j = if (i != ps.size-1) (i + 1) else 0
            sp += if (ps[i] != 0 && ps[j] == 0) 1 else 0
        }
        val ret = (2 <= np) && (np <= 6) && (sp == 1) && (ps[0] == 0 || ps[6] == 0 || (ps[2] == 0 && ps[4] == 0))
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
                matriks[i][j] = 1
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
