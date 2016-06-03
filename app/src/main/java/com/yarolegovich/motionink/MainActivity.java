package com.yarolegovich.motionink;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import com.yarolegovich.motionink.draw.DrawingArea;
import com.yarolegovich.motionink.draw.EraserTool;
import com.yarolegovich.motionink.draw.SlideManager;
import com.yarolegovich.motionink.draw.StandardBrushTool;
import com.yarolegovich.motionink.util.Permissions;
import com.yarolegovich.motionink.view.SlideView;
import com.yarolegovich.motionink.view.ToolPanelView;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements ToolPanelView.OnToolSelectedListener {

    private DrawingArea drawingArea;

    private View brushConfigFab;
    private int fabTranslation;

    private Permissions permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        SlideView slideView = (SlideView) findViewById(R.id.slide_view);
        slideView.setListener(new SlideManager(drawingArea));

        permissionHelper = new Permissions(this);
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
                openCameraActivity();
                break;
            case R.id.toolpanel_done:
                break;
        }
    }

    private void openCameraActivity() {
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        permissionHelper.doIfPermitted(() -> startActivity(cameraIntent), Manifest.permission.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handleGrantResults(grantResults);
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
