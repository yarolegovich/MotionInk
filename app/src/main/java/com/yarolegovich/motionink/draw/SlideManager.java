package com.yarolegovich.motionink.draw;

import android.graphics.Bitmap;

import com.yarolegovich.motionink.draw.persist.Serializer;
import com.yarolegovich.motionink.draw.persist.SlideImageSerializer;
import com.yarolegovich.motionink.draw.persist.StrokeSerializer;
import com.yarolegovich.motionink.view.SlideView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class SlideManager implements SlideView.SlideSelectionListener {

    private List<Stroke> backgroundStrokes;

    private Serializer<byte[], Bitmap> slideImageSerializer;
    private Serializer<List<Stroke>, List<Stroke>> strokeSerializer;

    private int numberOfSlides;
    private int currentPosition;

    private DrawingArea drawingArea;

    public SlideManager(DrawingArea drawingArea) {
        this.drawingArea = drawingArea;

        strokeSerializer = new StrokeSerializer(drawingArea.getContext());
        slideImageSerializer = new SlideImageSerializer(drawingArea.getContext());

        backgroundStrokes = new LinkedList<>();
    }

    @Override
    public void onSlideSelected(int slidePosition) {
        if (slidePosition == currentPosition) {
            return;
        }
        strokeSerializer.save(currentPosition, drawingArea.getStrokeList());
        drawingArea.displayRaster(slideImageSerializer.load(slidePosition));
        if (slidePosition != SlideView.POSITION_BG) {
            drawingArea.setBackgroundStrokes(backgroundStrokes);
            List<Stroke> strokes = strokeSerializer.load(slidePosition);
            drawingArea.setStrokeList(strokes);
            List<Stroke> previousSlideStrokes = slidePosition != currentPosition + 1 ?
                    strokeSerializer.load(slidePosition - 1) :
                    drawingArea.getStrokeList();
            drawingArea.setPreviousSlideStrokes(previousSlideStrokes);
        } else {
            drawingArea.setBackgroundStrokes(Collections.emptyList());
            drawingArea.setStrokeList(backgroundStrokes);
            drawingArea.setPreviousSlideStrokes(Collections.emptyList());
        }
        currentPosition = slidePosition;
    }

    public void nextSlide() {
        onSlideSelected((currentPosition + 1) % numberOfSlides);
    }

    public int getNumberOfSlides() {
        return Math.max(strokeSerializer.noOfSlides(), slideImageSerializer.noOfSlides());
    }

    @Override
    public void onSlideRemoved(int slidePosition) {
        strokeSerializer.remove(slidePosition);
        slideImageSerializer.remove(slidePosition);
    }

    public void reinitialize(int slidePosition) {
        currentPosition = Integer.MIN_VALUE;
        onSlideSelected(slidePosition);
    }

    public void setNumberOfSlides(int numberOfSlides) {
        this.numberOfSlides = numberOfSlides;
    }
}

