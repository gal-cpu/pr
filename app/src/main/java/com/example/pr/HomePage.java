package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClicksignUp(View view)
    {
        Intent go= new Intent(HomePage.this, SignUp.class);
        startActivity(go);
    }

    public void onClickLogIn(View view)
    {
        Intent go= new Intent(HomePage.this, LogIn.class);
        startActivity(go);
    }

    public void onClickAudot(View view)
    {
        Intent go= new Intent(HomePage.this, MainActivity.class);
        startActivity(go);
    }
}