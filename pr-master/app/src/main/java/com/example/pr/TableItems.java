package com.example.pr;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.Locale;
public class TableItems extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private ItemsAdapter itemsAdapter;
    ScrollView scrollViewFilter1, scrollViewFilter2;
    EditText edSearchItem;
    private TextView optionFore, optionFive, optionSix, optionSeven, optionEight, optionNine, optionTen, optionEleven, optionTwelve;
   private View ToggleFilter1, ToggleFilter2;
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

        // edSearchItem.addTextChangedListener(new TextWatcher() { //ברגע שמקלידים החיפוש מתבצע
        //@Override
        //    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //        filterItemsByCategory(); // קורא לסינון בכל שינוי טקסט
       //     }
       //     @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
       //     @Override public void afterTextChanged(Editable s) {}
       // });

        databaseService = DatabaseService.getInstance();
        RecyclerView recyclerView = findViewById(R.id.RcItemes);
        edSearchItem = findViewById(R.id.edSearchItem);

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

    private void filterItemsBySorting() {
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

    private void filterItemsByCategory() {
        if (allItems == null) return;

        if (filteredItems == null) {
            filteredItems = new ArrayList<>();
        }

        // שלב 1: סינון לפי קטגוריה
        if ("all".equals(selectedCategory2) || selectedCategory2 == null || selectedCategory2.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems.clear();
            for (Item item : allItems) {
                if (item.getType() != null && item.getType().equals(selectedCategory2)) {
                    filteredItems.add(item);
                }
            }
        }

        // שלב 2: סינון לפי טקסט החיפוש (רק אם המשתמש הקליד משהו)
        if (edSearchItem != null) {
            String searchText = edSearchItem.getText().toString().trim().toLowerCase();

            if (!searchText.isEmpty()) { // מסננים רק אם יש טקסט
                ArrayList<Item> arrayTemp = new ArrayList<>();
                // עוברים רק על מה שעבר את סינון הקטגוריה
                for (Item item : filteredItems) {
                    if (item.getpName() != null && item.getpName().toLowerCase().contains(searchText)) {
                        arrayTemp.add(item);
                    }
                }
                filteredItems = arrayTemp; // מעדכנים את הרשימה המסוננת
            }
        }
        // שלב 3: מיון סופי והצגה
        filterItemsBySorting();
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
            filterItemsByCategory();
            optionsContainer1.setVisibility(View.GONE);
        } else if (id == R.id.option5) {
            selectedCategory1 = "high";
            filterItemsByCategory();
            optionsContainer1.setVisibility(View.GONE);
        } else if (id == R.id.option6) {
            selectedCategory1 = "low";
            filterItemsByCategory();
            optionsContainer1.setVisibility(View.GONE);
        } else if (id == R.id.option7) {
            selectedCategory1 = "rate";
            filterItemsByCategory();
            optionsContainer1.setVisibility(View.GONE);
        }

        // טיפול בתפריט 2 (קטגוריות)
        else if (id == R.id.btnShowOptionsItem2) {
            optionsContainer1.setVisibility(View.GONE);
            optionsContainer2.setVisibility(optionsContainer2.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        } else if (id == R.id.option8) {
            selectedCategory2 = "all";
            filterItemsByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option9) {
            selectedCategory2 = "book"; // שנה לשם הקטגוריה האמיתי שלך
            filterItemsByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option10) {
            selectedCategory2 = "toy"; // שנה לשם הקטגוריה האמיתי שלך
            filterItemsByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option11) {
            selectedCategory2 = "device"; // שנה לשם הקטגוריה האמיתי שלך
            filterItemsByCategory();
            optionsContainer2.setVisibility(View.GONE);
        } else if (id == R.id.option12) {
            selectedCategory2 = "shoe"; // שנה לשם הקטגוריה האמיתי שלך
            filterItemsByCategory();
            optionsContainer2.setVisibility(View.GONE);
        }
    }

    public void serchClickItem(View view) {
        filterItemsByCategory();
    }
}
