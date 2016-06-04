package com.yarolegovich.motionink.gif;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.yarolegovich.motionink.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class GifEncoderService extends IntentService {

    private static final String EXTRA_DELAY = "extra_delay";
    private static final String EXTRA_OUT = "extra_out";

    private static final int NOTIF_ID = 2421;

    public static Intent callingIntent(Context context, Uri out, int delay) {
        Intent intent = new Intent(context, GifEncoderService.class);
        intent.putExtra(EXTRA_OUT, out);
        intent.putExtra(EXTRA_DELAY, delay);
        return intent;
    }

    private NotificationManager nm;
    private int noOfFrames;

    public GifEncoderService() {
        super(GifEncoderService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Uri out = (Uri) intent.getParcelableExtra(EXTRA_OUT);
        int delay = intent.getIntExtra(EXTRA_DELAY, 100);
        File framesDir = new File(getFilesDir() + GifSaver.FRAMES_DIR);
        File[] frames = framesDir.listFiles();
        Arrays.sort(frames, Utils.NUMBER_COMPARATOR);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noOfFrames = frames.length;

        try {
            AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
            gifEncoder.setRepeat(0);
            gifEncoder.setDelay(delay);

            OutputStream os = getContentResolver().openOutputStream(out);
            gifEncoder.start(os);

            for (int i = 0; i < frames.length; i++) {
                File frame = frames[i];
                notifyProgress(i);
                Bitmap frameBitmap = BitmapFactory.decodeFile(frame.getAbsolutePath());
                gifEncoder.addFrame(frameBitmap);
            }

            gifEncoder.finish();

            notifyDone();
        } catch (FileNotFoundException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    private void notifyProgress(int frameNo) {
        nm.notify(NOTIF_ID, new Notification.Builder(getApplicationContext())
                .setContentTitle("Creating GIF")
                .setContentText(String.format("Processed %d out of %d frames", frameNo, noOfFrames))
                .build());
    }

    private void notifyDone() {

    }
}
