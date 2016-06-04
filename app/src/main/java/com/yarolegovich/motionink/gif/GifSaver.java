package com.yarolegovich.motionink.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.yarolegovich.motionink.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class GifSaver {

    public static final String FRAMES_DIR = "/frames";

    public static final String NAME_PATTERN = "frame-%d.png";

    private HandlerThread ioThread;

    private Handler ioThreadHandler;
    private Handler mainThreadHandler;

    private Context context;

    private FrameProvider frameProvider;

    private int frameCounter;

    public GifSaver(Context context) {
        this.context = context;

        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public void prepareFilesToCreateGif(FrameProvider frameProvider) {
        this.frameProvider = frameProvider;

        ioThread = new HandlerThread("IoThread");
        ioThread.start();

        ioThreadHandler = new Handler(ioThread.getLooper());

        try {
            mainThreadHandler.post(() -> frameProvider.requestFrame(frameCounter));
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    public void addFrame(Bitmap bitmap) {
        if (bitmap != null) {
            ioThreadHandler.post(() -> {
                try {
                    OutputStream os = new FileOutputStream(frameName());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    frameCounter++;
                    mainThreadHandler.post(() -> frameProvider.requestFrame(frameCounter));
                } catch (FileNotFoundException e) {
                    Log.e(getClass().getSimpleName(), e.getMessage(), e);
                }
            });
        } else {
            ioThread.quit();
            ioThread = null;
        }
    }

    public void stop() {
        if (ioThread != null) {
            ioThread.quit();
        }
    }

    private File frameName() {
        return new File(
                Utils.createDirIfNotExists(context.getFilesDir() + FRAMES_DIR),
                String.format(NAME_PATTERN, frameCounter));
    }

    public interface FrameProvider {
        void requestFrame(int frameNumber);
    }
}
