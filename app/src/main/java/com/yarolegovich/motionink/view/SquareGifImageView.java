package com.yarolegovich.motionink.view;

import android.content.Context;
import android.util.AttributeSet;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class SquareGifImageView extends GifImageView {
    public SquareGifImageView(Context context) {
        super(context);
    }

    public SquareGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareGifImageView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
