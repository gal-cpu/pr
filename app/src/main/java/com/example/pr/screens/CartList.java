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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pr.R;
import com.example.pr.adapers.CartAdapter;
import com.example.pr.model.Cart;
import com.example.pr.model.ItemCart;
import com.example.pr.model.Order;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class CartList extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "cart";
    private DatabaseService databaseService;
    TextView optionOne, optionTwo, optionThree, optionFore;
    View ToggleFilter;
    private LinearLayout optionsContainer;
    private TextView tvPay;
    double sum = 0.0;
    Cart cart = null;
    private CartAdapter cartAdapter;
    private List<ItemCart> allItems = new ArrayList<>();

    // *** תיקון: הסרת filteredItems — applyFilters תמיד עובד על allItems ***
    private String selectedCategory = null; // null = כל הקטגוריות
    private String currentSearchQuery = "";
    private String currentSortOption = null; // null = ללא מיון

    private String current_userId = "";
    private FirebaseAuth mAuth;
    private User currentUser = null;
    Button btnPayCart;

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
        mAuth = FirebaseAuth.getInstance();
        current_userId = mAuth.getUid();

        assert current_userId != null;
        databaseService.getUser(current_userId, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(User user) {
                user.setPassword(null);
                user.setFavorites(null);
                user.setCart(null);
                user.setlName(null);
                currentUser = user;
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

        optionsContainer = findViewById(R.id.optionsContainerCart);
        ToggleFilter = findViewById(R.id.btnShowOptionsCart);
        optionOne = findViewById(R.id.option1);
        optionTwo = findViewById(R.id.option2);
        optionThree = findViewById(R.id.option3);
        optionFore = findViewById(R.id.option4);
        btnPayCart = findViewById(R.id.btnPayCart);

        ToggleFilter.setOnClickListener(this);
        optionOne.setOnClickListener(this);
        optionTwo.setOnClickListener(this);
        optionThree.setOnClickListener(this);
        optionFore.setOnClickListener(this);

        setupSearch();

        RecyclerView recyclerView = findViewById(R.id.RcCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvPay = findViewById(R.id.tvPay);

        cartAdapter = new CartAdapter(new ArrayList<>(), new CartAdapter.CartClickListener() {
            @Override
            public void onClick(ItemCart item) {
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(CartList.this, Item_page.class);
                intent.putExtra("Item_UID", item.getItem().getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(ItemCart item, int position) {
                // *** תיקון: מציאת הפריט ב-allItems לפי id, לא לפי position של הרשימה המסוננת ***
                new MaterialAlertDialogBuilder(CartList.this)
                        .setTitle("הסרה מהעגלה")
                        .setMessage("האם אתה בטוח שברצונך להסיר את " + item.getItem().getpName() + " מהסל?")
                        .setBackground(getResources().getDrawable(R.drawable.dialog_rounded_bg, getTheme()))
                        .setIcon(R.drawable.baseline_shopping_cart_24)
                        .setPositiveButton("כן, הסר", (dialog, which) -> {
                            // הסרה מ-allItems לפי id
                            allItems.removeIf(i -> i.getItem().getId().equals(item.getItem().getId()));
                            if (cart != null && cart.getItemArrayList() != null) {
                                cart.getItemArrayList().removeIf(i -> i.getItem().getId().equals(item.getItem().getId()));
                            }
                            applyFilters();
                            sumPrice();
                            updateCartInFirebase();
                        })
                        .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                        .show();
            }

            @Override
            public void onQuantityChanged(ItemCart item, int position) {
                sumPrice();
            }
        });

        recyclerView.setAdapter(cartAdapter);
        fetchCartFromFirebase();
    }

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

    /**
     * *** תיקון מרכזי ***
     * applyFilters תמיד מתחילה מ-allItems (כל הפריטים המקוריים),
     * מסננת לפי קטגוריה + חיפוש, ואז ממיינת.
     * selectedCategory = קטגוריה אמיתית (מסוג product type) — null/ריק = הכל
     * currentSortOption = "high" / "low" / "rate" / null
     */
    private void applyFilters() {
        List<ItemCart> result = new ArrayList<>();

        for (ItemCart item : allItems) {
            // סינון לפי קטגוריה
            boolean matchesCategory = (selectedCategory == null || selectedCategory.isEmpty())
                    || item.getItem().getType().equals(selectedCategory);

            // סינון לפי חיפוש
            boolean matchesSearch = currentSearchQuery.isEmpty()
                    || item.getItem().getpName().toLowerCase().contains(currentSearchQuery.toLowerCase());

            if (matchesCategory && matchesSearch) {
                result.add(item);
            }
        }

        // מיון
        if (currentSortOption != null) {
            switch (currentSortOption) {
                case "high":
                    result.sort((a, b) -> Double.compare(b.getItem().getPrice(), a.getItem().getPrice()));
                    break;
                case "low":
                    result.sort((a, b) -> Double.compare(a.getItem().getPrice(), b.getItem().getPrice()));
                    break;
                case "rate":
                    result.sort((a, b) -> Double.compare(b.getItem().getRate(), a.getItem().getRate()));
                    break;
            }
        }

        cartAdapter.setItem(result);
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
                    cart = cart2;
                } else {
                    allItems = new ArrayList<>();
                    cart = new Cart();
                }
                sumPrice();
                applyFilters(); // applyFilters מעדכנת את ה-adapter
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load cart", e);
                allItems = new ArrayList<>();
                cart = new Cart();
                cartAdapter.setItem(allItems);
                sumPrice();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnShowOptionsCart) {
            if (optionsContainer.getVisibility() == View.GONE) {
                optionsContainer.setVisibility(View.VISIBLE);
                optionsContainer.setAlpha(0f);
                optionsContainer.animate().alpha(1f).setDuration(300);
            } else {
                optionsContainer.setVisibility(View.GONE);
            }
        } else if (id == R.id.option1) {
            // *** תיקון: "without" = ללא מיון, לא קטגוריה ***
            currentSortOption = null;
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        } else if (id == R.id.option2) {
            currentSortOption = "high";
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        } else if (id == R.id.option3) {
            currentSortOption = "low";
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        } else if (id == R.id.option4) {
            currentSortOption = "rate";
            applyFilters();
            optionsContainer.setVisibility(View.GONE);
        }
    }

    private void sumPrice() {
        sum = 0.0;
        if (cart != null && cart.getItemArrayList() != null && !cart.getItemArrayList().isEmpty()) {
            for (ItemCart item : cart.getItemArrayList()) {
                if (item != null && item.getItem() != null) {
                    sum += (item.getItem().getPrice() * item.getAmount());
                }
            }
        }
        tvPay.setText("Total sum is: " + String.format("%.2f", sum) + "$");
    }

    private void processOrder() {
        if (cart == null || allItems.isEmpty()) {
            Toast.makeText(this, "העגלה ריקה!", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        String orderId = databaseService.generateOrderId();
        Order order = new Order(orderId, allItems, sum, "new", currentUser, timestamp);

        Toast.makeText(CartList.this, "הזמנה נשמרה!" + order.toString(), Toast.LENGTH_LONG).show();
        databaseService.createNewOreder(order, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(CartList.this, "הזמנה נשמרה!", Toast.LENGTH_SHORT).show();
                cart = new Cart();
                goUpdateCart(cart);
                Intent goLog = new Intent(CartList.this, Payment_page.class);
                goLog.putExtra("total", sum);
                startActivity(goLog);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CartList.this, "שגיאה בשמירת ההזמנה", Toast.LENGTH_SHORT).show();
                Intent goLog = new Intent(CartList.this, MainActivity.class);
                startActivity(goLog);
            }
        });
    }

    public void goUpdateCart(Cart cart) {
        databaseService.updateCart(current_userId, cart, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    public void onClickOrder(View view) {
        processOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_user) {
            startActivity(new Intent(CartList.this, UserProfile.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_history) {
            startActivity(new Intent(CartList.this, OrderHistory.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_favorites) {
            startActivity(new Intent(CartList.this, Favorites.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_cart) {
            startActivity(new Intent(CartList.this, CartList.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_logout) {
            startActivity(new Intent(CartList.this, LogIn.class));
            return true;
        }
        if (item.getItemId() == R.id.menu_admin) {
            if (LogIn.isAdmin) {
                startActivity(new Intent(CartList.this, AdminPage.class));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}