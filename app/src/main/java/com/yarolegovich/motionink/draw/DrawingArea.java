package com.yarolegovich.motionink.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.wacom.ink.path.PathBuilder;
import com.wacom.ink.path.PathUtils;
import com.wacom.ink.path.SpeedPathBuilder;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.rasterization.InkCanvas;
import com.wacom.ink.rasterization.Layer;
import com.wacom.ink.rasterization.StrokePaint;
import com.wacom.ink.rasterization.StrokeRenderer;
import com.wacom.ink.rendering.EGLRenderingContext;
import com.wacom.ink.smooth.MultiChannelSmoothener;
import com.yarolegovich.motionink.R;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yarolegovich on 03.06.2016.
 */
public class DrawingArea {

    private SurfaceView surfaceView;

    private Tool currentTool;

    private OnDrawingStartedListener drawingStartedListener;

    private List<Stroke> backgroundStrokes;
    private List<Stroke> previousSlideStrokes;
    private List<Stroke> currentStrokes;

    private List<Stroke> undoneStrokes;

    private int color;

    //ALL PACKAGE-PRIVATE FIELDS ARE EXTENSIVELY USED BY TOOL IMPLEMENTATIONS

    InkCanvas inkCanvas;

    Layer inkViewLayer;
    Layer inkStrokeLayer;
    Layer inkCurrentFrameLayer;
    Layer inkImageLayer;

    SpeedPathBuilder pathBuilder;

    StrokePaint paint;
    StrokeRenderer strokeRenderer;

    MultiChannelSmoothener smoothener;

    private Bitmap raster;

    public DrawingArea(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;

        currentStrokes = new LinkedList<>();
        undoneStrokes = new LinkedList<>();
        backgroundStrokes = Collections.emptyList();

        pathBuilder = new SpeedPathBuilder();
        pathBuilder.setNormalizationConfig(100f, 4000f);
        pathBuilder.setPropertyConfig(
                PathBuilder.PropertyName.Width, 30f, 40f, Float.NaN, Float.NaN,
                PathBuilder.PropertyFunction.Power, 1.0f, false);

        currentTool = new StandardBrushTool();

        color = ContextCompat.getColor(getContext(), R.color.colorAccent);

        smoothener = new MultiChannelSmoothener(pathBuilder.getStride());

        surfaceView.getHolder().addCallback(new Lifecycle());
        surfaceView.setOnTouchListener(new TouchInputListener());
    }

    private boolean buildPath(MotionEvent event) {
        Integer[] needToHandle = {MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE};
        if (!Arrays.asList(needToHandle).contains(event.getAction())) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            smoothener.reset();
        }

        PathUtils.Phase phase = PathUtils.getPhaseFromMotionEvent(event);
        FloatBuffer part = pathBuilder.addPoint(
                phase, event.getX(), event.getY(),
                event.getEventTime());
        int partSize = pathBuilder.getPathPartSize();

        MultiChannelSmoothener.SmoothingResult smoothingResult;

        if (partSize > 0) {
            smoothingResult = smoothener.smooth(part, partSize, phase == PathUtils.Phase.END);
            pathBuilder.addPathPart(smoothingResult.getSmoothedPoints(), smoothingResult.getSize());
        }

        FloatBuffer preliminaryPath = pathBuilder.createPreliminaryPath();
        smoothingResult = smoothener.smooth(preliminaryPath, pathBuilder.getPreliminaryPathSize(), true);
        pathBuilder.finishPreliminaryPath(smoothingResult.getSmoothedPoints(), smoothingResult.getSize());

        return event.getAction() == MotionEvent.ACTION_UP && pathBuilder.hasFinished();
    }

    void drawStrokes() {
        inkCanvas.setTarget(inkStrokeLayer);
        inkCanvas.clearColor();
        inkCanvas.drawLayer(inkImageLayer, BlendMode.BLENDMODE_OVERWRITE);

        drawStrokes(backgroundStrokes);
        drawStrokes(currentStrokes);

        paint.setColor(color);
        strokeRenderer.setStrokePaint(paint);

        inkCanvas.setTarget(inkCurrentFrameLayer);
        inkCanvas.clearColor(Color.WHITE);
        inkCanvas.drawLayer(inkStrokeLayer, BlendMode.BLENDMODE_NORMAL);
    }

    private void drawStrokes(List<Stroke> strokes) {
        for (Stroke stroke : strokes) {
            paint.setColor(stroke.getColor());
            strokeRenderer.setStrokePaint(paint);
            strokeRenderer.drawPoints(
                    stroke.getPoints(), 0, stroke.getSize(), stroke.getStartValue(),
                    stroke.getEndValue(), true);
            strokeRenderer.blendStroke(inkStrokeLayer, BlendMode.BLENDMODE_NORMAL);
        }
    }

    void renderView() {
        inkCanvas.setTarget(inkViewLayer);
        inkCanvas.drawLayer(inkCurrentFrameLayer, BlendMode.BLENDMODE_OVERWRITE);
        inkCanvas.invalidate();
    }

    public void setCurrentTool(Tool currentTool) {
        if (this.currentTool != null) {
            this.currentTool.release();
        }
        this.currentTool = currentTool;
        if (inkCanvas != null) {
            this.currentTool.init(this);
        }
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        if (paint != null) {
            paint.setColor(color);
            strokeRenderer.setStrokePaint(paint);
            renderView();
        }
    }

    public Bitmap getCurrentImage() {
        Bitmap bitmap = Bitmap.createBitmap(
                surfaceView.getWidth(),
                surfaceView.getHeight(),
                Bitmap.Config.ARGB_8888);
        inkCanvas.readPixels(
                inkCurrentFrameLayer, bitmap, 0, 0, 0, 0,
                surfaceView.getWidth(),
                surfaceView.getHeight());
        return bitmap;
    }

    public void setStrokeList(@NonNull List<Stroke> strokeList) {
        currentStrokes = strokeList;
        if (inkCanvas != null) {
            drawStrokes();
            renderView();
        }
    }

    public void setPreviousSlideStrokes(@NonNull List<Stroke> previousSlideStrokes) {
        this.previousSlideStrokes = previousSlideStrokes;
    }

    public void setBackgroundStrokes(@NonNull List<Stroke> backgroundStrokes) {
        this.backgroundStrokes = backgroundStrokes;
    }

    public List<Stroke> getStrokeList() {
        return currentStrokes;
    }

    //TODO: To implement this use Command pattern, store indices of inserted/removed
    public void undo() {

    }

    public void redo() {

    }

    public void displayRaster(Bitmap bitmap) {
        if (bitmap != null) {
            if (inkCanvas != null) {
                loadImageLayer(bitmap);
                drawStrokes();
                renderView();
            } else {
                raster = bitmap;
            }
        } else {
            if (raster != null) {
                raster.recycle();
                raster = null;
            }
            if (inkCanvas != null) {
                inkCanvas.clearLayer(inkImageLayer, Color.WHITE);
                drawStrokes();
                renderView();
            }
        }
    }

    private void loadImageLayer(Bitmap bitmap) {
        if (inkCanvas != null) {
            inkCanvas.loadBitmap(
                    inkImageLayer, bitmap,
                    GLES20.GL_LINEAR,
                    GLES20.GL_CLAMP_TO_EDGE);
        }
    }

    public int getColor() {
        return paint.getColor();
    }

    public void setDrawingStartedListener(OnDrawingStartedListener drawingStartedListener) {
        this.drawingStartedListener = drawingStartedListener;
    }

    private class Lifecycle implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        @SuppressWarnings("deprecation")
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (inkCanvas != null && !inkCanvas.isDisposed()) {
                releaseResources();
            }

            Log.d("tag", "onSurfaceChanged");

            inkCanvas = InkCanvas.create(holder, new EGLRenderingContext.EGLConfiguration());

            inkViewLayer = inkCanvas.createViewLayer(width, height);
            inkStrokeLayer = inkCanvas.createLayer(width, height);
            inkCurrentFrameLayer = inkCanvas.createLayer(width, height);
            inkImageLayer = inkCanvas.createLayer(width, height);

            inkCanvas.clearLayer(inkCurrentFrameLayer, Color.WHITE);

            paint = new StrokePaint();
            paint.setColor(color);

            if (currentTool != null) {
                currentTool.init(DrawingArea.this);
            }

            strokeRenderer = new StrokeRenderer(
                    inkCanvas, paint, pathBuilder.getStride(),
                    width, height);

            if (raster != null) {
                loadImageLayer(raster);
                drawStrokes();
            } else {
                inkCanvas.clearLayer(inkImageLayer, Color.WHITE);
                drawStrokes();
            }

            renderView();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseResources();
        }
    }

    private class TouchInputListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (drawingStartedListener != null) {
                drawingStartedListener.onDrawingStarted();
            }
            boolean isFinished = buildPath(event);
            if (currentTool != null) {
                currentTool.onMotionEvent(DrawingArea.this, event, inkImageLayer, isFinished);
            }
            return true;
        }
    }

    public interface OnDrawingStartedListener {
        void onDrawingStarted();
    }

    private void releaseResources() {
        if (currentTool != null) {
            currentTool.release();
        }
        strokeRenderer.dispose();
        inkCanvas.dispose();
        inkCanvas = null;
    }

    public void releaseOpenGl() {
        inkCanvas.releaseOpenGlState();
    }

    public Context getContext() {
        return surfaceView.getContext();
    }

    public int getWidth() {
        return surfaceView.getWidth();
    }

    public int getHeight() {
        return surfaceView.getHeight();
    }

}
