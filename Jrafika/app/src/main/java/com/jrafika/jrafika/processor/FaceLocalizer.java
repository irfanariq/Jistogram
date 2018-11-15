package com.jrafika.jrafika.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import com.jrafika.jrafika.core.BoxUtil;
import com.jrafika.jrafika.core.FaceUtil;
import com.jrafika.jrafika.core.Image;

public class FaceLocalizer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        Bitmap bm = image.toBitmap();
        Canvas canvas = new Canvas(bm);

        BoxUtil.BoundingBox boundingBox = FaceUtil.getFace(image);

        Pair<Integer, Integer> lowerBound = boundingBox.lowerBound;
        Pair<Integer, Integer> upperBound = boundingBox.upperBound;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
        canvas.drawRect(upperBound.first, upperBound.second, lowerBound.first, lowerBound.second, paint);

        return Image.fromBitmap(bm);
    }

}
