package com.yarolegovich.motionink.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yarolegovich.motionink.R;
import com.yarolegovich.motionink.util.Utils;

/**
 * Created by yarolegovich on 03.06.2016.
 */
public class SlideView extends HorizontalScrollView {

    private static final int FIRST_SLIDE_INDEX = 1;

    public static final int POSITION_BG = -1;

    private final RemoveOnClickListener removeOnClickListener = new RemoveOnClickListener();
    private final SlideSelectListener slideSelectListener = new SlideSelectListener();

    private SlideSelectionListener listener;

    private LinearLayout container;
    private int slideCount;

    private int currentSlide;

    public SlideView(Context context) {
        super(context);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorSlideView));
        setHorizontalScrollBarEnabled(false);
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.HORIZONTAL);
        int topPadding = Utils.dpToPx(getContext(), 4);
        container.setPadding(0, topPadding, 0, topPadding);
        addView(container);

        this.container = container;

        View bg = createSlide("BG");
        container.addView(bg);

        container.addView(createAddNewSlide());

        addSlide();

        post(() -> fullScroll(HorizontalScrollView.FOCUS_LEFT));
    }

    public void addSlide() {
        slideCount++;
        View slide = createSlide(String.valueOf(slideCount));
        container.addView(slide, lastSlideIndex());
        assertCantRemoveAllSlides();
        post(() -> fullScroll(HorizontalScrollView.FOCUS_RIGHT));
    }

    public void selectSlide(int position) {
        container.getChildAt(FIRST_SLIDE_INDEX + position).callOnClick();
    }

    public void reInit(int noOfSlides) {
        for (int i = lastSlideIndex() - 1; i > FIRST_SLIDE_INDEX; i--) {
            container.removeView(container.getChildAt(i));
        }
        addSlides(noOfSlides);
        selectSlide(0);
    }

    public void addSlides(int noOfSlides) {
        for (int i = 0; i < noOfSlides; i++) {
            addSlide();
        }
    }

    private void assertCantRemoveAllSlides() {
        SlideHolder firstSlideHolder = (SlideHolder) container
                .getChildAt(FIRST_SLIDE_INDEX)
                .getTag();
        firstSlideHolder.setRemovable(slideCount > 1);
    }

    private View createSlide(String labelText) {
        View slide = LayoutInflater.from(getContext()).inflate(R.layout.view_slide, container, false);
        SlideHolder slideHolder = new SlideHolder(slide);
        slideHolder.setRemovable(slideCount != 1 && TextUtils.isDigitsOnly(labelText));
        slideHolder.setPosition(slideCount - 1);
        slideHolder.setLabel(labelText);
        slide.setTag(slideHolder);
        return slide;
    }

    private View createAddNewSlide() {
        View slide = LayoutInflater.from(getContext()).inflate(R.layout.view_add_new_slide, container, false);
        slide.setOnClickListener(v -> addSlide());
        return slide;
    }

    public int getCurrentSlide() {
        return currentSlide;
    }

    public int noOfSlides() {
        return container.getChildCount() - 2;
    }

    public void setListener(SlideSelectionListener listener) {
        this.listener = listener;
    }

    private int lastSlideIndex() {
        return container.getChildCount() - 1;
    }

    public interface SlideSelectionListener {
        void onSlideSelected(int slidePosition);

        void onSlideRemoved(int slidePosition);
    }

    private class SlideSelectListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            SlideHolder holder = (SlideHolder) v.getTag();
            int position = holder.position;
            currentSlide = position;
            if (listener != null) {
                listener.onSlideSelected(position);
            }
        }
    }

    private class RemoveOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            SlideHolder slideHolder = (SlideHolder) v.getTag();

            int position = slideHolder.position;
            if (currentSlide == position) {

            }

            container.removeView(slideHolder.slideView);

            for (int index = FIRST_SLIDE_INDEX; index < lastSlideIndex(); index++) {
                int slidePosition = index - FIRST_SLIDE_INDEX;
                View slideView = container.getChildAt(index);
                SlideHolder holder = (SlideHolder) slideView.getTag();
                holder.setLabel(String.valueOf(slidePosition + 1));
                holder.setPosition(slidePosition);
            }

            slideCount--;

            assertCantRemoveAllSlides();

            if (listener != null) {
                listener.onSlideRemoved(position);
            }
        }
    }

    private class SlideHolder {

        private int position;

        private View slideView;

        private TextView label;
        private View removeSlideBtn;

        public SlideHolder(View slideView) {
            this.slideView = slideView;

            label = (TextView) slideView.findViewById(R.id.label);
            removeSlideBtn = slideView.findViewById(R.id.btn_remove_slide);
            removeSlideBtn.setTag(this);

            slideView.setOnClickListener(slideSelectListener);
        }

        public void setLabel(String labelText) {
            label.setText(labelText);
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setRemovable(boolean isRemovable) {
            if (isRemovable) {
                removeSlideBtn.setVisibility(View.VISIBLE);
                removeSlideBtn.setOnClickListener(removeOnClickListener);
            } else {
                removeSlideBtn.setVisibility(GONE);
            }
        }
    }
}
