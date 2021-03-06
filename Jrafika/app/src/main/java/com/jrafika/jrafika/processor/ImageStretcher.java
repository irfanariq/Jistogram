package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.Histogram;
import com.jrafika.jrafika.core.Image;

public class ImageStretcher implements ImageProcessor {

    private int inputMin;
    private int inputMax;
    private int outputMin;
    private int outputMax;

    public ImageStretcher(int inputMin, int inputMax, int outputMin, int outputMax) {
        this.inputMin = inputMin;
        this.inputMax = inputMax;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
    }

    @Override
    public Image proceed(Image image) {
        return Histogram.stretchHistogram(image, inputMin, inputMax, outputMin, outputMax);
    }
}
