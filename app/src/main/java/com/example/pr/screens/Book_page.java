package com.example.pr.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class Book_page extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private ItemsAdapter itemsAdapter;
    private TextView tvTitle;
    private ImageView ivTitleIteams;
    private String selectedCategory;
    private List<Item> allItems;

    // טקסט החיפוש הנוכחי
    private String currentSearchQuery = "";

    // אופציית המיון הנוכחית (null = ללא מיון)
    private String currentSortOption = null;

    // אלמנטי המיון
    private LinearLayout optionsContainer;
    private View toggleFilter;

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

        selectedCategory = getIntent().getStringExtra("type");

        databaseService = DatabaseService.getInstance();

        RecyclerView recyclerView = findViewById(R.id.rcItemes);
        tvTitle = findViewById(R.id.tvTitleIteams);
        ivTitleIteams = findViewById(R.id.ivTitleIteams);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemsAdapter = new ItemsAdapter(new ItemsAdapter.ItemClickListener() {
            @Override
            public void onLongClick(Item item, int position) {
            }

            @Override
            public void onClick(Item item) {
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(Book_page.this, Item_page.class);
                intent.putExtra("Item_UID", item.getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(itemsAdapter);

        setupSearch();
        setupSortFilter();

        fetchItemsFromFirebase();
    }

    // הגדרת לוגיקת החיפוש — Enter + לחיצה על זכוכית מגדלת
    private void setupSearch() {
        EditText edSearch = findViewById(R.id.edSearch);

        edSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                currentSearchQuery = edSearch.getText().toString().trim();
                applyFilters();
                return true;
            }
            return false;
        });

        edSearch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (edSearch.getCompoundDrawables()[2] != null) {
                    int drawableWidth = edSearch.getCompoundDrawables()[2].getBounds().width();
                    int touchX = (int) event.getX();
                    if (touchX >= edSearch.getWidth() - drawableWidth - edSearch.getPaddingEnd() - 8) {
                        currentSearchQuery = edSearch.getText().toString().trim();
                        applyFilters();
                        return true;
                    }
                }
            }
            return false;
        });
    }

    // הגדרת כפתור המיון ואופציות
    private void setupSortFilter() {
        toggleFilter = findViewById(R.id.btnShowOptionsBook);
        optionsContainer = findViewById(R.id.optionsContainerBook);

        TextView option1 = findViewById(R.id.optionBook1);
        TextView option2 = findViewById(R.id.optionBook2);
        TextView option3 = findViewById(R.id.optionBook3);
        TextView option4 = findViewById(R.id.optionBook4);

        toggleFilter.setOnClickListener(this);
        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnShowOptionsBook) {
            if (optionsContainer.getVisibility() == View.GONE) {
                optionsContainer.setVisibility(View.VISIBLE);
                optionsContainer.setAlpha(0f);
                optionsContainer.animate().alpha(1f).setDuration(300);
            } else {
                optionsContainer.setVisibility(View.GONE);
            }
        } else if (id == R.id.optionBook1) {
            currentSortOption = "without";
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        } else if (id == R.id.optionBook2) {
            currentSortOption = "high";
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        } else if (id == R.id.optionBook3) {
            currentSortOption = "low";
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        } else if (id == R.id.optionBook4) {
            currentSortOption = "rate";
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        }
    }

    private void fetchItemsFromFirebase() {
        databaseService.getItemList(new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<Item> items) {
                allItems = items;
                updateTitleAndIcon();
                applyFilters();
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

    // פונקציה מרכזית — שלושת הפילטרים פועלים יחד בסדר הנכון
    private void applyFilters() {
        if (allItems == null) return;

        List<Item> result = new ArrayList<>();

        // שלב 1: סינון לפי קטגוריה
        for (Item item : allItems) {
            boolean matchesCategory = (selectedCategory == null || selectedCategory.isEmpty())
                    || item.getType().equals(selectedCategory);

            // שלב 2: סינון לפי טקסט חיפוש
            boolean matchesSearch = currentSearchQuery.isEmpty()
                    || item.getpName().toLowerCase().contains(currentSearchQuery.toLowerCase());

            if (matchesCategory && matchesSearch) {
                result.add(item);
            }
        }

        // שלב 3: מיון
        if (currentSortOption != null) {
            switch (currentSortOption) {
                case "high":
                    result.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                    break;
                case "low":
                    result.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                    break;
                case "rate":
                    result.sort((a, b) -> Double.compare(b.getRate(), a.getRate()));
                    break;
                // "without" — ללא מיון
            }
        }

        itemsAdapter.setItem(result);
    }

    private void updateTitleAndIcon() {
        if (selectedCategory == null || selectedCategory.isEmpty()) return;
        switch (selectedCategory) {
            case "book":
                ivTitleIteams.setImageResource(R.drawable.icon_books_page);
                tvTitle.setText("Books store");
                break;
            case "toy":
                ivTitleIteams.setImageResource(R.drawable.icon_toys_page);
                tvTitle.setText("Toys store");
                break;
            case "device":
                ivTitleIteams.setImageResource(R.drawable.icon_devices_page);
                tvTitle.setText("Devices store");
                break;
            case "shoe":
                ivTitleIteams.setImageResource(R.drawable.icon_shoe_shop_page);
                tvTitle.setText("Shoes store");
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.user_menu, menu);

        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if(  item.getItemId()==R.id.menu_user) {
            Intent go = new Intent(Book_page.this, UserProfile.class);
            startActivity(go);
            return true;
        }

        if(  item.getItemId()==R.id.menu_history) {
            Intent go = new Intent(Book_page.this, OrderHistory.class);
            startActivity(go);
            return true;
        }
        if(  item.getItemId()==R.id.menu_favorites) {
            Intent go = new Intent(Book_page.this, Favorites.class);
            startActivity(go);
            return true;
        }

        if(  item.getItemId()==R.id.menu_cart) {
            Intent go = new Intent(Book_page.this, CartList.class);
            startActivity(go);
            return true;
        }

        if(  item.getItemId()==R.id.menu_logout) {
            Intent go = new Intent(Book_page.this, LogIn.class);
            startActivity(go);
            return true;
        }

        if( item.getItemId()==R.id.menu_admin) {
            if(LogIn.isAdmin) {
                Intent go = new Intent(Book_page.this, AdminPage.class);
                startActivity(go);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}