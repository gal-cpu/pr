package com.example.pr;

import android.content.Intent;
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

import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.adapers.UsersAdapter;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;

import java.util.List;

public class TableItems extends AppCompatActivity {

    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private ItemsAdapter itemsAdapter;
    private UsersAdapter adapter;
    private String selectedCategory; // משתנה לאחסון הקטגוריה שנבחרה
    private SearchView searchView;
    private List<Item> allIitems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_table_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        databaseService = DatabaseService.getInstance();

        RecyclerView recyclerView = findViewById(R.id.RcItemes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        itemsAdapter = new ItemsAdapter(new ItemsAdapter.ItemClickListener() {
            @Override
            public void onClick(Item item) {
                // Handle item click
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(TableItems.this, UpdateItem.class);
                intent.putExtra("Item_UID", item.getId());
                startActivity(intent);
            }

        });

        recyclerView.setAdapter(itemsAdapter);

        fetchItemsFromFirebase();
    }

    private void fetchItemsFromFirebase() {

        // טעינת המוצרים
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " + items.size());
                allIitems = items;
                itemsAdapter.setItem(items);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load items: ", e);
                new android.app.AlertDialog.Builder(TableItems.this)
                        .setMessage("נראה שקרתה תקלה בטעינת המוצרים, נסה שוב מאוחר יותר")
                        .setPositiveButton("אוקי", null)
                        .show();
            }
        });
    }

}