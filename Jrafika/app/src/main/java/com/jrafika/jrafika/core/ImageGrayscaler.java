package com.jrafika.jrafika.core;

public class ImageGrayscaler implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        return Util.imageRGBToGrayscale(image);
    }

}
