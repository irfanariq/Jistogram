package com.jrafika.jrafika.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Util;

import java.util.List;

import static com.jrafika.jrafika.core.FaceUtil.getFace;

public class FaceLocalizer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        Bitmap bm = image.toBitmap();
        Canvas canvas = new Canvas(bm);

        List<Util.AreaBox> boundingBoxes = getFace(image);

        for (Util.AreaBox area : boundingBoxes) {
            Pair<Integer, Integer> upperBound = area.upperBound;
            Pair<Integer, Integer> lowerBound = area.lowerBound;

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(1);
            canvas.drawRect(upperBound.first - 1, upperBound.second - 1, lowerBound.first, lowerBound.second, paint);
        }

        return Image.fromBitmap(bm);
    }

}
