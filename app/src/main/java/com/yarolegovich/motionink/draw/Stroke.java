package com.yarolegovich.motionink.draw;

import android.graphics.RectF;

import com.wacom.ink.manipulation.Intersectable;
import com.wacom.ink.manipulation.Intersector;
import com.wacom.ink.path.PathBuilder;
import com.wacom.ink.rasterization.BlendMode;
import com.wacom.ink.rasterization.StrokePaint;
import com.wacom.ink.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by yarolegovich on 03.06.2016.
 */
public class Stroke implements Intersectable {

    public static Stroke from(DrawingArea drawingArea) {
        PathBuilder pathBuilder = drawingArea.pathBuilder;
        StrokePaint paint = drawingArea.paint;
        Stroke stroke = new Stroke();
        stroke.copyPoints(pathBuilder.getPathBuffer(), 0, pathBuilder.getPathSize());
        stroke.setStride(pathBuilder.getStride());
        stroke.setWidth(paint.getWidth());
        stroke.setColor(drawingArea.getColor());
        stroke.setInterval(0.0f, 1.0f);
        stroke.setBlendMode(BlendMode.BLENDMODE_NORMAL);
        stroke.calculateBounds();
        return stroke;
    }

    public static Stroke from(Stroke stroke, Intersector.Interval interval) {
        int size = interval.toIndex - interval.fromIndex + stroke.getStride();
        Stroke newStroke = new Stroke(size);
        newStroke.copyPoints(stroke.getPoints(), interval.fromIndex, size);
        newStroke.setStride(stroke.getStride());
        newStroke.setColor(stroke.getColor());
        newStroke.setWidth(stroke.getWidth());
        newStroke.setBlendMode(stroke.getBlendMode());
        newStroke.setInterval(interval.fromValue, interval.toValue);
        newStroke.calculateBounds();
        return newStroke;
    }

    private FloatBuffer points;
    private int color;
    private int stride;
    private int size;
    private float width;
    private float startT;
    private float endT;
    private BlendMode blendMode = BlendMode.BLENDMODE_NORMAL;

    private RectF bounds;
    private FloatBuffer segmentsBounds;

    public Stroke(){
        bounds = new RectF();
    }

    public Stroke(int size) {
        this();
        setPoints(Utils.createNativeFloatBufferBySize(size), size);
        startT = 0.0f;
        endT = 1.0f;
    }

    @Override
    public int getStride() {
        return stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }

    @Override
    public FloatBuffer getPoints() {
        return points;
    }

    public int getSize() {
        return size;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getStartValue() {
        return startT;
    }

    @Override
    public float getEndValue() {
        return endT;
    }

    public void setInterval(float startT, float endT) {
        this.startT = startT;
        this.endT = endT;
    }

    public void setPoints(FloatBuffer points, int pointsSize) {
        size = pointsSize;
        this.points = points;
    }

    public void copyPoints(FloatBuffer source, int sourcePosition, int size) {
        this.size = size;
        points = ByteBuffer.allocateDirect(size * Float.SIZE/Byte.SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        Utils.copyFloatBuffer(source, points, sourcePosition, 0, size);
    }

    @Override
    public FloatBuffer getSegmentsBounds() {
        return segmentsBounds;
    }

    @Override
    public RectF getBounds() {
        return bounds;
    }

    public void setBlendMode(BlendMode blendMode){
        this.blendMode = blendMode;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public void calculateBounds(){
        RectF segmentRect = new RectF();
        Utils.invalidateRectF(bounds);
        FloatBuffer segmentsBounds = Utils.createNativeFloatBuffer(PathBuilder.calculateSegmentsCount(size, stride) * 4);
        segmentsBounds.position(0);
        for (int i = 0; i < PathBuilder.calculateSegmentsCount(size, stride); i++){
            PathBuilder.calculateSegmentBounds(
                    getPoints(), getStride(), getWidth(), i,
                    0.0f, segmentRect);
            segmentsBounds.put(segmentRect.left);
            segmentsBounds.put(segmentRect.top);
            segmentsBounds.put(segmentRect.width());
            segmentsBounds.put(segmentRect.height());
            Utils.uniteWith(bounds, segmentRect);
        }
        this.segmentsBounds = segmentsBounds;
    }

    @Override
    public String toString() {
        return "Stroke{" +
                "points=" + points +
                ", color=" + color +
                ", stride=" + stride +
                ", size=" + size +
                ", width=" + width +
                ", startT=" + startT +
                ", endT=" + endT +
                ", blendMode=" + blendMode +
                ", bounds=" + bounds +
                ", segmentsBounds=" + segmentsBounds +
                '}';
    }
}

