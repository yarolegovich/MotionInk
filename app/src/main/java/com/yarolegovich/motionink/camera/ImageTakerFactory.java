package com.yarolegovich.motionink.camera;

import android.view.TextureView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class ImageTakerFactory {
    public static ImageTaker createImageTaker(TextureView previewSurface) {
        ImageTaker imageTaker = new ImageTakerEasyCamera();
        imageTaker.init(previewSurface);
        return imageTaker;
    }
}
