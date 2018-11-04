package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.FilterUtil;
import com.jrafika.jrafika.core.Image;

public class Convoluter implements ImageProcessor {

    private float[][] kernel;

    public Convoluter(float[][] kernel) {
        this.kernel = kernel;
    }

    @Override
    public Image proceed(Image image) {
        return FilterUtil.convolute(image, kernel);
    }
}
