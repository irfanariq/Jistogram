package com.jrafika.jrafika.core;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Skeleton {

    public static int[] getNeighbors(Image image, int x, int y) {
        int[] neighbors = new int[8];
        int width = image.getWidth();
        int height = image.getHeight();

        for (int di = 0; di < 8; di++) {
            int nx = x + Util.DIRECTION_X[di];
            int ny = y + Util.DIRECTION_Y[di];
            if (nx < 0 || ny < 0 || nx >= width || ny >= height) {
                neighbors[di] = 0;
            } else {
                neighbors[di] = image.getPixel(nx, ny) > 0 ? 1 : 0;
            }
        }

        return neighbors;
    }

    public static int getNC(int[] neighbors) {
        int c = 0;
        for (int x : neighbors) {
            if (x > 0) {
                c++;
            }
        }
        return c;
    }

    public static int getNS(int[] neighbors) {
        int ns = 0;
        for (int i = 1; i <= 8; i++) {
            if (neighbors[i % 8] == 0 && neighbors[i - 1] > 0) {
                ns++;
            }
        }
        return ns;
    }

    public static Image thinning(Image image) {
        image = image.clone();
        image = new Image(image.getWidth(), image.getHeight(), Image.IMAGE_THINNED, image.getPixels());
        int width = image.getWidth();
        int height = image.getHeight();

        List<Pair<Integer, Integer>> toDelete = new LinkedList<>();
        int deleted;
        do {
            deleted = 0;

            toDelete.clear();
            for (int cx = 0; cx < width; cx++) {
                for (int cy = 0; cy < height; cy++) {
                    if (image.getPixel(cx, cy) > 0) {
                        int[] ng = getNeighbors(image, cx, cy);
                        int nc = getNC(ng);
                        int ns = getNS(ng);
                        if (2 <= nc && nc <= 6 && ns == 1 && (ng[2] == 0 || ng[4] == 0 || (ng[0] == 0 && ng[6] == 0))) {
                            toDelete.add(new Pair(cx, cy));
                            deleted++;
                        }
                    }
                }
            }
            for (Pair<Integer, Integer> pDel : toDelete) {
                image.setPixel(pDel.first, pDel.second, 0);
            }

            toDelete.clear();
            for (int cx = 0; cx < width; cx++) {
                for (int cy = 0; cy < height; cy++) {
                    if (image.getPixel(cx, cy) > 0) {
                        int[] ng = getNeighbors(image, cx, cy);
                        int nc = getNC(ng);
                        int ns = getNS(ng);
                        if (2 <= nc && nc <= 6 && ns == 1 && (ng[0] == 0 || ng[6] == 0 || (ng[2] == 0 && ng[4] == 0))) {
                            toDelete.add(new Pair(cx, cy));
                            deleted++;
                        }
                    }
                }
            }
            for (Pair<Integer, Integer> pDel : toDelete) {
                image.setPixel(pDel.first, pDel.second, 0);
            }
        } while (deleted > 0);

        return image;
    }

    public static boolean isCorner(Image image, int x, int y) {
        if (image.getPixel(x, y) > 0) {
            int[] ng = getNeighbors(image, x, y);
            int ns = getNS(ng);
            return ns == 1;
        }
        return false;
    }

    public static boolean isIntersection(Image image, int x, int y) {
        if (image.getPixel(x, y) > 0) {
            int[] ng = getNeighbors(image, x, y);
            int nc = getNC(ng);
            int ns = getNS(ng);
            return nc >= 3 && ns >= 3;
        }
        return false;
    }

    public static class CriticalPoint {
        public enum Type { CORNER, INTERSECTION }
        public Pair<Integer, Integer> pos;
        public Type type;

        public CriticalPoint(Pair<Integer, Integer> pos, Type type) {
            this.pos = pos;
            this.type = type;
        }
    }

    public static class AreaCriticalPoint {
        public Util.AreaBox area;
        public List<CriticalPoint> criticalPoints;

        public AreaCriticalPoint(Util.AreaBox area, List<CriticalPoint> criticalPoints) {
            this.area = area;
            this.criticalPoints = criticalPoints;
        }
    }

    public static List<AreaCriticalPoint> getCriticalPoints(Image image) {
        image = image.clone();
        int width = image.getWidth();
        int height = image.getHeight();

        List<AreaCriticalPoint> result = new ArrayList<>();

        List<Util.AreaBox> areas = Util.imageFloodFill(image);
        for (Util.AreaBox area : areas) {
            List<CriticalPoint> criticalPoints = new ArrayList<>();
            for (int x = area.upperBound.first; x < area.lowerBound.first; x++) {
                for (int y = area.upperBound.second; y < area.lowerBound.second; y++) {
                    if (isCorner(image, x, y)) {
                        criticalPoints.add(new CriticalPoint(new Pair(x, y), CriticalPoint.Type.CORNER));
                    } else if (isIntersection(image, x, y)) {
                        criticalPoints.add(new CriticalPoint(new Pair(x, y), CriticalPoint.Type.INTERSECTION));
                    }
                }
            }

            Queue<Pair<Integer, Integer>> toBeDeleted = new LinkedList<>();
            Queue<Pair<Integer, Integer>> intersectionReference = new LinkedList<>();
            for (CriticalPoint corner : criticalPoints) {
                if (corner.type != CriticalPoint.Type.CORNER) {
                    continue;
                }
                for (CriticalPoint intersection : criticalPoints) {
                    if (intersection.type != CriticalPoint.Type.INTERSECTION) {
                        continue;
                    }
                    int dx = intersection.pos.first - corner.pos.first;
                    int dy = intersection.pos.second - corner.pos.second;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < 0.05 * area.n) {
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
                        criticalPoints.add(new CriticalPoint(new Pair(x, y), CriticalPoint.Type.CORNER));
                    } else if (isIntersection(image, x, y)) {
                        criticalPoints.add(new CriticalPoint(new Pair(x, y), CriticalPoint.Type.INTERSECTION));
                    }
                }
            }

            result.add(new AreaCriticalPoint(area.clone(), criticalPoints));
        }

        return result;
    }

}
