package com.yarolegovich.motionink;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

import com.yarolegovich.motionink.camera.ImageTaker;
import com.yarolegovich.motionink.camera.ImageTakerFactory;
import com.yarolegovich.motionink.view.OverlayView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class CameraActivity extends AppCompatActivity {

    private ImageTaker imageTaker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        TextureView previewSurface = (TextureView) findViewById(R.id.camera_preview);
        imageTaker = ImageTakerFactory.createImageTaker(previewSurface);

        OverlayView overlayView = (OverlayView) findViewById(R.id.overlay);
        overlayView.overlay(previewSurface);
    }
}
