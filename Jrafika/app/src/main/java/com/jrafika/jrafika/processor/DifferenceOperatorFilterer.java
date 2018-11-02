package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.FilterUtil;
import com.jrafika.jrafika.core.Image;

public class DifferenceOperatorFilterer implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        Image result = FilterUtil.differenceFilter(image);
        return result;
    }

}
