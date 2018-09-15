package com.pengcit.tugas.jistogram

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.data.LineData
import kotlinx.android.synthetic.main.activity_t2_jistogram_equalization.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class T2JistogramEqualization : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t2_jistogram_equalization)

        imagesScrollView.visibility = View.INVISIBLE
        histogramsScrollView.visibility = View.INVISIBLE
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
            imagesScrollView.visibility = View.VISIBLE
            oldImage.setImageBitmap(resizedBitmap)

            tugas2Title.setText("Size : " + resizedBitmap .width + " x " + resizedBitmap .height)

            var equalizedImage= ImageHelper.equalizeRGB(resizedBitmap)
            newImageHE.setImageBitmap(equalizedImage[0])
            newImageLS.setImageBitmap(equalizedImage[1])
            setDoneText()

            val myService: ExecutorService = Executors.newFixedThreadPool(4)

            val dataChartFutureHE = myService.submit(Callable<ArrayList<LineData>>{
                ImageHelper.createGraphDataRGBGray(ImageHelper.calculateImageMatrixHistogramRGB(equalizedImage[0]))
            })
            val dataChartHE = dataChartFutureHE.get()
            ImageHelper.drawHistogram(dataChartHE[ImageHelper.RED], redHistogramTitleHE, redHistogramHE, "Red", Color.RED)
            ImageHelper.drawHistogram(dataChartHE[ImageHelper.GREEN], greenHistogramTitleHE, greenHistogramHE, "Green", Color.GREEN)
            ImageHelper.drawHistogram(dataChartHE[ImageHelper.BLUE], blueHistogramTitleHE, blueHistogramHE, "Blue", Color.BLUE)
            ImageHelper.drawHistogram(dataChartHE[ImageHelper.GRAY], grayscaleHistogramTitleHE, grayscaleHistogramHE, "Gray", Color.GRAY)

            val dataChartFutureLS = myService.submit(Callable<ArrayList<LineData>>{
                ImageHelper.createGraphDataRGBGray(ImageHelper.calculateImageMatrixHistogramRGB(equalizedImage[1]))
            })
            val dataChartLS = dataChartFutureLS.get()
            ImageHelper.drawHistogram(dataChartLS[ImageHelper.RED], redHistogramTitleLS, redHistogramLS, "Red", Color.RED)
            ImageHelper.drawHistogram(dataChartLS[ImageHelper.GREEN], greenHistogramTitleLS, greenHistogramLS, "Green", Color.GREEN)
            ImageHelper.drawHistogram(dataChartLS[ImageHelper.BLUE], blueHistogramTitleLS, blueHistogramLS, "Blue", Color.BLUE)
            ImageHelper.drawHistogram(dataChartLS[ImageHelper.GRAY], grayscaleHistogramTitleLS, grayscaleHistogramLS, "Gray", Color.GRAY)

            val dataChartFutureOld = myService.submit(Callable<ArrayList<LineData>>{
                ImageHelper.createGraphDataRGBGray(ImageHelper.calculateImageMatrixHistogramRGB(resizedBitmap))
            })
            val dataChartOld = dataChartFutureOld.get()
            ImageHelper.drawHistogram(dataChartOld[ImageHelper.RED], redHistogramTitleOld, redHistogramOld, "Red", Color.RED)
            ImageHelper.drawHistogram(dataChartOld[ImageHelper.GREEN], greenHistogramTitleOld, greenHistogramOld, "Green", Color.GREEN)
            ImageHelper.drawHistogram(dataChartOld[ImageHelper.BLUE], blueHistogramTitleOld, blueHistogramOld, "Blue", Color.BLUE)
            ImageHelper.drawHistogram(dataChartOld[ImageHelper.GRAY], grayscaleHistogramTitleOld, grayscaleHistogramOld, "Gray", Color.GRAY)

            myService.shutdown()
            while (!myService.isTerminated) {
            }

            histogramsScrollView.visibility = View.VISIBLE
        }
    }

    private fun setDoneText() {
        noImageText.setText("Result")
        oldImageTitle.setText("Before")
        newImageTitleHE.setText("After Histogram Equalization")
        newImageTitleLS.setText("After Linear Stretching")
    }
}
