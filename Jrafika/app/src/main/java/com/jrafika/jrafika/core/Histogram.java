package com.jrafika.jrafika.core;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class Histogram {

    public static Map<Integer, Integer[]> calculateHistogram(Image image) {
        int[] pixels = image.getPixels();

        Map<Integer, Integer[]> result = new HashMap<>();
        result.put(0, new Integer[256]);
        if (image.getChannel() > 1) {
            result.put(1, new Integer[256]);
            result.put(2, new Integer[256]);
        }
        for (Integer k : result.keySet())
            for (int i = 0; i < 256; i++)
                result.get(k)[i] = 0;

        for (int pix : pixels) {
            if (image.getChannel() > 1) {
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

}
