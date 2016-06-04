package com.yarolegovich.motionink.draw.persist;

import android.content.Context;

import java.io.File;

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
        return getDir().list().length;
    }

    public void prepareDir() {
        File dir = getDir();
        File[] children = dir.listFiles();
        for (int i = 0; i < children.length; i++) {
            children[i].delete();
        }
    }

    public void remove(int slidePosition) {
        File dir = getDir();
        File[] children = dir.listFiles();
        boolean removed = false;
        for (File file : children) {
            if (file.getName().equals(getFileName(slidePosition))) {
                removed = true;
                file.delete();
                break;
            }
        }
        if (removed) {
            for (int i = slidePosition + 1; i < children.length; i++) {
                File newName = new File(getDir(), getFileName(i - 1));
                new File(getDir(), getFileName(slidePosition)).renameTo(newName);
            }
        }
    }
}
