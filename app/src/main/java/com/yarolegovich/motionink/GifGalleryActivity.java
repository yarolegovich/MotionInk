package com.yarolegovich.motionink;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yarolegovich.motionink.adapter.GifGalleryAdapter;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class GifGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitivty_gallery);



    }
}
