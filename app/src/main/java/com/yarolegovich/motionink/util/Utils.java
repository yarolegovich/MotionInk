package com.yarolegovich.motionink.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yarolegovich on 03.06.2016.
 */
public class Utils {

    private static final Pattern PATTER_DIGITS = Pattern.compile("-?[0-9]+");
    public static final Comparator<File> NUMBER_COMPARATOR = (f1, f2) -> {
        Matcher f1m = PATTER_DIGITS.matcher(f1.getName());
        f1m.find();
        Matcher f2m = PATTER_DIGITS.matcher(f2.getName());
        f2m.find();
        return Integer.parseInt(f1m.group()) - Integer.parseInt(f2m.group());
    };

    public static final Comparator<File> NUMBER_REVERSE = (f1, f2) ->
            -NUMBER_COMPARATOR.compare(f1, f2);

    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static File createDirIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static Intent createGifShareIntent(Uri gifUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/gif");
        shareIntent.putExtra(Intent.EXTRA_STREAM, gifUri);
        return shareIntent;
    }

}
