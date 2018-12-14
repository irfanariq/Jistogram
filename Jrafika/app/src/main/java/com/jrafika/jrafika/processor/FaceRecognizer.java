package com.jrafika.jrafika.processor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;

import com.jrafika.jrafika.core.ColorUtil;
import com.jrafika.jrafika.core.Image;
import com.jrafika.jrafika.core.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FaceRecognizer implements ImageProcessor{

    @Override
    public Image proceed(Image image) {
        Bitmap bm = image.toBitmap();

        int width = bm.getWidth();
        int height = bm.getHeight();
        float widthTarget = (float) 512;
        float scaledSize = widthTarget / width;

        float newWidth = (width * scaledSize);
        float newHeight =  (height * scaledSize);
        Log.d("img size", newWidth + " * " + newHeight);
        Log.d("img size", (int) newWidth + " * " + (int) newHeight);

        Bitmap scaledBm = Bitmap.createScaledBitmap(bm, (int) newWidth, (int) newHeight, true);

        Canvas canvas = new Canvas(scaledBm);
        try {
            URL url = new URL("http://192.168.43.83:8000/");
            HttpURLConnection client = null;
            client = (HttpURLConnection) url.openConnection();

            client.setDoInput(true);
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setConnectTimeout(10000);
            client.setRequestProperty("Content-Type", "image/jpeg");

            OutputStream outputStream = client.getOutputStream();
            image.toBitmap().compress(Bitmap.CompressFormat.JPEG,100, outputStream);
            outputStream.close();

            client.connect();
            Log.d("Tes Response", String.valueOf(client.getResponseCode() + " " + client.getResponseMessage()));

            InputStream responseStream = new BufferedInputStream(client.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String responseString = stringBuilder.toString();
            Log.d("Tes Response",responseString);

            JSONArray response = new JSONArray(responseString);
            for(int i = 0; i < response.length(); i++) {
                JSONObject face = response.getJSONObject(i);
                Log.d("response", face.toString());

                Pair<Integer, Integer> upperBound = new Pair<Integer, Integer>(face.getJSONArray("upperbound").getInt(0),
                        face.getJSONArray("upperbound").getInt(1));
                Pair<Integer, Integer> lowerBound = new Pair<Integer, Integer>(face.getJSONArray("lowerbound").getInt(0),
                        face.getJSONArray("lowerbound").getInt(1));

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(1);
                canvas.drawRect(upperBound.first - 1, upperBound.second - 1, lowerBound.first, lowerBound.second, paint);

                paint.setColor(Color.RED);
                paint.setStrokeWidth(2);
                JSONArray landmarks = face.getJSONArray("landmark");
                for(int j = 0; j < landmarks.length(); j++) {
                    JSONArray point = landmarks.getJSONArray(j);
                    int x = point.getInt(0);
                    int y = point.getInt(1);

                    canvas.drawPoint(x, y, paint);
                }

                String namePrediction = face.getString("name");
                paint.setStrokeWidth(1);
                canvas.drawText(
                        namePrediction + "",
                        upperBound.first + 4,
                        upperBound.second + 12,
                        paint
                );
            }

            client.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Image.fromBitmap(scaledBm);
    }
}
