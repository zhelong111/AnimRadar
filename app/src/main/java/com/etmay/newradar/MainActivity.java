package com.etmay.newradar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    RadarView radarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radarView = findViewById(R.id.radar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        radarView.destroy();
    }
}
