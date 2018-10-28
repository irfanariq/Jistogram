package com.jrafika.jrafika.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Skeleton;

import java.util.List;

public class ImageThinningDisplayer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        image = Skeleton.thinning(image);

        Bitmap bmp = image.toBitmap();
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);

        List<Skeleton.AreaCriticalPoint> result = Skeleton.getCriticalPoints(image);
        for (Skeleton.AreaCriticalPoint area : result) {
            for (Skeleton.CriticalPoint criticalPoint : area.criticalPoints) {
                if (criticalPoint.type == Skeleton.CriticalPoint.Type.CORNER)
                    paint.setColor(Color.RED);
                else
                    paint.setColor(Color.BLUE);
                canvas.drawCircle(criticalPoint.pos.first, criticalPoint.pos.second, 6, paint);
            }
        }

        return Image.fromBitmap(bmp);
    }

}
