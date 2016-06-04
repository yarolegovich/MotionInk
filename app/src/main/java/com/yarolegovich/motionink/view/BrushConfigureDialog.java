package com.yarolegovich.motionink.view;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.yarolegovich.motionink.R;
import com.yarolegovich.motionink.util.AnimationEndListener;
import com.yarolegovich.motionink.util.AnimationStartListener;
import com.yarolegovich.motionink.util.Utils;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class BrushConfigureDialog {

    private OnBrushColorChanged colorListener;
    private OnBrushWidthChanged widthListener;

    private BottomSheetBehavior behavior;
    private FloatingActionButton fab;

    public BrushConfigureDialog(View bottomSheet) {
        fab = (FloatingActionButton) bottomSheet.findViewById(R.id.fab);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(true);
        behavior.setPeekHeight(Utils.dpToPx(bottomSheet.getContext(), 132));

        fab.setOnClickListener(v -> {
            if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                expand();
            } else {
                hide();
            }
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fab.animate().rotation(0).setDuration(500)
                            .setListener(new AnimationStartListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    fab.setImageResource(R.drawable.ic_done_white_24dp);
                                }
                            }).start();
                } else {
                    fab.animate().rotation(-180f).setDuration(500)
                            .setListener(new AnimationEndListener() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    fab.setImageResource(R.drawable.ic_settings_white_24dp);
                                }
                            }).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        AppCompatSeekBar widthBar = (AppCompatSeekBar) bottomSheet.findViewById(R.id.width_slider);
        widthBar.setMax(150);
        widthBar.setProgress(20);
        widthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (widthListener != null) {
                    widthListener.onNewWidth(progress + 5);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ColorPickerView colorPickerView = (ColorPickerView) bottomSheet.findViewById(R.id.color_picker_view);
        colorPickerView.addOnColorSelectedListener(i -> {
            if (colorListener != null) {
                colorListener.onNewColor(i);
            }
        });
    }

    public void collapse() {
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void hide() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void expand() {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void setColorListener(OnBrushColorChanged colorListener) {
        this.colorListener = colorListener;
    }

    public void setWidthListener(OnBrushWidthChanged widthListener) {
        this.widthListener = widthListener;
    }

    public interface OnBrushColorChanged {
        void onNewColor(int newColor);
    }

    public interface OnBrushWidthChanged {
        void onNewWidth(int newWidth);
    }
}
