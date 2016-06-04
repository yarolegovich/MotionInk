package com.yarolegovich.motionink.draw.persist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class SlideImageSerializer {

    private static final String DIR = "/rasters";

    private static final String BASE_NAME = "slide-img";
    private static final String EXTENSION = ".jpg";

    private Context context;
    private int savedCounter = 0;

    private int width, height;

    public SlideImageSerializer(Context context) {
        this.context = context;
        File directory = new File(context.getFilesDir() + DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveImage(byte[] imageData) {
        File file = new File(context.getFilesDir() + DIR, BASE_NAME + savedCounter + EXTENSION);

        Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        Matrix rotationMat = new Matrix();
        rotationMat.setRotate(90);

        float centerY = ((float) image.getHeight()) / 2f;
        float centerX = ((float) image.getWidth()) / 2f;

        float halfSize = ((float) Math.min(image.getWidth(), image.getHeight())) / 2f;

        Bitmap finalImage;
        if (image.getHeight() > image.getWidth()) {
            finalImage = Bitmap.createBitmap(
                    image, 0, (int) (centerY - halfSize),
                    image.getWidth(), (int) (centerY + halfSize),
                    rotationMat, true);
        } else {
            finalImage = Bitmap.createBitmap(
                    image, (int) (centerX - halfSize), 0,
                    (int) (centerX + halfSize), image.getHeight(),
                    rotationMat, true);
        }

        image.recycle();

        OutputStream imageFileOS = null;
        try {
            imageFileOS = new FileOutputStream(file);
            finalImage.compress(Bitmap.CompressFormat.PNG, 100, imageFileOS);
        } catch (Exception e) {
            file.delete();
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            return;
        } finally {
            if (imageFileOS != null) {
                try {
                    imageFileOS.close();
                } catch (IOException e) {
                    Log.e(getClass().getSimpleName(), e.getMessage(), e);
                }
            }
            finalImage.recycle();
        }
        savedCounter++;
    }

    public Bitmap loadIfExists(int slidePosition) {
        File file = new File(context.getFilesDir() + DIR, BASE_NAME + slidePosition + EXTENSION);
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            return null;
        }
    }

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void prepareDir() {
        File dir = new File(context.getFilesDir() + DIR);
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            new File(dir, children[i]).delete();
        }
    }
}
