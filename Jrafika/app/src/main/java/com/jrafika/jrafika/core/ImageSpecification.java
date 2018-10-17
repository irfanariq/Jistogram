package com.jrafika.jrafika.core;

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
