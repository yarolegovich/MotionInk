package com.yarolegovich.motionink.gif;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.yarolegovich.motionink.R;
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

    private Uri gifUri;
    private int noOfFrames;

    public GifEncoderService() {
        super(GifEncoderService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        gifUri = intent.getParcelableExtra(EXTRA_OUT);
        int delay = intent.getIntExtra(EXTRA_DELAY, 100);
        File framesDir = new File(getFilesDir() + GifSaver.FRAMES_DIR);
        File[] frames = framesDir.listFiles();
        Arrays.sort(frames, Utils.NUMBER_REVERSE);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noOfFrames = frames.length;

        try {
            AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
            gifEncoder.setRepeat(0);
            gifEncoder.setDelay(delay);

            OutputStream os = getContentResolver().openOutputStream(gifUri);
            gifEncoder.start(os);

            for (int i = 0; i < frames.length; i++) {
                File frame = frames[i];
                notifyProgress(i);
                Bitmap frameBitmap = BitmapFactory.decodeFile(frame.getAbsolutePath());
                gifEncoder.addFrame(frameBitmap);
            }

            gifEncoder.finish();

            for (int i = 0; i < frames.length; i++) {
                frames[i].delete();
            }

            notifyDone();
        } catch (FileNotFoundException e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            notifyFail(e.getMessage());
        }
    }

    private void notifyProgress(int frameNo) {
        nm.notify(NOTIF_ID, new Notification.Builder(getApplicationContext())
                .setContentTitle("Creating GIF...")
                .setProgress(noOfFrames, frameNo, false)
                .setSmallIcon(R.drawable.ic_stat_av_loop)
                .build());
    }

    private void notifyDone() {
        Intent shareIntent = Utils.createGifShareIntent(gifUri);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 9392, shareIntent, 0);
        nm.notify(NOTIF_ID, new Notification.Builder(getApplicationContext())
                .setContentText("Your GIF is ready, share it with you friends")
                .setContentTitle("Tap to share!")
                .setContentIntent(pi)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_stat_action_thumb_up)
                .build());
    }

    private void notifyFail(String message) {
        nm.notify(NOTIF_ID, new Notification.Builder(getApplicationContext())
                .setContentTitle("Failed to create a GIF")
                .setContentText(message)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_stat_alert_error)
                .build());
    }
}
