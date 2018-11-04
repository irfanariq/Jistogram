package com.jrafika.jrafika.core;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
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

    public static Image medianFilter(Image image) {
        if (image.getType() == Image.IMAGE_RGB) {
            image = Util.imageRGBToGrayscale(image);
        }
        Image result = image.clone();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                List<Integer> values = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    int color = image.getPixel(x - 1 + i % 3,y - 1 + i / 3);
                    values.add(color);
                }
                values.sort(Integer::compare);
                result.setPixel(x, y, values.get(4));
            }
        }
        return result;
    }

    public static Image averageFilter(Image image) {
        if (image.getType() == Image.IMAGE_RGB) {
            image = Util.imageRGBToGrayscale(image);
        }
        Image result = image.clone();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int sum = 0;
                for (int i = 0; i < 9; i++) {
                    int color = image.getPixel(x - 1 + i % 3,y - 1 + i / 3);
                    sum += color;
                }
                result.setPixel(x, y, sum / 9);
            }
        }
        return result;
    }

    public static Image differenceFilter(Image image) {
        if (image.getType() == Image.IMAGE_RGB) {
            image = Util.imageRGBToGrayscale(image);
        }
        Image result = image.clone();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int midColor = image.getPixel(x, y);
                int targetColor = 0;
                for (int i = 0; i < 9; i++) {
                    int color = image.getPixel(x - 1 + i % 3,y - 1 + i / 3);
                    targetColor = max(targetColor, abs(color - midColor));
                }
                result.setPixel(x, y, targetColor);
            }
        }
        return result;
    }

    public static Image gradientOperatorFilter(Image image) {
        if (image.getType() == Image.IMAGE_RGB) {
            image = Util.imageRGBToGrayscale(image);
        }
        Image result = image.clone();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int targetColor = 0;
                for (int i = 0; i < 4; i++) {
                    int color1 = image.getPixel(x - 1 + i % 3,y - 1 + i / 3);
                    int color2 = image.getPixel(x - 1 + 2 - (i % 3),y - 1 + 2 - (i / 3));
                    targetColor = max(targetColor, abs(color1 - color2));
                }
                result.setPixel(x, y, targetColor);
            }
        }
        return result;
    }

}
