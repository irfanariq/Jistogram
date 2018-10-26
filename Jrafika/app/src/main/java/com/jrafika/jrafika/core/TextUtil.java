package com.jrafika.jrafika.core;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import static com.jrafika.jrafika.core.Skeleton.isCorner;
import static com.jrafika.jrafika.core.Skeleton.isIntersection;

public class TextUtil {

    public static List<BoxUtil.BoundingBox> getCharactersBound(Image image) {
        return getCharactersBound(image, -1);
    }

    public static List<BoxUtil.BoundingBox> getCharactersBound(Image image, int threshold) {
        List<Util.AreaBox> areas = Util.imageFloodFill(image, threshold);

        Set<Integer>[] joins = new HashSet[areas.size()];
        for (int i = 0; i < areas.size(); i++) {
            joins[i] = new HashSet<>();
        }

        for (int i = 0; i < areas.size(); i++) {
            for (int j = i; j < areas.size(); j++) {
                Pair<Integer, Integer> dist = BoxUtil.getBoundDistance(areas.get(i), areas.get(j));
                if (dist.first == 0 && dist.second < 32) {
                    joins[i].add(j);
                }
            }
        }

        Set<Integer> inserted = new HashSet<>();
        List<BoxUtil.BoundingBox> results = new ArrayList<>();
        for (int i = 0; i < areas.size(); i++) {
            if (!inserted.contains(i)) {
                Stack<Integer> st = new Stack<>();
                for (int j : joins[i]) {
                    st.add(j);
                }

                List<BoxUtil.BoundingBox> group = new ArrayList<>();
                while (!st.empty()) {
                    int t = st.pop();
                    if (!inserted.contains(t)) {
                        group.add(areas.get(t));
                        inserted.add(t);
                        for (int j : joins[t]) {
                            st.add(j);
                        }
                    }
                }

                BoxUtil.BoundingBox bound = group.get(0);
                for (BoxUtil.BoundingBox b : group) {
                    bound = BoxUtil.mergeBound(bound, b);
                }
                results.add(bound);
            }
        }

        return results;
    }

    public static class AreaCriticalPoint {
        public BoxUtil.BoundingBox area;
        public List<Skeleton.CriticalPoint> criticalPoints;

        public AreaCriticalPoint(BoxUtil.BoundingBox area, List<Skeleton.CriticalPoint> criticalPoints) {
            this.area = area;
            this.criticalPoints = criticalPoints;
        }
    }

    public static List<AreaCriticalPoint> getCriticalPoints(Image image) {
        image = image.clone();
        int width = image.getWidth();
        int height = image.getHeight();

        List<AreaCriticalPoint> result = new ArrayList<>();

        List<BoxUtil.BoundingBox> areas = getCharactersBound(image);
        for (BoxUtil.BoundingBox area : areas) {
            List<Skeleton.CriticalPoint> criticalPoints = new ArrayList<>();
            int areaN = 0;
            for (int x = area.upperBound.first; x < area.lowerBound.first; x++) {
                for (int y = area.upperBound.second; y < area.lowerBound.second; y++) {
                    if (image.getPixel(x, y) > 0) {
                        areaN++;
                    }
                    if (isCorner(image, x, y)) {
                        criticalPoints.add(new Skeleton.CriticalPoint(new Pair(x, y), Skeleton.CriticalPoint.Type.CORNER));
                    } else if (isIntersection(image, x, y)) {
                        criticalPoints.add(new Skeleton.CriticalPoint(new Pair(x, y), Skeleton.CriticalPoint.Type.INTERSECTION));
                    }
                }
            }

            Queue<Pair<Integer, Integer>> toBeDeleted = new LinkedList<>();
            Queue<Pair<Integer, Integer>> intersectionReference = new LinkedList<>();
            for (Skeleton.CriticalPoint corner : criticalPoints) {
                if (corner.type != Skeleton.CriticalPoint.Type.CORNER) {
                    continue;
                }
                for (Skeleton.CriticalPoint intersection : criticalPoints) {
                    if (intersection.type != Skeleton.CriticalPoint.Type.INTERSECTION) {
                        continue;
                    }
                    int dx = intersection.pos.first - corner.pos.first;
                    int dy = intersection.pos.second - corner.pos.second;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < 0.02 * areaN) {
                        toBeDeleted.add(corner.pos);
                        intersectionReference.add(intersection.pos);
                        break;
                    }
                }
            }

            while (!toBeDeleted.isEmpty()) {
                Pair<Integer, Integer> current = toBeDeleted.remove();
                Pair<Integer, Integer> currentIntersection = intersectionReference.remove();
                if (isIntersection(image, currentIntersection.first, currentIntersection.second)) {
                    int cx = current.first;
                    int cy = current.second;

                    image.setPixel(cx, cy, 0);
                    for (int di = 0; di < 8; di++) {
                        int nx = cx + Util.DIRECTION_X[di];
                        int ny = cy + Util.DIRECTION_Y[di];
                        if (nx >= 0 && ny >= 0 && nx < width && ny < height && isCorner(image, nx, ny)) {
                            toBeDeleted.add(new Pair(nx, ny));
                            intersectionReference.add(currentIntersection);
                        }
                    }
                }
            }

            criticalPoints = new ArrayList<>();
            for (int x = area.upperBound.first; x < area.lowerBound.first; x++) {
                for (int y = area.upperBound.second; y < area.lowerBound.second; y++) {
                    if (isCorner(image, x, y)) {
                        criticalPoints.add(new Skeleton.CriticalPoint(new Pair(x, y), Skeleton.CriticalPoint.Type.CORNER));
                    } else if (isIntersection(image, x, y)) {
                        criticalPoints.add(new Skeleton.CriticalPoint(new Pair(x, y), Skeleton.CriticalPoint.Type.INTERSECTION));
                    }
                }
            }

            result.add(new AreaCriticalPoint(area.clone(), criticalPoints));
        }

        return result;
    }

}
