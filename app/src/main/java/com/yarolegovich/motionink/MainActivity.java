package com.yarolegovich.motionink;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.yarolegovich.motionink.draw.DrawingArea;
import com.yarolegovich.motionink.draw.EraserTool;
import com.yarolegovich.motionink.gif.GifEncoderService;
import com.yarolegovich.motionink.gif.GifSaver;
import com.yarolegovich.motionink.draw.SlideManager;
import com.yarolegovich.motionink.draw.StandardBrushTool;
import com.yarolegovich.motionink.util.Permissions;
import com.yarolegovich.motionink.util.Utils;
import com.yarolegovich.motionink.view.BrushConfigureDialog;
import com.yarolegovich.motionink.view.PresentationControlView;
import com.yarolegovich.motionink.view.PresentationSpeedView;
import com.yarolegovich.motionink.view.SlideView;
import com.yarolegovich.motionink.view.ToolPanelView;

import java.io.File;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements ToolPanelView.OnToolSelectedListener,
        PresentationControlView.OnPresentationControlListener {

    private static final int REQUEST_TAKE_PICTURES = 5777;

    private DrawingArea drawingArea;

    private SlideView slideView;
    private SlideManager slideManager;

    private PresentationControlView presentationControlView;
    private PresentationSpeedView presentationSpeedView;

    private Permissions permissionHelper;

    private Handler previewHandler = new Handler();

    private BrushConfigureDialog brushConfigureDialog;

    private GifSaver gifSaver;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifSaver = new GifSaver(this);

        ToolPanelView toolPanelView = (ToolPanelView) findViewById(R.id.tool_panel);
        toolPanelView.setListener(this);
        toolPanelView.setSelected(R.id.toolpanel_brush);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.drawing_surface);
        drawingArea = new DrawingArea(surfaceView);
        drawingArea.setCurrentTool(new StandardBrushTool());
        drawingArea.setDrawingStartedListener(() -> brushConfigureDialog.hide());

        View bottomSheet = findViewById(R.id.bottom_sheet);
        brushConfigureDialog = new BrushConfigureDialog(bottomSheet);
        brushConfigureDialog.setColorListener(drawingArea::setColor);
        brushConfigureDialog.setWidthListener(drawingArea::setWidth);

        slideManager = new SlideManager(drawingArea);
        slideView = (SlideView) findViewById(R.id.slide_view);
        slideView.setListener(slideManager);
        slideView.addSlides(slideManager.getNumberOfSlides() - 1);
        slideManager.reinitialize(0);

        presentationControlView = (PresentationControlView) findViewById(R.id.presentation_controls);
        presentationControlView.setListener(this);
        presentationSpeedView = (PresentationSpeedView) findViewById(R.id.presentation_speed);

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
                brushConfigureDialog.collapse();
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
                switchPresentationViewMode(true);
                break;
        }
    }

    @Override
    public void onPresentationActionSelected(int actionId) {
        switch (actionId) {
            case R.id.presentation_back:
                switchPresentationViewMode(false);
                break;
            case R.id.presentation_share:
                saveGifTo(Utils.createDirIfNotExists(getFilesDir() + "/out"));
                break;
            case R.id.presentation_save:
                permissionHelper.doIfPermitted(
                        () -> saveGifTo(Environment.getExternalStorageDirectory()),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }
    }

    private void saveGifTo(File dir) {
        progressDialog = ProgressDialog.show(this,
                "Please wait...", "",
                true);
        previewHandler.removeCallbacksAndMessages(null);
        gifSaver.prepareFilesToCreateGif(slideNo -> {
            if (slideNo < slideView.noOfSlides()) {
                slideManager.nextSlide();
                gifSaver.addFrame(drawingArea.getCurrentImage());
            } else {
                gifSaver.addFrame(null);
                progressDialog.dismiss();
                switchPresentationViewMode(false);
                Intent createGifIntent = GifEncoderService.callingIntent(
                        this, Uri.fromFile(dir),
                        presentationSpeedView.getAnimationSpeed());
                startService(createGifIntent);
                Toast.makeText(this,
                        "Your gif will be ready in a few minutes",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void switchPresentationViewMode(boolean presentationOn) {
        if (presentationOn) {
            slideManager.persistCurrentSlide();
            drawingArea.setInPresentationMode(true);
            presentationControlView.setVisibility(View.VISIBLE);
            presentationSpeedView.setVisibility(View.VISIBLE);
            slideManager.reinitialize(0);
            slideManager.setNumberOfSlides(slideView.noOfSlides());
            findViewById(R.id.overlay).setOnTouchListener((v, e) -> true);
            previewHandler.post(new Runnable() {
                @Override
                public void run() {
                    slideManager.nextSlide();
                    previewHandler.postDelayed(this, presentationSpeedView.getAnimationSpeed());
                }
            });
        } else {
            drawingArea.setInPresentationMode(false);
            slideManager.reinitialize(slideView.getCurrentSlide());
            previewHandler.removeCallbacksAndMessages(null);
            presentationControlView.setVisibility(View.INVISIBLE);
            presentationSpeedView.setVisibility(View.INVISIBLE);
            findViewById(R.id.overlay).setOnTouchListener(null);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        gifSaver.stop();
        slideManager.persistCurrentSlide();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        switchPresentationViewMode(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        slideManager.reinitialize(slideView.getCurrentSlide());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PICTURES) {
            if (resultCode == RESULT_OK) {
                new Handler().postDelayed(
                        () -> slideManager.reinitialize(slideView.getCurrentSlide()),
                        500);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void openCameraActivity() {
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        permissionHelper.doIfPermitted(
                () -> startActivityForResult(cameraIntent, REQUEST_TAKE_PICTURES),
                Manifest.permission.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handleGrantResults(grantResults);
    }

}
