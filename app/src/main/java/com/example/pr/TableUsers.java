package com.example.pr;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.adapers.UsersAdapter;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class TableUsers extends AppCompatActivity {


    private static final String TAG = "ItemsActivity";

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    DatabaseService databaseService;
    private String selectedCategory; // משתנה לאחסון הקטגוריה שנבחרה
    private SearchView searchView;
    private List<User> allIusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_table_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // קבלת שם הקטגוריה מ-Intent
        //selectedCategory = getIntent().getStringExtra("type");

        databaseService = DatabaseService.getInstance();

        recyclerView = findViewById(R.id.rcItemes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersAdapter = new UsersAdapter();
        recyclerView.setAdapter(usersAdapter);

        fetchItemsFromFirebase();
    }

    private void fetchItemsFromFirebase() {

        // טעינת המוצרים
        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                // Log.d(TAG, "onCompleted: " + users);
                allIusers = users;
                usersAdapter.setUsers(users);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load items: ", e);
                new android.app.AlertDialog.Builder(TableUsers.this)
                        .setMessage("נראה שקרתה תקלה בטעינת המוצרים, נסה שוב מאוחר יותר")
                        .setPositiveButton("אוקי", null)
                        .show();
            }
        });
    }
}