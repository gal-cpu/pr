package com.example.pr.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.adapers.FavoritesAdapter;
import com.example.pr.model.FavoriteList;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Favorites extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Favorites";
    private DatabaseService databaseService;
    ScrollView scrollViewFilter;
    TextView optionOne, optionTwo, optionThree, optionFore;
    View ToggleFilter;
    private LinearLayout optionsContainer;
    private FavoritesAdapter favoritesAdapter;
    private List<Item> allItems = new ArrayList<>();
    private ArrayList<Item> filteredItems = new ArrayList<>();
    private String selectedCategory, current_userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            current_userId = mAuth.getCurrentUser().getUid();
        }

        // --- תיקון IDs לפי ה-XML שלך ---
        scrollViewFilter = findViewById(R.id.ScrollViewFilterFavorites);
        optionsContainer = findViewById(R.id.optionsContainerFavorites); // תוקן מ-Cart ל-Favorites
        ToggleFilter = findViewById(R.id.btnShowOptionsFavorites);      // תוקן מ-Cart ל-Favorites

        optionOne = findViewById(R.id.option1);
        optionTwo = findViewById(R.id.option2);
        optionThree = findViewById(R.id.option3);
        optionFore = findViewById(R.id.option4);

        // הגדרת מאזינים
        if (ToggleFilter != null) ToggleFilter.setOnClickListener(this);
        if (optionOne != null) optionOne.setOnClickListener(this);
        if (optionTwo != null) optionTwo.setOnClickListener(this);
        if (optionThree != null) optionThree.setOnClickListener(this);
        if (optionFore != null) optionFore.setOnClickListener(this);

        // הגדרת RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rcFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoritesAdapter = new FavoritesAdapter(this, allItems, new FavoritesAdapter.FavoritesClickListener() {
            @Override
            public void onClick(Item item) {
                Intent intent = new Intent(Favorites.this, Item_page.class);
                intent.putExtra("Item_UID", item.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Item item, int position) {
                new MaterialAlertDialogBuilder(Favorites.this)
                        .setTitle("הסרה מהמועדפים")
                        .setMessage("האם אתה בטוח שברצונך להסיר את " + item.getpName() + "?")
                        .setPositiveButton("כן, הסר", (dialog, which) -> {
                            allItems.remove(position);
                            favoritesAdapter.setFavoriteItems(allItems);
                            updateFavoritesInFirebase(); // עדכון מועדפים ולא סל
                        })
                        .setNegativeButton("ביטול", null)
                        .show();
            }
        });

        recyclerView.setAdapter(favoritesAdapter);
        fetchFavoritesFromFirebase(); // קריאה למועדפים
    }

    private void fetchFavoritesFromFirebase() {
        // שימוש ב-getInstance() ובווידוא שה-Callback תואם למודל
        DatabaseService.getInstance().getFavorites(current_userId, new DatabaseService.DatabaseCallback<FavoriteList>() {
            @Override
            public void onCompleted(FavoriteList favorites) {
                if (favorites != null && favorites.getFavoriteItemsList() != null) {
                    allItems = favorites.getFavoriteItemsList();
                } else {
                    allItems = new ArrayList<>();
                }
                filteredItems = new ArrayList<>(allItems);
                favoritesAdapter.setFavoriteItems(allItems);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load favorites", e);
                allItems = new ArrayList<>();
                favoritesAdapter.setFavoriteItems(allItems);
            }
        });
    }


    private void updateFavoritesInFirebase() {
        databaseService.updateFavorites(allItems, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void unused) {
                Toast.makeText(Favorites.this, "המועדפים עודכנו", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Favorites.this, "עדכון נכשל", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnShowOptionsFavorites) {
            if (optionsContainer.getVisibility() == View.GONE) {
                optionsContainer.setVisibility(View.VISIBLE);
            } else {
                optionsContainer.setVisibility(View.GONE);
            }
        } else if (id == R.id.option1 || id == R.id.option2 || id == R.id.option3 || id == R.id.option4) {
            // לוגיקת המיון שלך...
            if (id == R.id.option1) selectedCategory = "without";
            else if (id == R.id.option2) selectedCategory = "high";
            else if (id == R.id.option3) selectedCategory = "low";
            else if (id == R.id.option4) selectedCategory = "rate";

            filterItemsBySorting();
            optionsContainer.setVisibility(View.GONE);
        }
    }

    private void filterItemsBySorting() {
        filteredItems.clear();
        filteredItems.addAll(allItems);
        if ("high".equals(selectedCategory)) {
            filteredItems.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
        } else if ("low".equals(selectedCategory)) {
            filteredItems.sort(Comparator.comparingDouble(Item::getPrice));
        } else if ("rate".equals(selectedCategory)) {
            filteredItems.sort((a, b) -> Double.compare(b.getRate(), a.getRate()));
        }
        favoritesAdapter.setFavoriteItems(filteredItems);
    }
}
