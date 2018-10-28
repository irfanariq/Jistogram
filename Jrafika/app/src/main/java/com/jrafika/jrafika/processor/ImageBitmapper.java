package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Util;

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
