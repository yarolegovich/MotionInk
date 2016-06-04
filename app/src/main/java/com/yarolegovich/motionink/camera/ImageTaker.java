package com.yarolegovich.motionink.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public abstract class ImageTaker implements TextureView.SurfaceTextureListener {

    private HandlerThread handlerThread;

    protected Activity context;
    protected TextureView textureView;

    protected Callback callback;
    protected Handler imageProcessingHandler;

    void init(TextureView surfaceView) {
        this.context = (Activity) surfaceView.getContext();
        this.textureView = surfaceView;

        surfaceView.setSurfaceTextureListener(this);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        handlerThread = new HandlerThread("ImageProcessorThread");
        handlerThread.start();
        imageProcessingHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (handlerThread != null) {
            handlerThread.quit();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    public abstract void capture();

    public interface Callback {
        void onImageTaken(byte[] imageData);
    }
}
