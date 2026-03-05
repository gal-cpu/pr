package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

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

public class TableUsers extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private UsersAdapter usersAdapter;
    private ScrollView scrollViewFilter;
    private TextView optionOne, optionTwo, optionThree;
    private View ToggleFilter;
    private LinearLayout optionsContainer;
    private String selectedCategory; // משתנה לאחסון הקטגוריה שנבחרה
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

        databaseService = DatabaseService.getInstance();

        RecyclerView recyclerView = findViewById(R.id.rcUsers);

        scrollViewFilter= findViewById(R.id.ScrollViewFilter);

        scrollViewFilter.setSmoothScrollingEnabled(true);
        optionsContainer = findViewById(R.id.optionsContainer); // וודא שיש ID כזה ב-XML
        ToggleFilter = findViewById(R.id.btnShowOptions); // הכפתור הראשי שפותח
        optionOne = findViewById(R.id.option1);
        optionTwo = findViewById(R.id.option2);
        optionThree=findViewById(R.id.option3);

        // 2. הגדרת מאזינים (כולם מפנים ל-onClick שנמצא למטה)
        ToggleFilter.setOnClickListener(this);
        optionOne.setOnClickListener(this);
        optionTwo.setOnClickListener(this);
        optionThree.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersAdapter = new UsersAdapter(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                // Handle user click
                Log.d(TAG, "User clicked: " + user);
                Intent intent = new Intent(TableUsers.this, UpdateUser.class);
                intent.putExtra("USER_UID", user.getId());
                startActivity(intent);
            }

            @Override
            public void onLongUserClick(User user) {
                // Handle long user click
                Log.d(TAG, "User long clicked: " + user);
            }

        });
        recyclerView.setAdapter(usersAdapter);

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

    private void filterUsersByCategory() {
        ArrayList<User> filteredUsers = new ArrayList<>();
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            if(selectedCategory.contains("all")){
                filteredUsers.addAll(allIusers);
            } else if (selectedCategory.contains("admin")) {
                // אם נבחרה קטגוריה מסוימת, נבצע סינון
                for (User user : allIusers) {
                    if (user.isAd()) {
                        filteredUsers.add(user);
                    }
                }
            }else if (selectedCategory.contains("user")) {
                // אם נבחרה קטגוריה מסוימת, נבצע סינון
                for (User user : allIusers) {
                    if (!user.isAd()) {
                        filteredUsers.add(user);
                    }
                }
            }

        } else {
            // אם לא נבחרה קטגוריה, נציג את כל המוצרים
            filteredUsers.addAll(allIusers);
        }
        usersAdapter.setUsers(filteredUsers);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnShowOptions) {
            // פתיחה או סגירה של התפריט
            if (optionsContainer.getVisibility() == View.GONE) {
                optionsContainer.setVisibility(View.VISIBLE);
                optionsContainer.setAlpha(0f);
                optionsContainer.animate().alpha(1f).setDuration(300);
            } else {
                optionsContainer.setVisibility(View.GONE);
            }
        }
        else if (id == R.id.option1) {
            // לחיצה על "הכל"
            selectedCategory = "all";
            filterUsersByCategory();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
        else if (id == R.id.option2) {
            // לחיצה על "מנהלים"
            selectedCategory = "admin";
            filterUsersByCategory();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
        else if (id == R.id.option3) {
            // לחיצה על "משתמשים"
            selectedCategory = "user";
            filterUsersByCategory();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
    }

}