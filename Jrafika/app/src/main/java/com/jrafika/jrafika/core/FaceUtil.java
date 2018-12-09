package com.jrafika.jrafika.core;

import android.util.Log;
import android.util.Pair;

import java.util.List;

import static com.jrafika.jrafika.core.ColorUtil.RGBToHSV;
import static com.jrafika.jrafika.core.ColorUtil.RGBToYCB;
import static com.jrafika.jrafika.core.Util.dilate;
import static com.jrafika.jrafika.core.Util.imageFloodFill;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class FaceUtil {

    private static final int FACE_REPRESENTATION[][] = new int[][]{
            new int[]{196, 158, 145},
            new int[]{95, 69, 53},
            new int[]{160, 117, 96},
            new int[]{182, 136, 116},
            new int[]{131, 95, 77}
    };

    private static double getMean(int[] arr) {
        double sum = 0;
        int n = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += i * arr[i];
            n += arr[i];
        }
        return sum / (double) n;
    }

    private static double getVar(int[] arr) {
        double mean = getMean(arr);
        int n = 0;
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += (mean - i) * (mean - i) * arr[i];
            n += arr[i];
        }
        return sum / (double) n;
    }

    private static boolean isSkin(int r, int g, int b) {
        ColorUtil.HSV hsv = RGBToHSV(r, g, b);
        ColorUtil.YCB ycb = RGBToYCB(r, g, b);
        float h = hsv.h;
        float s = hsv.s;
        float v = hsv.v;
        int y = ycb.y;
        int cr = ycb.cr;
        int cb = ycb.cb;
        if (r > 95 && g > 40 && b > 20 && r > g && r > b && abs(r - g) > 15) {
            return (h >= 0 && h <=50.0f/360.0f && s >= 0.23 && s <= 0.68)
            || (cr > 135 && cb > 58 && y > 80 && cr <= (1.5862 * cb) + 20 &&
                    cr >= (0.3448 * cb) + 76.2069 && cr >= (-4.5652 * cb) + 234.5652 &&
                    cr <= (-1.15 * cb) + 301.75 && cr <= (-2.2857 * cb) + 432.85);
        }
        return false;
    }

    public static Image skinify(Image image) {
        Image result = Util.imageRGBToGrayscale(image);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (isSkin(image.getPixel(x, y, 0), image.getPixel(x, y, 1), image.getPixel(x, y, 2))) {
                    result.setPixel(x, y, 255);
                } else {
                    result.setPixel(x, y, 0);
                }
            }
        }
        return result;
    }

    public static List<Util.AreaBox> getFace(Image image) {
        Image skinImage = dilate(skinify(image), 5);
        return imageFloodFill(skinImage, 1024);
    }

}
