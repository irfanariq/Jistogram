package com.jrafika.jrafika.core;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class FilterUtil {

    public static Image medianFilter(Image image, int kernelSize) {
        if (kernelSize % 2 == 0) {
            throw new IllegalArgumentException("kernelSize should be odd integer");
        }

        int channel = 1;
        if (image.getType() == Image.IMAGE_RGB) {
            channel = 3;
        }

        Image result = image.clone();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int[] val = new int[channel];
                for (int c = 0; c < channel; c++) {
                    List<Integer> values = new ArrayList<>();
                    for (int i = 0; i < kernelSize * kernelSize; i++) {
                        int color = image.getPixel(
                                x - (kernelSize / 2) + i % kernelSize,
                                y - (kernelSize / 2) + i / kernelSize,
                                c
                        );
                        values.add(color);
                    }
                    values.sort(Integer::compare);
                    val[c] = values.get(kernelSize * kernelSize / 2);
                }

                if (val.length == 1) {
                    result.setPixel(x, y, val[0]);
                } else {
                    result.setPixel(x, y, Color.rgb(val[0], val[1], val[2]));
                }
            }
        }

        return result;
    }

}
