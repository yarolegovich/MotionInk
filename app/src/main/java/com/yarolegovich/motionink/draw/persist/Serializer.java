package com.yarolegovich.motionink.draw.persist;

import android.content.Context;
import android.util.Log;

import com.yarolegovich.motionink.util.Utils;

import java.io.File;
import java.util.Arrays;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public abstract class Serializer<T, U> {

    protected Context context;

    protected Serializer(Context context) {
        this.context = context;
        checkDirExists();
    }

    public abstract void save(int slidePosition, T data);

    public abstract U load(int slidePosition);

    protected abstract File getDir();
    protected abstract String getFileName(int slidePosition);

    private void checkDirExists() {
        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public int noOfSlides() {
        return getFiles().length;
    }

    public void prepareDir() {
        File dir = getDir();
        File[] children = dir.listFiles();
        for (int i = 0; i < children.length; i++) {
            children[i].delete();
        }
    }

    public void remove(int slidePosition) {
        File[] children = getFiles();
        boolean removed = false;
        Arrays.sort(children, Utils.NUMBER_COMPARATOR);
        for (File file : children) {
            if (file.getName().equals(getFileName(slidePosition))) {
                removed = file.delete();
                Log.d("tag", String.format("removed slide #%d: %b", slidePosition, removed));
                break;
            }
        }
        if (removed) {
            for (int i = slidePosition + 1; i < children.length; i++) {
                File newName = new File(getDir(), getFileName(i - 1));
                Log.d("tag", getFileName(i) + " renamed to -> " + newName.getName());
                new File(getDir(), getFileName(i)).renameTo(newName);
            }
        }
    }

    protected File[] getFiles() {
        return getDir().listFiles();
    }
}
