package com.yarolegovich.motionink.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.yarolegovich.motionink.R;
import com.yarolegovich.motionink.util.Utils;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class PresentationSpeedView extends LinearLayout {

    private final int MAX = 15;

    private int animationSpeed = 100;

    public PresentationSpeedView(Context context) {
        super(context);
    }

    public PresentationSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PresentationSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PresentationSpeedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        setBackgroundColor(Color.WHITE);
        setOrientation(VERTICAL);
        int topMargin = Utils.dpToPx(getContext(), 16);
        setPadding(0, topMargin, 0, topMargin * 2);

        inflate(getContext(), R.layout.view_presentation_speed, this);

        AppCompatSeekBar seekBar = (AppCompatSeekBar) findViewById(R.id.seekbar);
        seekBar.setMax(MAX);
        seekBar.setOnSeekBarChangeListener(new AnimationSpeedController());
        seekBar.setProgress(9);
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }

    private class AnimationSpeedController implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            animationSpeed = 50 * (MAX - progress - 1);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
