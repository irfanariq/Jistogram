package com.jrafika.jrafika.core;

import android.util.Pair;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class BoxUtil {

    public static class BoundingBox {
        public Pair<Integer, Integer> upperBound;
        public Pair<Integer, Integer> lowerBound;

        public BoundingBox(Pair<Integer, Integer> upperBound, Pair<Integer, Integer> lowerBound) {
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
        }

        @Override
        protected BoundingBox clone() {
            return new BoundingBox(upperBound, lowerBound);
        }

        public int getArea() {
            return (lowerBound.second - upperBound.second) * (lowerBound.first - upperBound.first);
        }
    }

    public static Pair<Integer, Integer> getBoundDistance(BoundingBox a, BoundingBox b) {
        int distanceX = 0;
        if (a.upperBound.first >= b.lowerBound.first || a.lowerBound.first <= b.upperBound.first) {
            distanceX = min(
                    abs(a.upperBound.first - b.lowerBound.first + 1),
                    abs(a.lowerBound.first - b.upperBound.first + 1)
            );
        }

        int distanceY = 0;
        if (a.upperBound.second >= b.lowerBound.second || a.lowerBound.second <= b.upperBound.second) {
            distanceY = min(
                    abs(a.upperBound.second - b.lowerBound.second + 1),
                    abs(a.lowerBound.second - b.upperBound.second + 1)
            );
        }

        return new Pair<>(distanceX, distanceY);
    }

    public static BoundingBox mergeBound(BoundingBox a, BoundingBox b) {
        return new BoundingBox(
                new Pair(
                        min(a.upperBound.first, b.upperBound.first),
                        min(a.upperBound.second, b.upperBound.second)
                ),
                new Pair(
                        max(a.lowerBound.first, b.lowerBound.first),
                        max(a.lowerBound.second, b.lowerBound.second)
                )
        );
    }

}
