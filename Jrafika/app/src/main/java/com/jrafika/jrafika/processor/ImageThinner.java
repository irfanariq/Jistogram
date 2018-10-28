package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Skeleton;

public class ImageThinner implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        return Skeleton.thinning(image);
    }

}
