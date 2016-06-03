package com.yarolegovich.motionink.draw;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;

import com.wacom.ink.path.PathBuilder;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.rasterization.InkCanvas;
import com.wacom.ink.rasterization.Layer;
import com.wacom.ink.rasterization.SolidColorBrush;
import com.wacom.ink.rasterization.StrokeBrush;
import com.wacom.ink.rasterization.StrokeRenderer;

/**
 * Created by yarolegovich on 03.06.2016.
 */
public class StandardBrushTool implements Tool {

    @Override
    public void onMotionEvent(
            DrawingArea drawingArea,
            MotionEvent event,
            Layer raster,
            boolean isPathFinished) {
        StrokeRenderer strokeRenderer = drawingArea.strokeRenderer;
        PathBuilder pathBuilder = drawingArea.pathBuilder;

        boolean isActionUp = event.getAction() == MotionEvent.ACTION_UP;

        strokeRenderer.drawPoints(
                pathBuilder.getPathBuffer(),
                pathBuilder.getPathLastUpdatePosition(),
                pathBuilder.getAddedPointsSize(),
                isActionUp);
        strokeRenderer.drawPrelimPoints(
                pathBuilder.getPreliminaryPathBuffer(), 0,
                pathBuilder.getFinishedPreliminaryPathSize());

        InkCanvas inkCanvas = drawingArea.inkCanvas;
        Layer inkStrokeLayer = drawingArea.inkStrokeLayer;
        Layer inkCurrentFrameLayer = drawingArea.inkCurrentFrameLayer;

        if (!isActionUp) {
            inkCanvas.setTarget(inkCurrentFrameLayer, strokeRenderer.getStrokeUpdatedArea());
            inkCanvas.drawLayer(raster, BlendMode.BLENDMODE_OVERWRITE);
            inkCanvas.drawLayer(inkStrokeLayer, BlendMode.BLENDMODE_NORMAL);
            strokeRenderer.blendStrokeUpdatedArea(inkCurrentFrameLayer, BlendMode.BLENDMODE_NORMAL);
        } else {
            strokeRenderer.blendStroke(inkStrokeLayer, BlendMode.BLENDMODE_NORMAL);
            inkCanvas.setTarget(inkCurrentFrameLayer);
            inkCanvas.drawLayer(raster, BlendMode.BLENDMODE_OVERWRITE);
            inkCanvas.drawLayer(inkStrokeLayer, BlendMode.BLENDMODE_NORMAL);
        }

        drawingArea.renderView();

        if (isPathFinished) {
            Stroke stroke = Stroke.from(drawingArea);
            drawingArea.getStrokeList().add(stroke);
        }
    }


    @Override
    public void init(DrawingArea drawingArea) {
        StrokeBrush brush = new SolidColorBrush();
        drawingArea.paint.setStrokeBrush(brush);
        drawingArea.paint.setColor(drawingArea.getColor());
        drawingArea.paint.setWidth(Float.NaN);
    }

    @Override
    public void release() {

    }
}
