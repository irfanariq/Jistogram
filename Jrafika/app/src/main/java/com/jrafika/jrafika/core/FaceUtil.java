package com.jrafika.jrafika.core;

import android.util.Pair;

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

    public static BoxUtil.BoundingBox getFace(Image image) {
        int[] col = new int[image.getWidth()];
        int[] row = new int[image.getHeight()];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                double minDist = Double.POSITIVE_INFINITY;
                for (int i = 0; i < FACE_REPRESENTATION.length; i++) {
                    int temp1 = image.getPixel(x, y, 0) - FACE_REPRESENTATION[i][0];
                    int temp2 = image.getPixel(x, y, 1) - FACE_REPRESENTATION[i][1];
                    int temp3 = image.getPixel(x, y, 2) - FACE_REPRESENTATION[i][2];
                    double dist = sqrt(temp1 * temp1 + temp2* temp2 + temp3 * temp3) / 390.0 * 255.0;
                    if (minDist > dist) {
                        minDist = dist;
                    }
                }
                if (minDist >= 10) {
                    col[x]++;
                    row[y]++;
                }
            }
        }

        int colMean = (int) getMean(col);
        int colWidth = (int) (sqrt(getVar(col)) * 0.75);
        int rowMean = (int) getMean(row);
        int rowWidth = (int) (sqrt(getVar(row)) * 0.75);

        return new BoxUtil.BoundingBox(
                new Pair<>(colMean - colWidth, rowMean - rowWidth),
                new Pair<>(colMean + colWidth, rowMean + rowWidth)
        );
    }

}
