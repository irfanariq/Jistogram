package com.jrafika.jrafika.core;

import android.graphics.Color;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
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
    
    public static Integer[] calculateCumulativeHistogram(Integer[] histogram) {
        Integer[] result = new Integer[histogram.length];
        int cumulative = 0;
        for (int i = 0; i < histogram.length; i++) {
            cumulative += histogram[i];
            result[i] = cumulative;
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

    public static Image stretchHistogram(Image image, int inputMin, int inputMax, int outputMin, int outputMax) {
        image = image.clone();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getPixel(x, y);
                if (color < inputMin) {
                    image.setPixel(x, y, 0);
                } else if (color > inputMax) {
                    image.setPixel(x, y, 255);
                } else {
                    int d = (color - inputMin) * (outputMax - outputMin) / (inputMax - inputMin) + outputMin;
                    image.setPixel(x, y, d);
                }
            }
        }
        return image;
    }

    public static Image specifyHistogram(Image image, Integer[] targetHistogram) {
        image = image.clone();
        Integer[] histReal = calculateGrayHistogram(image);
        Integer[] cumuReal = calculateCumulativeHistogram(histReal);
        Integer[] cumuTarg = calculateCumulativeHistogram(targetHistogram);

        int lastTarget = cumuTarg[cumuTarg.length - 1];
        int lastReal = cumuReal[cumuReal.length - 1];
        float scale = (float) lastReal / (float) lastTarget;
        for (int i = 0; i < cumuTarg.length; i++) {
            cumuTarg[i] = (int) Math.floor((float) cumuTarg[i] * scale);
        }

        int[] mapping = new int[cumuReal.length];
        for (int i = 0; i < cumuReal.length; i++) {
            mapping[i] = 0;
            int currMin = Math.abs(cumuReal[i] - cumuTarg[0]);
            for (int j = 0; j < cumuTarg.length; j++) {
                int diff = Math.abs(cumuReal[i] - cumuTarg[j]);
                if (diff < currMin) {
                    mapping[i] = j;
                    currMin = diff;
                }
            }
        }

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pix = image.getPixel(x, y);
                image.setPixel(x, y, mapping[pix]);
            }
        }
        return image;
    }

}
