package com.jrafika.jrafika.processor;

import com.jrafika.jrafika.core.Image;

import static com.jrafika.jrafika.core.FaceUtil.skinify;
import static com.jrafika.jrafika.core.Util.dilate;

public class SkinDetector implements ImageProcessor {

    @Override
    public Image proceed(Image image) {
        return dilate(skinify(image), 5);
    }

}
