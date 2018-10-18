package com.jrafika.jrafika.core;

import android.util.Pair;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Util {

    public static final int[] DIRECTION_X = new int[]{0, 1, 1, 1, 0, -1, -1, -1};
    public static final int[] DIRECTION_Y = new int[]{-1, -1, 0, 1, 1, 1, 0, -1};

    public static Image imageRGBToGrayscale(Image image) {
        if (image.getType() != Image.IMAGE_RGB)
            throw new RuntimeException("image should be IMAGE_RGB typed");

        Image newImage = new Image(
                image.getWidth(),
                image.getHeight(),
                Image.IMAGE_GRAYSCALE,
                new int[image.getWidth() * image.getHeight()]
        );
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int r = image.getPixel(x, y, 0);
                int g = image.getPixel(x, y, 1);
                int b = image.getPixel(x, y, 2);
                int gray = (r + g + b) / 3;
                newImage.setPixel(x, y, gray);
            }
        }
        return newImage;
    }

    public static Image imageGrayscaleToBitmap(Image image, int threshold) {
        if (image.getType() != Image.IMAGE_GRAYSCALE)
            throw new RuntimeException("image should be IMAGE_GRAYSCALE typed");

        Image newImage = new Image(
                image.getWidth(),
                image.getHeight(),
                Image.IMAGE_BLACK_WHITE,
                new int[image.getHeight() * image.getWidth()]
        );
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pix = image.getPixel(x, y);
                if (pix < threshold) {
                    newImage.setPixel(x, y, 1);
                } else {
                    newImage.setPixel(x, y, 0);
                }
            }
        }
        return newImage;
    }

    public static List<Double> curveEquation(double x1, double y1, double x2, double y2) {
        RealMatrix coefficients = new Array2DRowRealMatrix(
                new double[][] {
                        { 1, x1, x1*x1, x1*x1*x1 },
                        { 1, x2, x2*x2, x2*x2*x2 },
                        { 0, 1, 2*x1, 3*x1*x1 },
                        { 0, 1, 2*x2, 3*x2*x2 }
                        },
                false);
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
        RealVector constants = new ArrayRealVector(new double[] {y1, y2, 0, 0}, false);
        RealVector solution = solver.solve(constants);

        List<Double> result = new ArrayList<>();
        result.add(solution.getEntry(0));
        result.add(solution.getEntry(1));
        result.add(solution.getEntry(2));
        result.add(solution.getEntry(3));
        return result;
    }

    public static class AreaBox {
        public Pair<Integer, Integer> upperBound;
        public Pair<Integer, Integer> lowerBound;
        public Pair<Integer, Integer> seedPoint;
        public int n;

        public AreaBox(
                Pair<Integer, Integer> upperBound,
                Pair<Integer, Integer> lowerBound,
                Pair<Integer, Integer> seedPoint,
                int n
        ) {
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
            this.seedPoint = seedPoint;
            this.n = n;
        }

        public AreaBox clone() {
            return new Util.AreaBox(
                    new Pair(upperBound.first, upperBound.second),
                    new Pair(lowerBound.first, lowerBound.second),
                    new Pair(seedPoint.first, seedPoint.second),
                    n
            );
        }
    }

    public static List<AreaBox> imageFloodFill(Image image, int thresholdArea) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getPixels().clone();

        List<AreaBox> result = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pixels[y * width + x] > 0) {
                    Stack<Pair<Integer, Integer>> st = new Stack<>();
                    Pair<Integer, Integer> seedPoint = new Pair(x, y);
                    Pair<Integer, Integer> upperBound = new Pair<>(width, height);
                    Pair<Integer, Integer> lowerBound = new Pair<>(0, 0);
                    int npoint = 0;

                    st.push(new Pair<>(x, y));
                    while (!st.empty()) {
                        Pair<Integer, Integer> topStack = st.pop();
                        int cx = topStack.first;
                        int cy = topStack.second;
                        if (pixels[cy * width + cx] > 0) {
                            pixels[cy * width + cx] = 0;
                            npoint++;

                            upperBound = new Pair(
                                    min(upperBound.first, cx),
                                    min(upperBound.second, cy)
                            );
                            lowerBound = new Pair(
                                    max(lowerBound.first, cx + 1),
                                    max(lowerBound.second, cy + 1)
                            );

                            for (int di = 0; di < 8; di++) {
                                int nx = cx + DIRECTION_X[di];
                                int ny = cy + DIRECTION_Y[di];
                                if (nx >= 0 && nx < width &&
                                        ny >= 0 && ny < height &&
                                        pixels[ny * width + nx] > 0) {
                                    st.push(new Pair(nx, ny));
                                }
                            }
                        }
                    }

                    int area = (lowerBound.second - upperBound.second)
                            * (lowerBound.first - upperBound.second);
                    if (thresholdArea < 0 || area >= thresholdArea) {
                        result.add(new AreaBox(upperBound, lowerBound, seedPoint, npoint));
                    }
                }
            }
        }
        return result;
    }

    public static List<AreaBox> imageFloodFill(Image image) {
        return imageFloodFill(image, -1);
    }

}
