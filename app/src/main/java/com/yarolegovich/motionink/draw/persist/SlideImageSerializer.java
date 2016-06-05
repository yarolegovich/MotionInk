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
public class SlideImageSerializer extends Serializer<byte[], Bitmap> {

    private int savedCounter = 0;
    private int rotation;

    public SlideImageSerializer(Context context) {
        super(context);
    }


    public void saveImage(byte[] data, int cameraId) {
        rotation = cameraId == 0 ? 90 : -90;
        save(savedCounter, data);
    }

    @Override
    public void save(int slidePosition, byte[] data) {
        File file = new File(getDir(), getFileName(slidePosition));

        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix rotationMat = new Matrix();
        rotationMat.setRotate(rotation);

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

    @Override
    public Bitmap load(int slidePosition) {
        File file = new File(getDir(), getFileName(slidePosition));
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            return null;
        }
    }

    @Override
    protected File getDir() {
        return new File(context.getFilesDir() + "/rasters");
    }

    @Override
    protected String getFileName(int slidePosition) {
        return "slide-img" + slidePosition + ".jpg";
    }
}
