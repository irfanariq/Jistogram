package com.jrafika.jrafika.core;

import org.apache.commons.math3.geometry.partitioning.utilities.OrderedTuple;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static Image imageRGBToGrayscale(Image image) {
        if (image.getType() != Image.IMAGE_RGB)
            throw new RuntimeException("image should be RGB typed");

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
        return newImage ;
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

}
