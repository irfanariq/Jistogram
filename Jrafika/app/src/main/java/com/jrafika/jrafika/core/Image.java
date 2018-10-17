package com.jrafika.jrafika.core;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import java.nio.ByteBuffer;

public class Image {

    public static final int IMAGE_RGB = 0;
    public static final int IMAGE_GRAYSCALE = 1;
    public static final int IMAGE_BLACK_WHITE = 2;
    public static final int IMAGE_THINNED = 3;
    public static final int IMAGE_THINNED_PRUNE = 4;

    private int width;
    private int height;
    private int type;
    private int pixels[];

    public Image() {
    }

    public Image(int width, int height, int type, int[] pixels) {
        this.width = width;
        this.height = height;
        this.type = type;
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixel(int x, int y, int channel) {
        int color = pixels[y * width + x];
        if (channel == 0) {
            switch (this.type) {
                case IMAGE_RGB: return Color.red(color);
                default: return color;
            }
        } else if (channel == 1)
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

    public void setPixel(int x, int y, int color) {
        if (type == IMAGE_RGB) {
            pixels[y * width + x] = color;
        } else if (type == IMAGE_GRAYSCALE) {
            pixels[y * width + x] = color & 0xff;
        } else {
            pixels[y * width + x] = color > 0 ? 1 : 0;
        }
    }

    public int getType() {
        return type;
    }

    public Image clone() {
        int[] newPixels = pixels.clone();
        return new Image(width, height, type, newPixels);
    }

    public static Image fromBitmap(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int pixels[] = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return new Image(width, height, IMAGE_RGB, pixels);
    }

    public static Image fromBytes(byte[] input) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(input);
        int width = byteBuffer.getInt();
        int height = byteBuffer.getInt();
        int type = byteBuffer.getInt();
        int[] pixels = new int[width * height];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = byteBuffer.getInt();
        }
        return new Image(width, height, type, pixels);
    }

    public static Image fromBase64String(String input) {
        byte[] buff = Base64.decode(input, Base64.DEFAULT);
        return fromBytes(buff);
    }

    public Bitmap toBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(width, height,  Bitmap.Config.ARGB_8888);
        int[] copyPixels = pixels;
        if (type != IMAGE_RGB) {
            copyPixels = copyPixels.clone();
            for (int i = 0; i < copyPixels.length; i++) {
                int color = copyPixels[i];
                if (type == IMAGE_GRAYSCALE)
                    copyPixels[i] = Color.argb(255, color, color, color);
                else
                    copyPixels[i] = color > 0 ? Color.BLACK : Color.WHITE;
            }
        }
        bitmap.setPixels(copyPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public String toString() {
        ByteBuffer buffer = ByteBuffer.allocate(width * height * 4 + 16);
        buffer.putInt(width);
        buffer.putInt(height);
        buffer.putInt(type);
        for (int pix : pixels) {
            buffer.putInt(pix);
        }
        return Base64.encodeToString(buffer.array(), Base64.DEFAULT);
    }

}
