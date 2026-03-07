package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.adapers.UsersAdapter;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableItems extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private ItemsAdapter itemsAdapter;
    ScrollView scrollViewFilter;
    int locationM,locationL;
    double max, min;
    Item maxItem, minItem;
    TextView optionFore, optionFive, optionSix;
    View ToggleFilter;
    private LinearLayout optionsContainer;
    private String selectedCategory; // משתנה לאחסון הקטגוריה שנבחרה
    private List<Item> allItems;

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

        scrollViewFilter= findViewById(R.id.ScrollViewFilterItem1);

        scrollViewFilter.setSmoothScrollingEnabled(true);
        optionsContainer = findViewById(R.id.optionsContainerItem); // וודא שיש ID כזה ב-XML
        ToggleFilter = findViewById(R.id.btnShowOptionsItem); // הכפתור הראשי שפותח
        optionFore = findViewById(R.id.option4);
        optionFive = findViewById(R.id.option5);
        optionSix = findViewById(R.id.option6);

        // 2. הגדרת מאזינים (כולם מפנים ל-onClick שנמצא למטה)
        ToggleFilter.setOnClickListener(this);
        optionFore.setOnClickListener(this);
        optionFive.setOnClickListener(this);
        optionSix.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        itemsAdapter = new ItemsAdapter(new ItemsAdapter.ItemClickListener() {
            @Override
            public void onLongClick(Item item, int position) {

            }

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
                allItems = items;
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
    private void filterUsersByCategory() {
        // יצירת עותק של הרשימה המקורית כדי לא לפגוע בנתונים המקוריים
        ArrayList<Item> filteredItems = new ArrayList<>(allItems);

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            if (selectedCategory.contains("high")) {
                // מיון מהגבוה לנמוך
                Collections.sort(filteredItems, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            } else if (selectedCategory.contains("low")) {
                // מיון מהנמוך לגבוה
                Collections.sort(filteredItems, (a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            }else{
                fetchItemsFromFirebase();
            }
        }
        // אם לא נבחרה קטגוריה, filteredItems פשוט נשארת העתק של allItems

        itemsAdapter.setItem(filteredItems);
        itemsAdapter.notifyDataSetChanged(); // חשוב לעדכן את האדפטר
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnShowOptionsItem) {
            // פתיחה או סגירה של התפריט
            if (optionsContainer.getVisibility() == View.GONE) {
                optionsContainer.setVisibility(View.VISIBLE);
                optionsContainer.setAlpha(0f);
                optionsContainer.animate().alpha(1f).setDuration(300);
            } else {
                optionsContainer.setVisibility(View.GONE);
            }
        }
        else if (id == R.id.option4) {
            // לחיצה על "without"
            selectedCategory = "without";
            filterUsersByCategory();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
        else if (id == R.id.option5) {
            // לחיצה על "high to low"
            selectedCategory = "high";
            filterUsersByCategory();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
        else if (id == R.id.option6) {
            // לחיצה על "low to high"
            selectedCategory = "low";
            filterUsersByCategory();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
    }
}