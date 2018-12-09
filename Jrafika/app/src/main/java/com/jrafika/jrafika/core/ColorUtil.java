package com.jrafika.jrafika.core;

import android.graphics.Color;
import android.util.Log;

public class ColorUtil {

    public static class YCB {
        public int y;
        public int cr;
        public int cb;

        public YCB() {
        }

        public YCB(int y, int cr, int cb) {
            this.y = y;
            this.cr = cr;
            this.cb = cb;
        }
    }

    public static class HSV {
        public float h;
        public float s;
        public float v;

        public HSV(float h, float s, float v) {
            this.h = h;
            this.s = s;
            this.v = v;
        }

        public HSV() {
        }
    }

    public static YCB RGBToYCB(int r, int g, int b) {
        int y = 16 + (((r << 6) + (r << 1)+ (g << 7) + g + (b << 4) + (b << 3) + b) >> 8);
        int cr = 128 + ((-((r << 5) + (r << 2) + (r << 1)) - ((g << 6) + (g << 3) + (g << 1)) + (b << 7) - (b << 4)) >> 8);
        int cb = 128 + (((r << 7) - (r << 4) - ((g << 6) + (g << 5) - (g << 1)) - ((b << 4) + (b << 1))) >> 8);
        return new YCB(y, cr, cb);
    }

    public static HSV RGBToHSV(int r, int g, int b) {
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);
        return new HSV(hsv[0] / 360.0f, hsv[1], hsv[2]);
    }

}
