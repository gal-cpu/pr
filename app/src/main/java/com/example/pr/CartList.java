package com.example.pr;

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

import com.example.pr.adapers.CartAdapter; // שינוי למתאם העגלה
import com.example.pr.model.Cart;
import com.example.pr.model.Item;
import com.example.pr.model.ItemCart;
import com.example.pr.services.DatabaseService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartList extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "cart";
    private DatabaseService databaseService;
    ScrollView scrollViewFilter;
    TextView optionOne, optionTwo, optionThree, optionFore;
    View ToggleFilter;
    private LinearLayout optionsContainer;
    private TextView tvPay;

    Cart cart=null;
    private CartAdapter cartAdapter; // שימוש ב-CartAdapter
    private List<ItemCart> allItems = new ArrayList<>();
    private ArrayList<ItemCart> filteredItems = new ArrayList<>();
    private String selectedCategory, current_userId = "";

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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();



        scrollViewFilter= findViewById(R.id.ScrollViewFilterCart);

        scrollViewFilter.setSmoothScrollingEnabled(true);
        optionsContainer = findViewById(R.id.optionsContainerCart); // וודא שיש ID כזה ב-XML
        ToggleFilter = findViewById(R.id.btnShowOptionsCart); // הכפתור הראשי שפותח
        optionOne = findViewById(R.id.option1);
        optionTwo = findViewById(R.id.option2);
        optionThree=findViewById(R.id.option3);
        optionFore=findViewById(R.id.option4);

        // 2. הגדרת מאזינים (כולם מפנים ל-onClick שנמצא למטה)
        ToggleFilter.setOnClickListener(this);
        optionOne.setOnClickListener(this);
        optionTwo.setOnClickListener(this);
        optionThree.setOnClickListener(this);
        optionFore.setOnClickListener(this);

        if (mAuth.getCurrentUser() != null) {
            current_userId = mAuth.getCurrentUser().getUid();
        }

        fetchCartFromFirebase() ;


        RecyclerView recyclerView = findViewById(R.id.RcCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvPay = findViewById(R.id.tvPay);

        // אתחול ה-CartAdapter עם ה-Listener המתאים
        cartAdapter = new CartAdapter(allItems, new CartAdapter.CartClickListener() {
            @Override
            public void onClick(ItemCart item) {
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(CartList.this, Item_page.class);
                intent.putExtra("Item_UID", item.getItem().getId());
                startActivity(intent);

            }

            @Override
            public void onLongClick(ItemCart item, int position) {
                new MaterialAlertDialogBuilder(CartList.this)
                        .setTitle("הסרה מהעגלה")
                        .setMessage("האם אתה בטוח שברצונך להסיר את " + item.getItem().getpName() + " מהסל?")
                        .setBackground(getResources().getDrawable(R.drawable.dialog_rounded_bg, getTheme()))
                        .setIcon(R.drawable.baseline_shopping_cart_24)
                        .setPositiveButton("כן, הסר", (dialog, which) -> {
                            if (allItems != null && position < allItems.size()) {
                                // 1. הסרה מהרשימה המקומית
                                allItems.remove(position);

                                // 2. עדכון ה-Adapter
                                cartAdapter.notifyDataSetChanged();
                                // 3. עדכון המחיר הכולל
                                sumPrice();
                                // 4. עדכון Firebase
                                updateCartInFirebase();
                            }
                        })
                        .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        recyclerView.setAdapter(cartAdapter);
        fetchCartFromFirebase();
    }

    private void updateCartInFirebase() {

        databaseService.updateCart(current_userId, new Cart(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

            }

            @Override
            public void onFailed(Exception e) {

            }
        });



    }

    private void fetchCartFromFirebase() {
        databaseService.getCart(current_userId, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Cart cart2) {
                if (cart2 != null && cart2.getItemArrayList() != null) {
                    allItems = cart2.getItemArrayList();

                    cart=cart2;
                } else {
                    allItems = new ArrayList<>();
                    cart=new Cart();
                }
                filteredItems = new ArrayList<>(allItems);
                cartAdapter.setItem(allItems);
                sumPrice();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load cart", e);
                allItems = new ArrayList<>();
                cart=new Cart();
                cartAdapter.setItem(allItems);
                sumPrice();
            }
        });
    }

    private void filterItemsBySorting() {
        if (filteredItems == null) return;
        filteredItems.clear();
        filteredItems.addAll(cart.getItemArrayList());
        if ("without".equals(selectedCategory)) {

        }else if ("high".equals(selectedCategory)) {
            filteredItems.sort((a, b) -> Double.compare(b.getItem().getPrice(), a.getItem().getPrice()));
        } else if ("low".equals(selectedCategory)) {
            filteredItems.sort((a, b) -> Double.compare(a.getItem().getPrice(), b.getItem().getPrice()));
        } else if ("rate".equals(selectedCategory)) {
            filteredItems.sort((a, b) -> Double.compare(b.getItem().getRate(), a.getItem().getRate()));
        }

        cartAdapter.setItem(filteredItems);
        cartAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // טיפול בתפריט (מיון)
        if (id == R.id.btnShowOptionsCart) {
            // פתיחה או סגירה של התפריט
            if (optionsContainer.getVisibility() == View.GONE) {
                optionsContainer.setVisibility(View.VISIBLE);
                optionsContainer.setAlpha(0f);
                optionsContainer.animate().alpha(1f).setDuration(300);
            } else {
                optionsContainer.setVisibility(View.GONE);
            }
        }else if (id == R.id.option1) {
            selectedCategory = "without";
            filterItemsBySorting();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }else if (id == R.id.option2) {
            selectedCategory = "high";
            filterItemsBySorting();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }else if (id == R.id.option3) {
            selectedCategory = "low";
            filterItemsBySorting();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }else if (id == R.id.option4) {
            selectedCategory = "rate";
            filterItemsBySorting();
            optionsContainer.setVisibility(View.GONE); // סגירת התפריט אחרי בחירה
        }
    }
    private void sumPrice() {
        double sum = 0.0;
        // 1. בדיקה שכל אובייקט העגלה (cart) אינו null
        if (cart != null && cart.getItemArrayList() != null && !cart.getItemArrayList().isEmpty()) {
            for (ItemCart item : cart.getItemArrayList()) {
                // 2. בדיקה שה-ItemCart קיים וגם שהמוצר (Item) שבתוכו קיים
                if (item != null && item.getItem() != null) {
                    sum += (item.getItem().getPrice() * item.getAmount());
                }
            }
        }
        tvPay.setText("Total sum is: " + String.format("%.2f", sum) + "$");
    }
}