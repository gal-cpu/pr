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
    TextView optionFore, optionFive, optionSix, optionSeven, optionEight, optionNine, optionTen, optionEleven, optionTwelve;
    View ToggleFilter1, ToggleFilter2;
    private LinearLayout optionsContainer1, optionsContainer2;
    private String selectedCategory1 = "without", selectedCategory2 = "all";
    private List<Item> allItems = new ArrayList<>();
    private ArrayList<Item> filteredItems = new ArrayList<>();

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

        scrollViewFilter1 = findViewById(R.id.ScrollViewFilterItem1);
        scrollViewFilter2 = findViewById(R.id.ScrollViewFilterItem2);

        optionsContainer1 = findViewById(R.id.optionsContainerItem1);
        optionsContainer2 = findViewById(R.id.optionsContainerItem2);

        ToggleFilter1 = findViewById(R.id.btnShowOptionsItem1);
        ToggleFilter2 = findViewById(R.id.btnShowOptionsItem2);

        optionFore = findViewById(R.id.option4);
        optionFive = findViewById(R.id.option5);
        optionSix = findViewById(R.id.option6);
        optionSeven = findViewById(R.id.option7);
        optionEight = findViewById(R.id.option8);
        optionNine = findViewById(R.id.option9);
        optionTen = findViewById(R.id.option10);
        optionEleven = findViewById(R.id.option11);
        optionTwelve = findViewById(R.id.option12);

        // הגדרת מאזינים
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
            public void onLongClick(Item item, int position) {}

            @Override
            public void onClick(Item item) {
                Intent intent = new Intent(TableItems.this, UpdateItem.class);
                intent.putExtra("Item_UID", item.getId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(itemsAdapter);
        fetchItemsFromFirebase();
    }

    private void fetchItemsFromFirebase() {
        databaseService.getItemList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<Item> items) {
                if (items != null) {
                    allItems = items;
                    filteredItems = new ArrayList<>(allItems);
                    itemsAdapter.setItem(filteredItems);
                    itemsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load items", e);
            }
        });
    }

    private void filterUsersBySorting() {
        if (filteredItems == null) return;

        if ("high".equals(selectedCategory1)) {
            filteredItems.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
        } else if ("low".equals(selectedCategory1)) {
            filteredItems.sort(Comparator.comparingDouble(Item::getPrice));
        } else if ("rate".equals(selectedCategory1)) {
            filteredItems.sort((a, b) -> Double.compare(b.getRate(), a.getRate()));
        }

        itemsAdapter.setItem(filteredItems);
        itemsAdapter.notifyDataSetChanged();
    }

    private void filterUsersByCategory() {
        if (allItems == null) return;

        if ("all".equals(selectedCategory2)) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems.clear();
            for (Item item : allItems) {
                if (item.getType() != null && item.getType().equals(selectedCategory2)) {
                    filteredItems.add(item);
                }
            }
        }
        filterUsersBySorting();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // טיפול בתפריט 1 (מיון)
        if (id == R.id.btnShowOptionsItem1) {
            optionsContainer2.setVisibility(View.GONE);
            optionsContainer1.setVisibility(optionsContainer1.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        } else if (id == R.id.option4) {
            selectedCategory1 = "without";
            filterUsersByCategory();
            optionsContainer1.setVisibility(View.GONE);
        } else if (id == R.id.option5) {
            selectedCategory1 = "high";
            filterUsersByCategory();
            optionsContainer1.setVisibility(View.GONE);
        } else if (id == R.id.option6) {
            selectedCategory1 = "low";
            filterUsersByCategory();
            optionsContainer1.setVisibility(View.GONE);
        } else if (id == R.id.option7) {
            selectedCategory1 = "rate";
            filterUsersByCategory();
            optionsContainer1.setVisibility(View.GONE);
        }

        // טיפול בתפריט 2 (קטגוריות)
        else if (id == R.id.btnShowOptionsItem2) {
            optionsContainer1.setVisibility(View.GONE);
            optionsContainer2.setVisibility(optionsContainer2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        } else if (id == R.id.option8) {
            selectedCategory2 = "all";
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option9) {
            selectedCategory2 = "book"; // שנה לשם הקטגוריה האמיתי שלך
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option10) {
            selectedCategory2 = "toy"; // שנה לשם הקטגוריה האמיתי שלך
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option11) {
            selectedCategory2 = "device"; // שנה לשם הקטגוריה האמיתי שלך
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option12) {
            selectedCategory2 = "shoes"; // שנה לשם הקטגוריה האמיתי שלך
            filterUsersByCategory();
            optionsContainer2.setVisibility(View.GONE);
        }

    }
}
