package com.yarolegovich.motionink;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

import com.yarolegovich.motionink.camera.ImageTaker;
import com.yarolegovich.motionink.camera.ImageTakerFactory;
import com.yarolegovich.motionink.draw.persist.SlideImageSerializer;
import com.yarolegovich.motionink.view.OverlayView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
@SuppressWarnings({"ConstantCondintions", "ConstantConditions"})
public class CameraActivity extends AppCompatActivity {

    private ImageTaker imageTaker;
    private Handler handler;

    private FloatingActionButton fab;

    private boolean isTakingPhotos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        setResult(RESULT_CANCELED);

        handler = new Handler();

        SlideImageSerializer slideImageSerializer = new SlideImageSerializer(this);
        slideImageSerializer.prepareDir();

        TextureView previewSurface = (TextureView) findViewById(R.id.camera_preview);
        imageTaker = ImageTakerFactory.createImageTaker(previewSurface);
        imageTaker.setCallback(slideImageSerializer::saveImage);

        OverlayView overlayView = (OverlayView) findViewById(R.id.overlay);
        overlayView.overlay(previewSurface);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            if (!isTakingPhotos) {
                isTakingPhotos = true;
                startTakingPhotos();
            } else {
                stopTakingPhotos();
                finish();
            }
        });
    }

    public void startTakingPhotos() {
        fab.setImageResource(R.drawable.ic_stop_white_24dp);
        handler.post(new Runnable() {
            @Override
            public void run() {
                imageTaker.capture();
                setResult(RESULT_OK);
                handler.postDelayed(this, 500);
            }
        });
    }

    private void stopTakingPhotos() {
        fab.setImageResource(R.drawable.ic_camera_alt_white_24dp);
        handler.removeCallbacks(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTakingPhotos();
    }
}
