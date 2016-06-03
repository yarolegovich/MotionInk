package com.yarolegovich.motionink.draw;

import com.yarolegovich.motionink.view.SlideView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class SlideManager implements SlideView.SlideSelectionListener {

    private List<Stroke> backgroundStrokes;
    private StrokeSerializer strokeSerializer;

    private int currentPosition;

    private DrawingArea drawingArea;

    public SlideManager(DrawingArea drawingArea) {
        this.drawingArea = drawingArea;

        strokeSerializer = new StrokeSerializer(drawingArea.getContext());
        backgroundStrokes = new LinkedList<>();
    }

    @Override
    public void onSlideSelected(int slidePosition) {
        if (slidePosition == currentPosition) {
            return;
        }
        strokeSerializer.setDimension(drawingArea.getWidth(), drawingArea.getHeight());
        strokeSerializer.saveStrokes(currentPosition, drawingArea.getStrokeList());
        if (slidePosition != SlideView.POSITION_BG) {
            drawingArea.setBackgroundStrokes(backgroundStrokes);
            List<Stroke> strokes = strokeSerializer.loadStrokes(slidePosition);
            drawingArea.setStrokeList(strokes);
            List<Stroke> previousSlideStrokes = slidePosition != currentPosition + 1 ?
                    strokeSerializer.loadStrokes(slidePosition - 1) :
                    drawingArea.getStrokeList();
            drawingArea.setPreviousSlideStrokes(previousSlideStrokes);
        } else {
            drawingArea.setBackgroundStrokes(Collections.emptyList());
            drawingArea.setStrokeList(backgroundStrokes);
            drawingArea.setPreviousSlideStrokes(Collections.emptyList());
        }
        currentPosition = slidePosition;
    }

    @Override
    public void onSlideRemoved(int slidePosition) {

    }
}

