package com.yarolegovich.motionink;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yarolegovich.motionink.adapter.GifGalleryAdapter;

import java.io.File;

/**
 * Created by yarolegovich on 04.06.2016.
 */
@SuppressWarnings("ConstantConditions")
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        findViewById(R.id.btn_new).setOnClickListener(v -> openWorkspace(true));
        findViewById(R.id.btn_existing).setOnClickListener(v -> openWorkspace(false));

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(this, 3));
        File dir = new File(Environment.getExternalStorageDirectory() + "/motionink");
        list.setAdapter(new GifGalleryAdapter(dir.listFiles()));
    }

    private void openWorkspace(boolean startFromScratch) {
        startActivity(MainActivity.callingIntent(this, startFromScratch));
    }
}
