package com.example.pr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.SharedPreferencesUtil;

public class splash extends AppCompatActivity {
    Handler handler = new Handler();
    User cachedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        cachedUser = SharedPreferencesUtil.getUser(splash.this);

        VideoView VideoView = (VideoView) findViewById(R.id.vdSplash);
        VideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash));

        //new set media controller
        MediaController mediaController = new MediaController(this);
        VideoView.setMediaController(mediaController);
        VideoView.start();

        handler.postDelayed(() -> {
            if (cachedUser == null || !SharedPreferencesUtil.isUserLoggedIn(splash.this)) {
                goTo(HomePage.class);
                return;
            }

            DatabaseService.getInstance().getUser(cachedUser.getId(), new DatabaseService.DatabaseCallback<>() {
                @Override
                public void onCompleted(User user) {
                    if (user != null) {
                        SharedPreferencesUtil.saveUser(splash.this, user);
                        goTo(user.gatIsAd() ? AdminPage.class : MainActivity.class);
                    } else {
                        // User might have been deleted from the server
                        SharedPreferencesUtil.signOutUser(splash.this);
                        goTo(HomePage.class);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    // In case of network error, etc., log the user out to be safe
                    SharedPreferencesUtil.signOutUser(splash.this);
                    goTo(HomePage.class);
                }
            });
        }, 3000);
    }

    private void goTo(Class<?> target) {
        Intent intent = new Intent(this, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}