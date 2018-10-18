package com.jrafika.jrafika.core;

public class ImageBitmapper implements ImageProcessor {

    private int threshold;

    public ImageBitmapper(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Image proceed(Image image) {
        return Util.imageGrayscaleToBitmap(image, threshold);
    }

}
