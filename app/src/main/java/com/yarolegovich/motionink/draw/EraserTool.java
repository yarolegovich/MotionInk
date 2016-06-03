package com.yarolegovich.motionink.draw;

import android.view.MotionEvent;

import com.wacom.ink.manipulation.Intersector;
import com.wacom.ink.path.PathBuilder;
import com.wacom.ink.rasterization.Layer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class EraserTool implements Tool {

    private Intersector<Stroke> intersector;

    public EraserTool() {
        intersector = new Intersector<>();
    }

    @Override
    public void onMotionEvent(
            DrawingArea drawingArea,
            MotionEvent event, Layer raster,
            boolean isPathFinished) {
        List<Stroke> presentStrokes = drawingArea.getStrokeList();
        PathBuilder pathBuilder = drawingArea.pathBuilder;
        if (presentStrokes.size() > 0) {
            intersector.setTargetAsStroke(
                    pathBuilder.getPathBuffer(), pathBuilder.getPathLastUpdatePosition(),
                    pathBuilder.getAddedPointsSize(), pathBuilder.getStride());

            List<Stroke> removedStrokes = new LinkedList<>();
            List<Stroke> newStrokes = new LinkedList<>();

            for (Stroke stroke : presentStrokes) {
                Intersector.IntersectionResult intersection = intersector.intersectWithTarget(stroke);

                if (intersection.getCount() == 1) {
                    Intersector.IntervalIterator iterator = intersection.getIterator();
                    if (iterator.next().inside) {
                        removedStrokes.add(stroke);
                    }
                } else if (intersection.getCount() > 1) {
                    removedStrokes.add(stroke);
                    Intersector.IntervalIterator iterator = intersection.getIterator();
                    while (iterator.hasNext()) {
                        Intersector.Interval interval = iterator.next();
                        if (!interval.inside) {
                            Stroke newStroke = Stroke.from(stroke, interval);
                            newStrokes.add(newStroke);
                        }
                    }
                }
            }

            presentStrokes.removeAll(removedStrokes);
            presentStrokes.addAll(newStrokes);

            drawingArea.drawStrokes();
            drawingArea.renderView();
        }
    }


    @Override
    public void init(DrawingArea drawingArea) {

    }

    @Override
    public void release() {

    }
}

