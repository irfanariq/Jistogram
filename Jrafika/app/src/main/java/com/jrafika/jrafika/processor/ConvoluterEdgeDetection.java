package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.FilterUtil;
import com.jrafika.jrafika.core.Image;

public class ConvoluterEdgeDetection implements ImageProcessor {

    private float[][][] kernel;

    public ConvoluterEdgeDetection(float[][][] kernel) {
        this.kernel = kernel;
    }

    @Override
    public Image proceed(Image image) {
        return FilterUtil.convoluteMag(image, kernel);
    }
}
