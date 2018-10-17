package com.jrafika.jrafika.core;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class Histogram {

    public static Map<Integer, Integer[]> calculateHistogram(Image image) {
        int[] pixels = image.getPixels();

        Map<Integer, Integer[]> result = new HashMap<>();
        result.put(0, new Integer[256]);
        if (image.getType() == Image.IMAGE_RGB) {
            result.put(1, new Integer[256]);
            result.put(2, new Integer[256]);
        }
        for (Integer k : result.keySet())
            for (int i = 0; i < 256; i++)
                result.get(k)[i] = 0;

        for (int pix : pixels) {
            if (image.getType() == Image.IMAGE_RGB) {
                int red = Color.red(pix);
                int green = Color.green(pix);
                int blue = Color.blue(pix);
                result.get(0)[red]++;
                result.get(1)[green]++;
                result.get(2)[blue]++;
            } else {
                int alpha = Color.alpha(pix);
                result.get(0)[alpha]++;
            }
        }

        return result;
    }

    public static Integer[] calculateGrayHistogram(Image image) {
        int[] pixels = image.getPixels();
        Integer[] result = new Integer[256];
        for (int i = 0; i < 256; i++) {
            result[i] = 0;
        }
        for (int pix : pixels) {
            result[pix]++;
        }
        return result;
    }

    public static Image equalizeHistogram(Image image) {
        image = image.clone();
        if (image.getType() == Image.IMAGE_RGB) {
            image = Util.imageRGBToGrayscale(image);
        }
        Integer[] hist = calculateGrayHistogram(image);
        int total = image.getWidth() * image.getHeight();
        int[] colorMap = new int[256];
        int cumulative = 0;
        for (int i = 0; i < 256; i++) {
            cumulative += hist[i];
            colorMap[i] = Math.floorDiv(cumulative * 256,  total);
            colorMap[i] = Math.min(255, colorMap[i]);
            colorMap[i] = Math.max(0, colorMap[i]);
        }
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pix = image.getPixel(x, y);
                image.setPixel(x, y, colorMap[pix]);
            }
        }
        return image;
    }

}
