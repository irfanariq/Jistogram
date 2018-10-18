package com.jrafika.jrafika.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SkeletonPredictor implements ImageProcessor {

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
            paint.setStrokeWidth(3);
            canvas.drawRect(upperBound.first, upperBound.second, lowerBound.first, lowerBound.second, paint);

            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(24);
            canvas.drawText(
                    prediction + "",
                    (upperBound.first + lowerBound.first) / 2,
                    lowerBound.second + 30,
                    paint
            );
        }

        return Image.fromBitmap(bm);
    }

    public static class Feature {
        int[] p11corner;
        int[] p12corner;
        int[] p21corner;
        int[] p22corner;
        int[] p31corner;
        int[] p13corner;

        int nCorner;

        int[] p11intersection;
        int[] p12intersection;
        int[] p21intersection;
        int[] p22intersection;
        int[] p31intersection;
        int[] p13intersection;

        int nIntersection;

        float[] r11;
        float ratio;

        public Feature() {
            p11corner = new int[9];
            p12corner = new int[6];
            p21corner = new int[6];
            p22corner = new int[4];
            p31corner = new int[3];
            p13corner = new int[3];

            nCorner = 0;

            p11intersection = new int[9];
            p12intersection = new int[6];
            p21intersection = new int[6];
            p22intersection = new int[4];
            p31intersection = new int[3];
            p13intersection = new int[3];

            nIntersection = 0;

            r11 = new float[9];
            ratio = 1;
        }

        public Feature(int[] p11corner, int[] p12corner, int[] p21corner, int[] p22corner, int[] p31corner, int[] p13corner, int nCorner, int[] p11intersection, int[] p12intersection, int[] p21intersection, int[] p22intersection, int[] p31intersection, int[] p13intersection, int nIntersection, float[] r11, float ratio) {
            this.p11corner = p11corner;
            this.p12corner = p12corner;
            this.p21corner = p21corner;
            this.p22corner = p22corner;
            this.p31corner = p31corner;
            this.p13corner = p13corner;
            this.nCorner = nCorner;
            this.p11intersection = p11intersection;
            this.p12intersection = p12intersection;
            this.p21intersection = p21intersection;
            this.p22intersection = p22intersection;
            this.p31intersection = p31intersection;
            this.p13intersection = p13intersection;
            this.nIntersection = nIntersection;
            this.r11 = r11;
            this.ratio = ratio;
        }
    }

    public static class AreaFeature {
        public Util.AreaBox area;
        public Feature feature;

        public AreaFeature(Util.AreaBox area, Feature feature) {
            this.area = area;
            this.feature = feature;
        }
    }

    public static List<AreaFeature> getFeatures(Image image) {
        image = image.clone();

        List<AreaFeature> result = new ArrayList<>();

        List<Skeleton.AreaCriticalPoint> areaCriticalPoints = Skeleton.getCriticalPoints(image);
        for (Skeleton.AreaCriticalPoint area : areaCriticalPoints) {
            int width = area.area.lowerBound.first - area.area.upperBound.first;
            int height = area.area.lowerBound.second - area.area.upperBound.second;
            int x1 = area.area.upperBound.first + width / 3;
            int x2 = area.area.upperBound.first + (width / 3) * 2;
            int y1 = area.area.upperBound.second + height / 3;
            int y2 = area.area.upperBound.second + (height / 3) * 2;

            int[] p11corner = new int[9];
            int[] p11intersection = new int[9];

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

            int[] p12corner = new int[6];
            int[] p21corner = new int[6];
            int[] p22corner = new int[4];
            int[] p31corner = new int[3];
            int[] p13corner = new int[3];

            int[] p12intersection = new int[6];
            int[] p21intersection = new int[6];
            int[] p22intersection = new int[4];
            int[] p31intersection = new int[3];
            int[] p13intersection = new int[3];

            for (int i = 0; i < 3; i++) {
                p21corner[i * 2] = p11corner[i * 3] + p11corner[i * 3 + 1];
                p21corner[1 * 2 + 1] = p11corner[i * 3 + 1] + p11corner[i * 3 + 2];
                p21intersection[i * 2] = p11intersection[i * 3] + p11intersection[i * 3 + 1];
                p21intersection[1 * 2 + 1] = p11intersection[i * 3 + 1] + p11intersection[i * 3 + 2];

                p12corner[i] = p11corner[i] + p11corner[i + 3];
                p12corner[i + 3] = p11corner[i + 3] + p11corner[i + 6];
                p12intersection[i] = p11intersection[i] + p11intersection[i + 3];
                p12intersection[i + 3] = p11intersection[i + 3] + p11intersection[i + 6];
            }

            for (int i = 0; i < 2; i++) {
                p22corner[i] = p21corner[i] + p21corner[i + 2];
                p22corner[i + 1] = p21corner[i] + p21corner[i + 2];

                p22intersection[i] = p21intersection[i] + p21intersection[i + 2];
                p22intersection[i + 1] = p21intersection[i] + p21intersection[i + 2];
            }

            for (int i = 0; i < 3; i++) {
                p31corner[i] = p11corner[i * 3] + p11corner[i * 3 + 1] + p11corner[i * 3 + 2];
                p31intersection[i] = p11intersection[i * 3] + p11intersection[i * 3 + 1] + p11intersection[i * 3 + 2];

                p13corner[i] = p12corner[i] + p11corner[6 + i];
                p13intersection[i] = p12intersection[i] + p11intersection[6 + i];
            }

            int nCorner = p31corner[0] + p31corner[1] + p31corner[2];
            int nIntersection = p31corner[0] + p31corner[1] + p31corner[2];
            float ratio = (float) width / (float) height;

            float[] r11 = new float[9];

            for (int x = area.area.upperBound.first; x < area.area.lowerBound.first; x++) {
                for (int y = area.area.upperBound.second; y < area.area.lowerBound.second; y++) {
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
            for (int i = 0; i < 9; i++) {
                r11[i] /= width * height;
            }

            Feature feature = new Feature(
                    p11corner, p12corner, p21corner, p22corner, p31corner, p13corner, nCorner,
                    p11intersection, p12intersection, p21intersection, p22intersection,
                    p31intersection, p13intersection, nIntersection, r11, ratio);
            result.add(new AreaFeature(area.area.clone(), feature));
        }

        return result;
    }

    public static class AreaPrediction {
        public Util.AreaBox area;
        public char prediction;

        public AreaPrediction(Util.AreaBox area, char prediction) {
            this.area = area;
            this.prediction = prediction;
        }
    }

    public static List<AreaPrediction> getPredictions(Image image) {
        List<AreaPrediction> result = new ArrayList<>();
        for (AreaFeature areaFeature : getFeatures(image)) {
            result.add(new AreaPrediction(areaFeature.area.clone(), '?'));
        }
        return result;
    }

}
