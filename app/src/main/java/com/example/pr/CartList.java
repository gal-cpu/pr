package com.example.pr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.adapers.UsersAdapter;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class CartList extends AppCompatActivity {

    private static final String TAG = "ItemsActivity";
    private ItemsAdapter itemsAdapter;
    DatabaseService databaseService;
    private String selectedCategory; // משתנה לאחסון הקטגוריה שנבחרה
    private List<Item> allIitems;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        RecyclerView recyclerView = findViewById(R.id.RcCart);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        itemsAdapter = new ItemsAdapter(new ItemsAdapter.ItemClickListener() {
            @Override
            public void onClick(Item item) {
                // Handle item click
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(CartList.this, UpdateItem.class);
                intent.putExtra("Item_UID", item.getId());
                startActivity(intent);
            }

        });
        recyclerView.setAdapter(itemsAdapter);

        recyclerView.setAdapter(itemsAdapter);

        fetchItemsFromFirebase();
    }

    private void fetchItemsFromFirebase() {

        // טעינת המוצרים
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                // Log.d(TAG, "onCompleted: " + items);
                allIitems = items;
                itemsAdapter.setItem(items);
                filterItemsByCategory();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load items: ", e);
                new android.app.AlertDialog.Builder(CartList.this)
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
            for (Item item : allIitems) {
                if (item.getId().equals(selectedCategory)) {
                    filteredItems.add(item);
                }
            }
        }
        itemsAdapter.setItem(filteredItems);
    }
}