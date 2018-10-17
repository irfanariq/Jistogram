package com.jrafika.jrafika.core;

public class ImageEqualizer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        return Histogram.equalizeHistogram(image);
    }

}
