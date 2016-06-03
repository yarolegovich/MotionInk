package com.yarolegovich.motionink.camera;

import android.content.res.Configuration;
import android.graphics.SurfaceTexture;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

/**
 * Created by yarolegovich on 04.06.2016.
 */
class ImageTakerEasyCamera extends ImageTaker {

    private EasyCamera easyCamera;
    private EasyCamera.CameraActions actions;
    EasyCamera.PictureCallback pictureCallback = (data, camActions) -> {
        imageProcessingHandler.post(() -> callback.onImageTaken(data));
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        try {
            easyCamera = DefaultEasyCamera.open();
            easyCamera.setDisplayOrientation(getRotation());
            actions = easyCamera.startPreview(surface);
        } catch (Exception e) {
            if (easyCamera != null) {
                easyCamera.close();
            }
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (easyCamera != null) {
            easyCamera.close();
        }
        return super.onSurfaceTextureDestroyed(surface);
    }

    @Override
    public void capture() {
        actions.takePicture(EasyCamera.Callbacks.create()
                .withRestartPreviewAfterCallbacks(true)
                .withJpegCallback(pictureCallback));
    }

    private int getRotation() {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT ?
                90 : 0;
    }
}
