package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.FilterUtil;
import com.jrafika.jrafika.core.Image;

public class GradientOperatorFilterer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        Image result = FilterUtil.gradientOperatorFilter(image);
        return result;
    }

}
