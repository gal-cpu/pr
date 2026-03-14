package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends AppCompatActivity {
    private String current_userId="";
    DatabaseService databaseService;

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

        databaseService = DatabaseService.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        assert mAuth.getCurrentUser() != null;
        current_userId = mAuth.getCurrentUser().getUid();
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

    public void userProfileClick(View view) {
        Intent go = new Intent(UserProfile.this, UpdateUser.class);
        go.putExtra("USER_UID", current_userId);
        go.putExtra("Deliver", "U");
        startActivity(go);
    }

    public void cartClick(View view) {
        Intent go = new Intent(UserProfile.this, CartList.class);
        startActivity(go);
    }

    public void LogOutClick(View view) {
        Intent go = new Intent(UserProfile.this, HomePage.class);
        startActivity(go);
        finish();
    }

    public void FavoriesClick(View view) {
        Intent go = new Intent(UserProfile.this, Favorites.class);
        startActivity(go);
    }
}