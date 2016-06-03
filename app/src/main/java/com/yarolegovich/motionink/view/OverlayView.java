package com.yarolegovich.motionink.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class OverlayView extends View {

    private Paint transparentRectPaint;
    private RectF transparentRect;

    private int overlayColor;

    public OverlayView(Context context) {
        super(context);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        transparentRect = new RectF();

        overlayColor = Color.BLACK;

        transparentRectPaint = new Paint();
        transparentRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparentRectPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        setLayerType(View.LAYER_TYPE_SOFTWARE, transparentRectPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(overlayColor);
        canvas.drawRect(transparentRect, transparentRectPaint);
    }

    @SuppressWarnings("Convert2Lambda")
    public void overlay(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                float width = view.getWidth();
                float height = view.getHeight();
                float minSide = Math.min(width, height);
                float halfSide = (minSide / 2f);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                transparentRect.left = centerX - halfSide;
                transparentRect.top = centerY - halfSide;
                transparentRect.right = centerX + halfSide;
                transparentRect.bottom = centerY + halfSide;
                invalidate();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
