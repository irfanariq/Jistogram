package com.jrafika.jrafika.core;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class FilterUtil {

    public static Image convolute(Image image, float[][] kernel) {
        int kwidth = kernel[0].length;
        int kheight = kernel.length;

        if (kwidth % 2 == 0 || kheight % 2 == 0) {
            throw new IllegalArgumentException("kernel size should be odd integer");
        }

        double[][] result = new double[image.getHeight()][image.getWidth()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double val = 0.0;
                for (int i = 0; i < kheight; i++) {
                    for (int j = 0; j < kwidth; j++) {
                        int color = image.getPixel(
                                x - (kwidth / 2) + j,
                                y - (kheight / 2) + i
                        );
                        double colorD = (double) color / 256.0;
                        val += colorD * kernel[i][j];
                    }
                }
                result[y][x] = val;
            }
        }

        Image resultImage = image.clone();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resultImage.setPixel(x, y, (int) (255.0 * max(0.0, min(1.0, result[y][x]))));
            }
        }

        return resultImage;
    }

    public static Image sobelXFilter(Image image) {
        return convolute(image, new float[][]{{1,2,1},{0,0,0},{-1,-2,-1}});
    }

    public static Image sobelYFilter(Image image) {
        return convolute(image, new float[][]{{1,0,-1},{2,0,-2},{1,0,-1}});
    }

    public static Image laplacianFilter(Image image) {
        return convolute(image, new float[][]{{0, -1, 0}, {-1, 4, -1}, {0, -1, 0}});
    }

    public static Image sobelMagFilter(Image image) {
        Image sobelX = sobelXFilter(image);
        Image sobelY = sobelYFilter(image);
        Image result = image.clone();
        for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                int cx = sobelX.getPixel(x, y);
                int cy = sobelY.getPixel(x, y);
                result.setPixel(x, y, (int) min(255, max(0, 0.5*cx + 0.5*cy)));
            }
        }
        return result;
    }

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
