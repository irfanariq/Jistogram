package com.jrafika.jrafika.processor;

import android.util.Log;

import com.jrafika.jrafika.core.FilterUtil;
import com.jrafika.jrafika.core.Image;

public class MedianFilterer implements ImageProcessor {

    private int kernelSize;

    public MedianFilterer(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    @Override
    public Image proceed(Image image) {
        Image result = FilterUtil.medianFilter(image, kernelSize);
        return result;
    }

}
