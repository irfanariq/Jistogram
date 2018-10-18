package com.jrafika.jrafika.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static org.apache.commons.math3.stat.StatUtils.mean;

public class ChainCodePredictor implements ImageProcessor {

    public static final Integer[] TEMPLATE_0 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 4, 4, 3, 3, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 5, 4, 4, 5, 5, 5, 5, 5, 5, 5, 6, 6, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 0, 7, 7, 0, 0, 7, 7, 0, 0, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2};
    public static final Integer[] TEMPLATE_1 = new Integer[]{2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 5, 5, 5, 6, 5, 5, 5, 5, 6, 5, 5, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 2, 1, 1, 1, 2};
    public static final Integer[] TEMPLATE_2 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 2, 3, 3, 3, 3, 4, 4, 4, 4, 3, 4, 4, 5, 4, 4, 5, 4, 5, 4, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 4, 5, 5, 4, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 7, 7, 7, 7, 7, 6, 6, 6, 6, 6, 6, 6, 5, 6, 5, 5, 5, 4, 5, 4, 5, 7, 6, 6, 0, 0, 0, 1, 0, 0, 1, 1, 2, 1, 2};
    public static final Integer[] TEMPLATE_3 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 2, 3, 3, 3, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 5, 5, 3, 2, 2, 3, 2, 3, 3, 3, 4, 4, 3, 4, 4, 5, 4, 4, 4, 5, 4, 5, 5, 6, 5, 6, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 6, 7, 6, 7, 0, 7, 7, 0, 0, 7, 1, 2, 2, 2, 2, 4, 4, 3, 4, 3, 2, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 7, 7, 7, 6, 7, 6, 6, 6, 6, 7, 6, 6, 6, 0, 0, 0, 2, 2, 2, 2, 2, 2, 1, 2, 1, 1, 1, 1, 0, 0, 0, 0, 0, 7, 0, 7, 7, 6, 7, 6, 6, 6, 6, 6, 6, 6, 5, 5, 5, 5, 4, 5, 6, 6, 6, 6, 0, 1, 0, 0, 1, 1, 1, 1, 2, 2};
    public static final Integer[] TEMPLATE_4 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0};
    public static final Integer[] TEMPLATE_5 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 3, 3, 3, 4, 3, 4, 4, 3, 4, 4, 4, 4, 5, 4, 4, 4, 4, 5, 5, 5, 6, 5, 6, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 6, 7, 6, 7, 7, 0, 7, 0, 0, 2, 1, 2, 4, 3, 4, 3, 3, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 7, 0, 7, 7, 6, 7, 6, 6, 6, 6, 6, 6, 5, 6, 5, 6, 5, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0};
    public static final Integer[] TEMPLATE_6 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 3, 3, 3, 3, 3, 4, 4, 6, 6, 6, 6, 6, 0, 0, 7, 7, 6, 6, 7, 6, 6, 6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 4, 5, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 3, 3, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 3, 2, 3, 3, 3, 3, 4, 3, 3, 4, 4, 4, 3, 3, 4, 4, 4, 5, 5, 4, 4, 4, 4, 4, 5, 4, 5, 5, 5, 5, 5, 6, 6, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 6, 7, 7, 7, 7, 7, 0, 0, 7, 0, 7, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 2, 1, 1, 2};
    public static final Integer[] TEMPLATE_7 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 5, 5, 4, 5, 5, 5, 4, 5, 5, 4, 5, 5, 5, 4, 5, 5, 4, 5, 4, 4, 5, 4, 5, 5, 4, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0};
    public static final Integer[] TEMPLATE_8 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 3, 3, 4, 4, 4, 5, 4, 4, 4, 4, 5, 4, 4, 5, 5, 5, 6, 5, 5, 4, 4, 3, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 3, 3, 4, 4, 4, 5, 4, 4, 4, 4, 4, 5, 5, 4, 5, 5, 5, 5, 6, 5, 5, 6, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 6, 7, 7, 7, 7, 7, 7, 7, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 1, 7, 7, 7, 6, 6, 7, 7, 7, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2};
    public static final Integer[] TEMPLATE_9 = new Integer[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2, 3, 3, 3, 3, 4, 3, 4, 3, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 5, 4, 5, 5, 4, 5, 5, 5, 5, 5, 6, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 6, 7, 7, 7, 7, 0, 7, 7, 0, 2, 2, 1, 1, 3, 3, 4, 3, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 7, 5, 5, 4, 5, 6, 5, 5, 6, 6, 5, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 7, 7, 7, 6, 0, 7, 7, 7, 0, 0, 7, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 2, 1, 1, 2};

    public static final Integer[][] ARIAL_CHAIN_CODE = new Integer[][]{
            TEMPLATE_0,  TEMPLATE_1,  TEMPLATE_2,  TEMPLATE_3,  TEMPLATE_4,  TEMPLATE_5,
            TEMPLATE_6,  TEMPLATE_7,  TEMPLATE_8,  TEMPLATE_9
    };

    public static int getDiff(Integer[] a, Integer[] b) {
        int diff = 0;
        for (int i = 0; i < min(a.length, b.length); i++) {
            diff += min(abs(a[i] - b[i]), 8 - abs(a[i] - b[i]));
        }
        return diff;
    }

    public static int getDiff(List<Integer> a, List<Integer> b) {
        return getDiff(
                a.toArray(new Integer[a.size()]),
                b.toArray(new Integer[b.size()])
        );
    }

    public static int getDiff(ChainCode a, ChainCode b) {
        return getDiff(a.chain, b.chain);
    }

    public static int predict(ChainCode a) {
        if (a.chain.size() != 180) {
            a = stretchChainCode(a, 180);
        }
        int result = 1000000;
        int resultI = 0;
        for (int i = 0; i < 10; i++) {
            List<Integer> template = new ArrayList<>();
            for (Integer x : ARIAL_CHAIN_CODE[i]) {
                template.add(x);
            }

            int dist = getDiff(a.chain, template);
            if (dist < result) {
                result = dist;
                resultI = i;
            }
        }
        return resultI;
    }


    public static ChainCode stretchChainCode(ChainCode a, int size) {
        a = a.clone();
        List<Integer> newChain = new ArrayList<>();
        int oldSize = a.chain.size();
        if (oldSize < size) {
            float scale = (float) size / (float) oldSize;
            for (int i = 0; i < size; i++) {
                newChain.add(a.chain.get(min(round((float) i/scale), oldSize-1)));
            }
            a.chain = newChain;
        } else if (oldSize > size) {
            float scale = (float) oldSize / (float) size;
            for (int i = 0; i < size; i++) {
                int ifrom = round(scale * i);
                int ito = min(round(ifrom + scale), oldSize);
                
                double[] h = new double[ito - ifrom];
                for (int j = ifrom; j < ito; j++) {
                    h[j - ifrom] = a.chain.get(ifrom);
                }
                newChain.add(((int) round(mean(h))));
            }
            a.chain = newChain;
        }
        return a;
    }

    @Override
    public Image proceed(Image image) {
        Bitmap bm = image.toBitmap();
        Canvas canvas = new Canvas(bm);

        List<ChainCode> chainCodes = ChainCode.getImageChainCode(image, 100);
        for (ChainCode cc : chainCodes) {
            int prediction = predict(cc);

            Pair<Integer, Integer> lowerBound = cc.areaBox.lowerBound;
            Pair<Integer, Integer> upperBound = cc.areaBox.upperBound;

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(3);
            canvas.drawRect(upperBound.first, upperBound.second, lowerBound.first, lowerBound.second, paint);

            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(24);
            canvas.drawText(
                    prediction + "",
                    (upperBound.first + lowerBound.first) / 2,
                    lowerBound.second + 30,
                    paint
            );
        }

        return Image.fromBitmap(bm);
    }
}
