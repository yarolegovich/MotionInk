package com.yarolegovich.motionink.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yarolegovich.motionink.R;
import com.yarolegovich.motionink.util.Utils;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class PresentationControlView extends LinearLayout {

    private final ControlClickListener ON_CONTROL_CLICK = new ControlClickListener();

    private OnPresentationControlListener listener;

    public PresentationControlView(Context context) {
        super(context);
    }

    public PresentationControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PresentationControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PresentationControlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        setOrientation(HORIZONTAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        int sidePadding = Utils.dpToPx(getContext(), 4);
        int topPadding = Utils.dpToPx(getContext(), 8);
        setPadding(sidePadding, topPadding, sidePadding, topPadding);

        inflate(getContext(), R.layout.view_presentation_control, this);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ImageView) {
                child.setOnClickListener(ON_CONTROL_CLICK);
            }
        }
    }

    public void setListener(OnPresentationControlListener listener) {
        this.listener = listener;
    }

    private class ControlClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onPresentationActionSelected(v.getId());
            }
        }
    }

    public interface OnPresentationControlListener {
        void onPresentationActionSelected(int actionId);
    }
}
