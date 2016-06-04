package com.yarolegovich.motionink.camera;

import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaActionSound;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

/**
 * Created by yarolegovich on 04.06.2016.
 */
class ImageTakerEasyCamera extends ImageTaker {

    private EasyCamera easyCamera;
    private EasyCamera.CameraActions actions;
    EasyCamera.PictureCallback pictureCallback = (data, camActions) -> {
        imageProcessingHandler.post(() -> {
            if (callback != null) {
                callback.onImageTaken(data);
            }
        });
    };

    private MediaActionSound sound;

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        try {
            easyCamera = DefaultEasyCamera.open();
            easyCamera.setDisplayOrientation(getRotation());
            actions = easyCamera.startPreview(surface);

            sound = new MediaActionSound();
        } catch (Exception e) {
            if (easyCamera != null) {
                easyCamera.close();
            }
            if (sound != null) {
                sound.release();
            }
        }
    }

    @Override
    public synchronized boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        boolean flag = super.onSurfaceTextureDestroyed(surface);
        if (easyCamera != null) {
            easyCamera.close();
            easyCamera = null;
        }
        if (sound != null) {
            sound.release();
        }
        return flag;
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
