package com.yarolegovich.motionink.camera;

import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.util.Log;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

/**
 * Created by yarolegovich on 04.06.2016.
 */
@SuppressWarnings("deprecation")
class ImageTakerEasyCamera extends ImageTaker {

    private int currentCamera;

    private EasyCamera easyCamera;
    private EasyCamera.CameraActions actions;
    EasyCamera.PictureCallback pictureCallback = (data, camActions) -> {
        imageProcessingHandler.post(() -> {
            if (callback != null) {
                callback.onImageTaken(data, currentCamera);
            }
        });
    };

    private MediaActionSound sound;


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        sound = new MediaActionSound();
        openCamera(0);
    }

    @Override
    public synchronized boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        boolean flag = super.onSurfaceTextureDestroyed(surface);
        closeCamera();
        if (sound != null) {
            sound.release();
        }
        return flag;
    }

    private void openCamera(int cameraId) {
        try {
            currentCamera = cameraId;
            easyCamera = DefaultEasyCamera.open(cameraId);
            easyCamera.setDisplayOrientation(getRotation());
            actions = easyCamera.startPreview(textureView.getSurfaceTexture());
        } catch (Exception e) {
            if (easyCamera != null) {
                easyCamera.close();
            }
        }
    }

    private void closeCamera() {
        if (easyCamera != null) {
            easyCamera.close();
            easyCamera = null;
        }
    }

    public void changeCamera() {
        closeCamera();
        int newCamera = currentCamera == 0 ? 1 : 0;
        openCamera(newCamera);
    }

    @Override
    public synchronized void capture() {
        if (easyCamera != null) {
            sound.play(MediaActionSound.SHUTTER_CLICK);
            actions.takePicture(EasyCamera.Callbacks.create()
                    .withRestartPreviewAfterCallbacks(true)
                    .withJpegCallback(pictureCallback));
        }
    }

    private int getRotation() {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT ?
                90 : 0;
    }
}
