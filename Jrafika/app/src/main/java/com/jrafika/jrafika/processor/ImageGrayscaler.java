package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Util;

public class ImageGrayscaler implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        return Util.imageRGBToGrayscale(image);
    }

}
