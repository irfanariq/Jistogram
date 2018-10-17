package com.jrafika.jrafika.core;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Image {

    public static final int IMAGE_RGB = 0;
    public static final int IMAGE_GRAYSCALE = 1;
    public static final int IMAGE_BLACK_WHITE = 2;
    public static final int IMAGE_THINNED = 3;
    public static final int IMAGE_THINNED_PRUNE = 4;

    private int width;
    private int height;
    private int channel;
    private int type;
    private int pixels[];

    public Image() {
    }

    public Image(int width, int height, int channel, int type, int[] pixels) {
        this.width = width;
        this.height = height;
        this.channel = channel;
        this.type = type;
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannel() {
        return channel;
    }

    public int getPixel(int x, int y, int channel) {
        int color = pixels[y * width + x];
        if (channel == 0)
            return this.channel == 0 ? Color.alpha(color) : Color.red(color);
        else if (channel == 1)
            return Color.green(pixels[y * width + x]);
        else
            return Color.blue(pixels[y * width + x]);
    }

    public int getPixel(int x, int y) {
        return getPixel(x, y, 0);
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getType() {
        return type;
    }

    public static Image fromBitmap(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Bitmap.Config config = bitmap.getConfig();
        int pixels[] = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        if (config.equals(Bitmap.Config.ALPHA_8)) {
            return new Image(width, height, 1, IMAGE_GRAYSCALE, pixels);
        } else {
            return new Image(width, height, 3, IMAGE_RGB, pixels);
        }
    }

//    @Override
//    public String toString() {
//        private int width;
//        private int height;
//        private int channel;
//        private int type;
//        private int pixels[];
//
//        ByteBuffer
//        return "";
//    }

}
