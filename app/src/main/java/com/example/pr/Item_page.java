package com.example.pr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class Item_page extends AppCompatActivity {
    private ItemsAdapter itemsAdapter;
    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private List<Item> allItems;
    private String selectedCategory;
    TextView tvName, tvNote, tvPrice, tvRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedCategory = getIntent().getStringExtra("Item_UID");

        databaseService = DatabaseService.getInstance();
        tvName = findViewById(R.id.tvNameItem);
        tvNote = findViewById(R.id.tvNoteItem);
        tvPrice = findViewById(R.id.tvPriceItem);
        tvRate = findViewById(R.id.tvRateItem);

        fetchItemsFromFirebase();
    }

    private void fetchItemsFromFirebase() {

        // טעינת המוצרים
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                // Log.d(TAG, "onCompleted: " + items);
                allItems = items;
                itemsAdapter.setItem(items);
                filterItemsByCategory();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load items: ", e);
                new android.app.AlertDialog.Builder(Item_page.this)
                        .setMessage("נראה שקרתה תקלה בטעינת המוצרים, נסה שוב מאוחר יותר")
                        .setPositiveButton("אוקי", null)
                        .show();
            }
        });
    }

    private void filterItemsByCategory() {
        ArrayList<Item> filteredItems = new ArrayList<>();
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            // אם נבחרה קטגוריה מסוימת, נבצע סינון
            for (Item item : allItems) {
                if (item.getId().equals(selectedCategory)) {
                    filteredItems.add(item);
                }
            }
            itemsAdapter.setItem(filteredItems);
        }
    }
}