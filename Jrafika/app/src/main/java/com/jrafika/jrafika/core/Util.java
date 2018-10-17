package com.jrafika.jrafika.core;

import android.graphics.Color;

public class Util {

    public static Image imageRGBToGrayscale(Image image) {
        if (image.getType() != Image.IMAGE_RGB)
            throw new RuntimeException("image should be RGB typed");

        Image newImage = new Image(
                image.getWidth(),
                image.getHeight(),
                Image.IMAGE_GRAYSCALE,
                new int[image.getWidth() * image.getHeight()]
        );
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int r = image.getPixel(x, y, 0);
                int g = image.getPixel(x, y, 1);
                int b = image.getPixel(x, y, 2);
                int gray = (r + g + b) / 3;
                newImage.setPixel(x, y, gray);
            }
        }
        return newImage ;
    }

}
