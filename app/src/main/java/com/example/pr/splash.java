package com.example.pr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splash extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        VideoView VideoView = (VideoView) findViewById(R.id.vdSplash);
        VideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash));

        //new set media controller
        MediaController mediaController = new MediaController(this);
        VideoView.setMediaController(mediaController);
        VideoView.start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splash.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}