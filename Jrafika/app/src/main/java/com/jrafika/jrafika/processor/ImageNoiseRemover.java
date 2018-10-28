package com.jrafika.jrafika.processor;

import android.util.Pair;

import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ImageNoiseRemover implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        image = image.clone();
        List<Util.AreaBox> areas = Util.imageFloodFill(image,-1 );
        for (Util.AreaBox area : areas) {
            if (area.n < 20) {
                Queue<Pair<Integer, Integer>> que = new LinkedList<>();
                que.add(area.seedPoint);
                while (!que.isEmpty()) {
                    Pair<Integer, Integer> current = que.remove();
                    int cx = current.first;
                    int cy = current.second;
                    if (image.getPixel(cx, cy) > 0) {
                        image.setPixel(cx, cy, 0);

                        for (int di = 0; di < 8; di++) {
                            int nx = cx + Util.DIRECTION_X[di];
                            int ny = cy + Util.DIRECTION_Y[di];
                            if (nx >= area.upperBound.first && nx < area.lowerBound.first &&
                                    ny >= area.upperBound.second && ny < area.lowerBound.second &&
                                    image.getPixel(nx, ny) > 0) {
                                que.add(new Pair(nx, ny));
                            }
                        }
                    }
                }
            }
        }
        return image;
    }

}
