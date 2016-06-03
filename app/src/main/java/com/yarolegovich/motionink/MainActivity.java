package com.yarolegovich.motionink;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import com.yarolegovich.motionink.draw.DrawingArea;
import com.yarolegovich.motionink.draw.EraserTool;
import com.yarolegovich.motionink.draw.StandardBrushTool;
import com.yarolegovich.motionink.view.SlideView;
import com.yarolegovich.motionink.view.ToolPanelView;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements ToolPanelView.OnToolSelectedListener {

    private DrawingArea drawingArea;

    private View brushConfigFab;

    private int fabTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlideView slideView = (SlideView) findViewById(R.id.slide_view);
        for (int i = 0; i < 10; i++) {
            slideView.addSlide();
        }

        int marginBottom = getResources().getDimensionPixelSize(R.dimen.brush_conf_fab_marginBottom);
        int fabSize = getResources().getDimensionPixelSize(R.dimen.fab_size);
        fabTranslation = marginBottom + fabSize;

        brushConfigFab = findViewById(R.id.brush_config_fab);

        setBrushConfigFabVisibility(false);

        ToolPanelView toolPanelView = (ToolPanelView) findViewById(R.id.tool_panel);
        toolPanelView.setListener(this);
        toolPanelView.setSelected(R.id.toolpanel_brush);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.drawing_surface);
        drawingArea = new DrawingArea(surfaceView);
        drawingArea.setDrawingStartedListener(() -> setBrushConfigFabVisibility(false));

    }

    @Override
    public void onToolSelected(int id, ToolPanelView.ToolInterface toolInterface) {
        switch (id) {
            case R.id.toolpanel_back:
                break;
            case R.id.toolpanel_brush:
                drawingArea.setCurrentTool(new StandardBrushTool());
                toolInterface.setSelected(true);
                setBrushConfigFabVisibility(true);
                break;
            case R.id.toolpanel_eraser:
                drawingArea.setCurrentTool(new EraserTool());
                toolInterface.setSelected(true);
                break;
            case R.id.toolpanel_undo:
                break;
            case R.id.toolpanel_redo:
                break;
            case R.id.toolpanel_camera:
                break;
            case R.id.toolpanel_done:
                break;
        }
    }

    private boolean hidden = false;
    private void setBrushConfigFabVisibility(boolean visible) {
        if (visible) {
            if (hidden) {
                hidden = false;
                brushConfigFab.animate().translationY(0).start();
            }
        } else {
            if (!hidden) {
                hidden = true;
                brushConfigFab.animate().translationYBy(fabTranslation).start();
            }
        }
    }
}
