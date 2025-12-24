package com.example.pr;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.adapers.ItemsAdapter2;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class Book_page extends AppCompatActivity {


    private static final String TAG = "ItemsActivity";

    private RecyclerView recyclerView;
    private ItemsAdapter2 itemsAdapter2;

    private ArrayList<Item> allItems = new ArrayList<>();

    DatabaseService databaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        recyclerView = findViewById(R.id.rcItemes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        itemsAdapter2 = new ItemsAdapter2(allItems);

        recyclerView.setAdapter(itemsAdapter2);

        fetchItemsFromFirebase();

    }

    private void fetchItemsFromFirebase() {

        // טעינת המוצרים
        databaseService.getItemList(new DatabaseService.DatabaseCallback<List<Item>>() {
            @Override
            public void onCompleted(List<Item> items) {
                // Log.d(TAG, "onCompleted: " + items);
                allItems.clear();
                allItems.addAll(items);
                //    filterItemsByCategory();
                //קריאה לסינון המוצרים אחרי טעינת המוצרים מחדש

                Log.d(TAG, "onCompleted: " + allItems);


                itemsAdapter2.notifyDataSetChanged();

                // עדכון התצוגה על פי חיפוש
                // String query = ((SearchView) findViewById(R.id.searchView)).getQuery().toString();
                // itemsAdapter.filter(query);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load items: ", e);
                new android.app.AlertDialog.Builder(Book_page.this)
                        .setMessage("נראה שקרתה תקלה בטעינת המוצרים, נסה שוב מאוחר יותר")
                        .setPositiveButton("אוקי", null)
                        .show();
            }
        });
    }

}