package com.example.pr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.Cart;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class Item_page extends AppCompatActivity {
    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    Item current_item; // השתמשנו במשתנה הזה באופן עקבי
    TextView tvName, tvNote, tvPrice, tvAverageRating;
    FirebaseAuth mAuth;
    String userId;
    Cart userCart = null;
    private RatingBar ratingBarUser;
    private Button btnSubmitRating;

    private double lastRatingByUser = 0;
    private boolean hasRatedBefore = false;

    private Button AddToFav, CartItemBtn;
    private ImageView ivItemField;
    private String selectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseService = DatabaseService.getInstance();

        // קבלת ה-ID של המוצר מה-Intent
        Object itemIntentId = getIntent().getSerializableExtra("Item_UID");
        if (itemIntentId != null) {
            selectedItemId = itemIntentId.toString();

        databaseService.getCart(userId, new DatabaseService.DatabaseCallback<Cart>() {
            @Override
            public void onCompleted(Cart cart) {

                if (cart == null)
                    cart = new Cart();

                userCart = cart;
            }

            @Override
            public void onFailed(Exception e) {
                userCart = new Cart();
            }
        });

        selectedItemId = getIntent().getSerializableExtra("Item_UID").toString();


        if (selectedItemId != "") {

            databaseService.getItem(selectedItemId, new DatabaseService.DatabaseCallback<Item>() {
                @Override
                public void onCompleted(Item item) {
                    current_item = item;
                    setupListeners();
                    populateFields();
                    setupListeners(); // מפעילים מאזינים רק כשיש אובייקט
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(Item_page.this, "טעינת מוצר נכשלה", Toast.LENGTH_SHORT).show();
                }
            });
        }

            // טעינת העגלה
            databaseService.getCart(userId, new DatabaseService.DatabaseCallback<Cart>() {
                @Override
                public void onCompleted(Cart cart) {
                    userCart = (cart == null) ? new Cart() : cart;
                }
                @Override
                public void onFailed(Exception e) {
                    userCart = new Cart();
                }
            });

        } else {
            Toast.makeText(Item_page.this,
                    " " + selectedItemId,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        tvName = findViewById(R.id.tvNameItem);
        tvNote = findViewById(R.id.tvNoteItem);
        tvPrice = findViewById(R.id.tvPriceItem);
        ivItemField = findViewById(R.id.ivItemAddCart);
        AddToFav = findViewById(R.id.AddTOFavBtn);
        CartItemBtn = findViewById(R.id.CartItemBtn);

        // רכיבי הדירוג
        ratingBarUser = findViewById(R.id.ratingBarUser);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        CartItemBtn = findViewById(R.id.CartItemBtn);
    }

    private void setupListeners() {
        CartItemBtn.setOnClickListener(v -> addCartItem());


        // כפתור דירוג
        btnSubmitRating.setOnClickListener(v -> {
            if (current_item == null) return;

            double newUserRating = ratingBarUser.getRating();
            if (newUserRating > 0) {
                // 1. עדכון לוגי באובייקט (חובה להוסיף את הפונקציה ב-Item.java כפי שהסברתי קודם)
                current_item.updateRating(lastRatingByUser, newUserRating, !hasRatedBefore);

                // 2. עדכון התצוגה
                tvAverageRating.setText(String.format("דירוג ממוצע: %.1f", current_item.getRate()));

                // 3. שמירה ל-Firebase (שימוש ב-DatabaseService הקיים שלך)
                // כאן אנחנו מעדכנים את המוצר בטבלת המוצרים הכללית
                databaseService.addItem(current_item, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void unused) {
                        lastRatingByUser = newUserRating;
                        hasRatedBefore = true;
                        Toast.makeText(Item_page.this, "הדירוג עודכן!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(Item_page.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "אנא בחר כוכבים לדירוג", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields() {

        if (current_item != null) {

            tvName.setText("Name: " + current_item.getpName());
            tvNote.setText("Information: " + current_item.getpNote());
            tvPrice.setText("Price: " + current_item.getPrice() + "$");
            tvAverageRating.setText(String.format("דירוג ממוצע: %.1f", current_item.getRate()));
            @Nullable Bitmap iv = ImageUtil.convertFrom64base(current_item.getImage());
            if (iv != null) ivItemField.setImageBitmap(iv);
        }
    }

    private void addCartItem() {
        if (userCart != null && current_item != null) {
                databaseService.updateCart(userId, user -> {
                    if (user == null) return null;
                    user.getCart().addItem(current_item);
                    return user;
                }, new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User updatedUser) {
                        Intent go;
                        if (current_item.getType().equals("book")) {
                            go = new Intent(Item_page.this, Book_page.class);
                            go.putExtra("type", "book");
                        }else if (current_item.getType().equals("toy")) {
                            go = new Intent(Item_page.this, Book_page.class);
                            go.putExtra("type", "toy");
                        }else if (current_item.getType().equals("device")) {
                            go = new Intent(Item_page.this, Book_page.class);
                            go.putExtra("type", "device");
                        }else if (current_item.getType().equals("shoe")) {
                            go = new Intent(Item_page.this, Book_page.class);
                            go.putExtra("type", "shoe");
                        }else {
                            go = new Intent(Item_page.this, MainActivity.class);
                        }
                        startActivity(go);
                        finish();
                    }
                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(Item_page.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }
}