package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void IVuserClick(View view) {
        Intent go = new Intent(UserProfile.this, UserProfile.class);
        startActivity(go);
    }

    public void IVcartClick(View view) {
        Intent go = new Intent(UserProfile.this, CartList.class);
        startActivity(go);
    }

    public void IVhouseClick(View view) {
        Intent go = new Intent(UserProfile.this, MainActivity.class);
        startActivity(go);
    }

    public void favorite(View view) {
    }

    public void userProfileClick(View view) {
    }

    public void cartClick(View view) {
        Intent go = new Intent(UserProfile.this, CartList.class);
        startActivity(go);
    }
}