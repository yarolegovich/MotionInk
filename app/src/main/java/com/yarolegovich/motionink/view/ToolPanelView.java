package com.yarolegovich.motionink.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yarolegovich.motionink.R;
import com.yarolegovich.motionink.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yarolegovich on 03.06.2016.
 */
public class ToolPanelView extends LinearLayout {

    private final OnToolClickListener ON_TOOL_CLICK_LISTENER = new OnToolClickListener();

    private OnToolSelectedListener listener;
    private List<ToolInterface> toolInterfaceList;

    public ToolPanelView(Context context) {
        super(context);
    }

    public ToolPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToolPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        setOrientation(HORIZONTAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        int sidePadding = Utils.dpToPx(getContext(), 4);
        int topPadding = Utils.dpToPx(getContext(), 8);
        setPadding(sidePadding, topPadding, sidePadding, topPadding);

        inflate(getContext(), R.layout.view_tool_panel, this);

        toolInterfaceList = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ImageView) {
                child.setOnClickListener(ON_TOOL_CLICK_LISTENER);
                toolInterfaceList.add(new ToolInterface((ImageView) child));
            }
        }
    }

    public void setSelected(@IdRes int toolId) {
        getTool(toolId).setSelected(true);
    }

    public ToolInterface getTool(@IdRes int viewId) {
        for (ToolInterface toolInterface : toolInterfaceList) {
            if (toolInterface.wrappedTool.getId() == viewId) {
                return toolInterface;
            }
        }
        throw new IllegalArgumentException("No tool with such id");
    }

    public void setListener(OnToolSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnToolSelectedListener {
        void onToolSelected(int id, ToolInterface toolInterface);
    }

    private class OnToolClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onToolSelected(v.getId(), getTool(v.getId()));
            }
        }
    }

    public class ToolInterface {

        private ImageView wrappedTool;
        private boolean isSelected;

        public ToolInterface(ImageView wrappedTool) {
            this.wrappedTool = wrappedTool;
        }

        public void setSelected(boolean selected) {
            if (isSelected == selected) {
                return;
            }
            if (selected) {
                for (ToolInterface toolInterface : toolInterfaceList) {
                    if (toolInterface != this) {
                        toolInterface.setSelected(false);
                    }
                }
                wrappedTool.setBackground(getBgDrawable(true));
            } else {
                wrappedTool.setBackground(getBgDrawable(false));
            }
            isSelected = selected;
        }

        public void setEnabled(boolean enabled) {

        }
    }

    private Drawable getBgDrawable(boolean isSelected) {
        LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.tool_bg);
        if (!isSelected) {
            drawable.findDrawableByLayerId(R.id.circle_selected).setAlpha(0);
        } else {
            drawable.findDrawableByLayerId(R.id.circle_selected).setAlpha(255);
        }
        return drawable;
    }

}
