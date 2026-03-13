package com.example.pr;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TableItems extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private ItemsAdapter itemsAdapter;
    ScrollView scrollViewFilter1, scrollViewFilter2;
    TextView optionFore, optionFive, optionSix, optionSeven,optionEight, optionNine, optionTen, optionEleven, optionTwelve;
    View ToggleFilter1, ToggleFilter2;
    private LinearLayout optionsContainer1, optionsContainer2;
    private String selectedCategory1="without", selectedCategory2="all"; // משתנה לאחסון הקטגוריה שנבחרה
    private List<Item> allItems;
    ArrayList<Item> filteredItems;


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

        scrollViewFilter1= findViewById(R.id.ScrollViewFilterItem1);
        scrollViewFilter2= findViewById(R.id.ScrollViewFilterItem2);

        scrollViewFilter1.setSmoothScrollingEnabled(true);
        scrollViewFilter2.setSmoothScrollingEnabled(true);

        optionsContainer1 = findViewById(R.id.optionsContainerItem1);
        optionsContainer2 = findViewById(R.id.optionsContainerItem2);

        ToggleFilter1 = findViewById(R.id.btnShowOptionsItem1); // הכפתור הראשי שפותח
        ToggleFilter2 = findViewById(R.id.btnShowOptionsItem2); // הכפתור הראשי שפותח

        optionFore = findViewById(R.id.option4);
        optionFive = findViewById(R.id.option5);
        optionSix = findViewById(R.id.option6);
        optionSeven = findViewById(R.id.option7);
        optionEight = findViewById(R.id.option8);
        optionNine = findViewById(R.id.option9);
        optionTen = findViewById(R.id.option10);
        optionEleven = findViewById(R.id.option11);
        optionTwelve = findViewById(R.id.option12);

        // 2. הגדרת מאזינים (כולם מפנים ל-onClick שנמצא למטה)
        ToggleFilter1.setOnClickListener(this);
        ToggleFilter2.setOnClickListener(this);
        optionFore.setOnClickListener(this);
        optionFive.setOnClickListener(this);
        optionSix.setOnClickListener(this);
        optionSeven.setOnClickListener(this);
        optionEight.setOnClickListener(this);
        optionNine.setOnClickListener(this);
        optionTen.setOnClickListener(this);
        optionEleven.setOnClickListener(this);
        optionTwelve.setOnClickListener(this);

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
        databaseService.getItemList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<Item> items) {
                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " + items.size());
                allItems = items;
                filteredItems = new ArrayList<>(allItems);
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
    private void filterUsersBySorting() {
        if (selectedCategory1 != null && !selectedCategory1.isEmpty()) {
            if (selectedCategory1.contains("high")) {
                // מיון מהגבוה לנמוך
                filteredItems.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            } else if (selectedCategory1.contains("low")) {
                // מיון מהנמוך לגבוה
                filteredItems.sort(Comparator.comparingDouble(Item::getPrice));
            }else if (selectedCategory1.contains("rate")) {
                // מיון מהנמוך לגבוה
                filteredItems.sort((a, b) -> Double.compare(b.getRate(), a.getRate()));
            }else{
                fetchItemsFromFirebase();
            }
        }
        // אם לא נבחרה קטגוריה, filteredItems פשוט נשארת העתק של allItems
        itemsAdapter.setItem(filteredItems);
        itemsAdapter.notifyDataSetChanged(); // חשוב לעדכן את האדפטר
    }

    private void filterUsersByCategory() {
        if (selectedCategory2 != null && !selectedCategory2.isEmpty() && !selectedCategory2.equals("all")) {
            filteredItems.clear();
            for (Item item : allItems) {
                if (item.getType().equals(selectedCategory2)) {
                    filteredItems.add(item);
                }
            }
        }
        else if(selectedCategory2.equals("all")) {
            filteredItems = new ArrayList<>(allItems);
        }
        // אם לא נבחרה קטגוריה, filteredItems פשוט נשארת העתק של allItems

        itemsAdapter.setItem(filteredItems);
        itemsAdapter.notifyDataSetChanged(); // חשוב לעדכן את האדפטר
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnShowOptionsItem1) {
            // פתיחה או סגירה של התפריט
            if (optionsContainer1.getVisibility() == View.GONE) {
                optionsContainer1.setVisibility(View.VISIBLE);
                optionsContainer1.bringToFront();
                optionsContainer1.setAlpha(0f);
                optionsContainer1.animate().alpha(1f).setDuration(300);
            } else {
                optionsContainer1.setVisibility(View.GONE);
            }
        }
        else if (id == R.id.option4) {
            // לחיצה על "without"
            selectedCategory1 = "without";
            filterUsersBySorting();
            optionsContainer1.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
        else if (id == R.id.option5) {
            // לחיצה על "high to low"
            selectedCategory1 = "high";
            filterUsersBySorting();
            optionsContainer1.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
        else if (id == R.id.option6) {
            // לחיצה על "low to high"
            selectedCategory1 = "low";
            filterUsersBySorting();
            optionsContainer1.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
        else if (id == R.id.option7) {
            // לחיצה על "rate to high"
            selectedCategory1 = "rate";
            filterUsersBySorting();
            optionsContainer1.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }

        if (id == R.id.btnShowOptionsItem2) {
            // פתיחה או סגירה של התפריט
            if (optionsContainer2.getVisibility() == View.GONE) {
                optionsContainer2.setVisibility(View.VISIBLE);
                optionsContainer2.bringToFront();
                optionsContainer2.setAlpha(0f);
                optionsContainer2.animate().alpha(1f).setDuration(300);
            } else {
                optionsContainer2.setVisibility(View.GONE);
            }
        } else if (id == R.id.option8) {
            // לחיצה על "all"
            selectedCategory2 = "all";
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        } else if (id == R.id.option9) {
            // לחיצה על "books"
            selectedCategory2 = "book";
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        } else if (id == R.id.option10) {
            // לחיצה על "toys"
            selectedCategory2 = "toy";
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        } else if (id == R.id.option11) {
            // לחיצה על "devices"
            selectedCategory2 = "device";
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        } else if (id == R.id.option12) {
            // לחיצה על "shoes"
            selectedCategory2 = "shoe";
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
    }
}