package com.jrafika.jrafika.processor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;

import com.jrafika.jrafika.R;
import com.jrafika.jrafika.core.BoxUtil;
import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.LoaderUtil;
import com.jrafika.jrafika.core.Skeleton;
import com.jrafika.jrafika.core.TextUtil;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HandwritingPredictor implements ImageProcessor {

    private RealMatrix matrixCoef1;
    private RealMatrix matrixCoef2;
    private RealMatrix matrixCoef3;
    private RealMatrix matrixCoef4;

    private RealVector vecConstant1;
    private RealVector vecConstant2;
    private RealVector vecConstant3;
    private RealVector vecConstant4;

    public HandwritingPredictor(Context context) throws IOException {
        matrixCoef1 = MatrixUtils.createRealMatrix(
                LoaderUtil.loadDoubleMatrix(context, R.raw.coef1, 200)
        );
        matrixCoef2 = MatrixUtils.createRealMatrix(
                LoaderUtil.loadDoubleMatrix(context, R.raw.coef2, 200)
        );
        matrixCoef3 = MatrixUtils.createRealMatrix(
                LoaderUtil.loadDoubleMatrix(context, R.raw.coef3, 200)
        );
        matrixCoef4 = MatrixUtils.createRealMatrix(
                LoaderUtil.loadDoubleMatrix(context, R.raw.coef4, 96)
        );

        vecConstant1 = new ArrayRealVector(LoaderUtil.loadDouble(context, R.raw.bias1));
        vecConstant2 = new ArrayRealVector(LoaderUtil.loadDouble(context, R.raw.bias2));
        vecConstant3 = new ArrayRealVector(LoaderUtil.loadDouble(context, R.raw.bias3));
        vecConstant4 = new ArrayRealVector(LoaderUtil.loadDouble(context, R.raw.bias4));
    }

    @Override
    public Image proceed(Image image) {
        Bitmap bm = image.toBitmap();
        Canvas canvas = new Canvas(bm);

        for (AreaPrediction areaPrediction : getPredictions(image)) {
            char prediction = areaPrediction.prediction;
            Pair<Integer, Integer> lowerBound = areaPrediction.area.lowerBound;
            Pair<Integer, Integer> upperBound = areaPrediction.area.upperBound;

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(1);
            canvas.drawRect(upperBound.first - 1, upperBound.second - 1, lowerBound.first, lowerBound.second, paint);

            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(24);
            canvas.drawText(
                    prediction + "",
                    upperBound.first,
                    upperBound.second + 24,
                    paint
            );
        }

        return Image.fromBitmap(bm);
    }

    public static class AreaFeature {
        public BoxUtil.BoundingBox area;
        public double[] feature;

        public AreaFeature(BoxUtil.BoundingBox area, double[] feature) {
            this.area = area;
            this.feature = feature;
        }
    }

    public static List<AreaFeature> getFeatures(Image image) {
        image = image.clone();

        List<AreaFeature> result = new ArrayList<>();

        List<TextUtil.AreaCriticalPoint> areaCriticalPoints = TextUtil.getCriticalPoints(image);
        for (TextUtil.AreaCriticalPoint area : areaCriticalPoints) {
            int width = area.area.lowerBound.first - area.area.upperBound.first;
            int height = area.area.lowerBound.second - area.area.upperBound.second;
            int x1 = area.area.upperBound.first + width / 3;
            int x2 = x1 + (width / 3);
            int y1 = area.area.upperBound.second + height / 3;
            int y2 = y1 + (height / 3);

            double[] p11corner = new double[9];
            double[] p11intersection = new double[9];
            double[] r11 = new double[9];

            for (Skeleton.CriticalPoint criticalPoint : area.criticalPoints) {
                int i = 0;
                int j = 0;
                if (criticalPoint.pos.first < x1) {
                    j = 0;
                } else if (criticalPoint.pos.first < x2) {
                    j = 1;
                } else {
                    j = 2;
                }
                if (criticalPoint.pos.second < y1) {
                    i = 0;
                } else if (criticalPoint.pos.second < y2) {
                    i = 1;
                } else {
                    i = 2;
                }

                if (criticalPoint.type == Skeleton.CriticalPoint.Type.CORNER) {
                    p11corner[i * 3 + j]++;
                } else {
                    p11intersection[i * 3 + j]++;
                }
            }

            for (int x = area.area.upperBound.first; x < area.area.lowerBound.first; x++) {
                for (int y = area.area.upperBound.second; y < area.area.lowerBound.second; y++) {
                    if (image.getPixel(x, y) > 0) {
                        int i = 0;
                        int j = 0;
                        if (x < x1) {
                            j = 0;
                        } else if (x < x2) {
                            j = 1;
                        } else {
                            j = 2;
                        }
                        if (y < y1) {
                            i = 0;
                        } else if (y < y2) {
                            i = 1;
                        } else {
                            i = 2;
                        }
                        r11[i * 3 + j] += 1;
                    }
                }
            }
            for (int i = 0; i < 9; i++) {
                r11[i] /= width * height;
            }

            double featureArr[] = new double[109];
            int i = 0;
            for (int h = 1; h < 4; h++) {
                for (int w = 1; w < 4; w++) {
                    for (int y = 0; y < 3 - h + 1; y++) {
                        for (int x = 0; x < 3 - w + 1; x++) {
                            float corner = 0;
                            float intersect = 0;
                            float density = 0;
                            for (int dx = 0; dx < w; dx++) {
                                for (int dy = 0; dy < h; dy++) {
                                    corner += p11corner[(y + dy) * 3 + (x + dx)];
                                    intersect += p11intersection[(y + dy) * 3 + (x + dx)];
                                    density += r11[(y + dy) * 3 + (x + dx)];
                                }
                            }
                            featureArr[i] = corner;
                            featureArr[36 + i] = intersect;
                            featureArr[72 + i] = density;
                            i++;
                        }
                    }
                }
            }
            featureArr[108] = (double) height / (double) width;

            result.add(new AreaFeature(area.area.clone(), featureArr));
        }

        return result;
    }

    public static class AreaPrediction {
        public BoxUtil.BoundingBox area;
        public char prediction;

        public AreaPrediction(BoxUtil.BoundingBox area, char prediction) {
            this.area = area;
            this.prediction = prediction;
        }
    }

    public List<AreaPrediction> getPredictions(Image image) {
        List<AreaPrediction> result = new ArrayList<>();
        for (AreaFeature areaFeature : getFeatures(image)) {
            double[] temp = new double[109];
            for (int i = 0; i < 109; i++) {
                temp[i] = areaFeature.feature[i];
            }
            RealVector featureVec = new ArrayRealVector(temp);

            RealVector output = matrixCoef4.preMultiply(
                matrixCoef3.preMultiply(
                    matrixCoef2.preMultiply(
                            matrixCoef1.preMultiply(featureVec).add(vecConstant1)
                    ).add(vecConstant2)
                ).add(vecConstant3)
            ).add(vecConstant4);

            String s = "";
            RealVector v = featureVec;
            for (int i = 0; i < v.getDimension(); i++)
                s += v.getEntry(i) + " ";
            Log.d("wow", "feature " + v.getDimension() + " : " + s);

            double sumSoftmax = 0.0;
            for (int i = 0; i < 96; i++) {
                sumSoftmax += Math.exp(output.getEntry(i));
            }

            double curr = -1;
            int pred = ((int) '?');
            for (int i = 0; i < 96; i++) {
                double t = Math.exp(output.getEntry(i))/sumSoftmax;
                if (t > curr) {
                    curr = t;
                    pred = i + 32;
                }
            }
            char prediction = (char) pred;

            Log.d("wow", "prediction " + (char) pred);

            result.add(new AreaPrediction(areaFeature.area.clone(), prediction));
        }
        return result;
    }

}
