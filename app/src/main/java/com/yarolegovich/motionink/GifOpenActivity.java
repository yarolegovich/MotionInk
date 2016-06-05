package com.yarolegovich.motionink;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.yarolegovich.motionink.util.Utils;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
@SuppressWarnings("ConstantConditions")
public class GifOpenActivity extends AppCompatActivity {

    public static final String EXTRA_GIF = "extra_gif";

    private Uri gifUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);

        GifImageView gifImageView = (GifImageView) findViewById(R.id.gif_view);
        gifUri = getIntent().getParcelableExtra(EXTRA_GIF);
        gifImageView.setImageURI(gifUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gif_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.share:
                Intent shareIntent = Utils.createGifShareIntent(gifUri);
                startActivity(Intent.createChooser(shareIntent, "Share with"));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
