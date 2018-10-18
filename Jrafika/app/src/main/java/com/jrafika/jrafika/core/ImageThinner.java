package com.jrafika.jrafika.core;

public class ImageThinner implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        return Skeleton.thinning(image);
    }

}
