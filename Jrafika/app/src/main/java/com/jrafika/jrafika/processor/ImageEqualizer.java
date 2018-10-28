package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.Histogram;
import com.jrafika.jrafika.core.Image;

public class ImageEqualizer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        return Histogram.equalizeHistogram(image);
    }

}
