package com.yarolegovich.motionink.draw;

import android.graphics.Bitmap;
import android.view.MotionEvent;

import com.wacom.ink.rasterization.Layer;

/**
 * Created by yarolegovich on 03.06.2016.
 */
public interface Tool {

    void onMotionEvent(
            DrawingArea drawingArea,
            MotionEvent event,
            Layer raster,
            boolean isPathFinished);

    void init(DrawingArea drawingArea);

    void release();

}
