package com.example.pr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
    }

    public void onClickBookPage(View view)
    {
        Intent go= new Intent(MainActivity.this, Book_page.class);
        go.putExtra("type", "book");
        startActivity(go);
    }
    public void onClickToysPage(View view)
    {
        Intent go= new Intent(MainActivity.this, Book_page.class);
        go.putExtra("type", "toy");
        startActivity(go);
    }

    public void onClickDevicesPage(View view) {
        Intent go= new Intent(MainActivity.this, Book_page.class);
        go.putExtra("type", "device");
        startActivity(go);
    }
}