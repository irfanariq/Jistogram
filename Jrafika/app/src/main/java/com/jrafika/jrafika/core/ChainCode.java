package com.jrafika.jrafika.core;

import java.util.ArrayList;
import java.util.List;

public class ChainCode {

    public List<Integer> chain;
    public Util.AreaBox areaBox;

    public ChainCode(List<Integer> chain, Util.AreaBox areaBox) {
        this.chain = chain;
        this.areaBox = areaBox;
    }

    @Override
    protected ChainCode clone() {
        return new ChainCode(
                new ArrayList(chain),
                areaBox.clone()
        );
    }

    public static List<ChainCode> getImageChainCode(Image image, int areaThreshold) {
        image = image.clone();
        int width = image.getWidth();
        int height = image.getHeight();
        List<Util.AreaBox> floodFill = Util.imageFloodFill(image, areaThreshold);

        List<ChainCode> results = new ArrayList<>();

        boolean[] visited = new boolean[width * height];
        for (int i = 0; i < width * height; i++) {
            visited[i] = false;
        }

        for (Util.AreaBox areaBox : floodFill) {
            int cx = areaBox.seedPoint.first;
            int cy = areaBox.seedPoint.second;
            List<Integer> chain = new ArrayList<>();
            while (!visited[cy * width + cx]) {
                visited[cy * width + cx] = true;
                for (int di = 0; di < 8; di++) {
                    int nx = cx + Util.DIRECTION_X[di];
                    int ny = cy + Util.DIRECTION_Y[di];
                    if (nx >= 0 && nx < width &&
                            ny >= 0 && ny < height &&
                            image.getPixel(nx, ny) > 0 &&
                            !visited[ny * width + nx]) {
                        int top = ny > 0 ? image.getPixel(nx, ny - 1) : 0;
                        int right = nx < width - 1 ? image.getPixel(nx + 1, ny) : 0;
                        int bottom = ny < height - 1 ? image.getPixel(nx, ny + 1) : 0;
                        int left = nx > 0 ? image.getPixel(nx - 1, ny) : 0;

                        if (top == 0 || right == 0 || bottom == 0 || left == 0) {
                            cx = nx;
                            cy = ny;
                            chain.add(di);
                            break;
                        }
                    }
                }
            }
            if (chain.size() > 0) {
                results.add(new ChainCode(chain, areaBox));
            }
        }
        return results;
    }

    public static List<ChainCode> getImageChainCode(Image image) {
        return getImageChainCode(image, -1);
    }

}
