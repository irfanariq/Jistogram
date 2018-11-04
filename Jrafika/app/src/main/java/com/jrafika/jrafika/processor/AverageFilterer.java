package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.FilterUtil;
import com.jrafika.jrafika.core.Image;

public class AverageFilterer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        Image result = FilterUtil.averageFilter(image);
        return result;
    }

}
