package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.Histogram;
import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.processor.ImageProcessor;

public class ImageSpecification implements ImageProcessor {

    private Integer[] targetHistogram;

    public ImageSpecification(Integer[] targetHistogram) {
        this.targetHistogram = targetHistogram;
    }

    @Override
    public Image proceed(Image image) {
        return Histogram.specifyHistogram(image, targetHistogram);
    }

}
