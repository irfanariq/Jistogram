package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.FilterUtil;
import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Util;

public class LaplacianFilterer implements ImageProcessor {
    @Override
    public Image proceed(Image image) {
        if (image.getType() == Image.IMAGE_RGB) {
            image = Util.imageRGBToGrayscale(image);
        }
        return FilterUtil.laplacianFilter(image);
    }
}
