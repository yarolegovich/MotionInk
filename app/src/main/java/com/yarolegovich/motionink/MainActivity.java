package com.yarolegovich.motionink;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yarolegovich.motionink.view.SlideView;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlideView slideView = (SlideView) findViewById(R.id.slide_view);
        for (int i = 0; i < 10; i++) {
            slideView.addSlide();
        }
    }
}
