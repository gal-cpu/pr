package com.example.pr.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.R;

public class HomePage extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 1;


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
        //checkNotificationPermission();
    }

    public void onClicksignUp(View view) {
        Intent go = new Intent(HomePage.this, SignUp.class);
        startActivity(go);
    }

    public void onClickLogIn(View view) {
        Intent go = new Intent(HomePage.this, LogIn.class);
        startActivity(go);
    }

    public void onClickAudot(View view) {
        Intent go = new Intent(HomePage.this, Information_page.class);
        startActivity(go);
    }



//    private void checkNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//
 //                ActivityCompat.requestPermissions(
//                        this,
//                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
//                        NOTIFICATION_PERMISSION_CODE
//                );
//            }
 //       }
//    }

    // ---- שליחת הודעה למשתמש בעת אישור/דחיית ההרשאה ----
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // אם יש תוצאה והיא מתן הרשאה
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "הרשאה ניתנה!", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this, "הרשאה נדחתה!", Toast.LENGTH_SHORT).show();


            }
        }
    }
}