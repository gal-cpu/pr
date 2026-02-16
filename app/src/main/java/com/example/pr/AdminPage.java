package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.ChildEvent;

public class AdminPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void GoToAddItem(View view) {
        Intent go = new Intent(AdminPage.this, AddItem.class);
        startActivity(go);
    }

    public void OnClickUsersTable(View view) {
        Intent go = new Intent(AdminPage.this, TableUsers.class);
        startActivity(go);
    }


    public void OnClickItemsTable(View view) {
        Intent go = new Intent(AdminPage.this, TableItems.class);
        startActivity(go);
    }

    public void OnClickWebsite(View view) {
        Intent go = new Intent(AdminPage.this, MainActivity.class);
        startActivity(go);
    }
}