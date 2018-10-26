package com.jrafika.jrafika.core;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class LoaderUtil {

    public static double[] loadDouble(Context context, int resourceId) throws IOException {
        InputStream raw = context.getResources().openRawResource(resourceId);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = raw.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] byteArray = buffer.toByteArray();

        ByteBuffer arr = ByteBuffer.wrap(byteArray);
        double[] result = new double[arr.remaining() / 8];
        for (int i = 0; i < result.length; i++) {
            long l = arr.getLong();
            result[i] = Double.longBitsToDouble(Long.reverseBytes(l));
        }
        return result;
    }

    public static double[][] loadDoubleMatrix(Context context, int resourceId, int width) throws IOException {
        double[] vec = loadDouble(context, resourceId);
        double[][] result = new double[(vec.length + width - 1) / width][width];
        int k = 0;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = vec[k];
                k++;
            }
        }
        return result;
    }

}
