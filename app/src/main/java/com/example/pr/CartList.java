package com.example.pr;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.pr.services.DatabaseService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CartList extends AppCompatActivity {

    private static final String TAG = "cart";
    private DatabaseService databaseService;
    private TextView tvPay;
    private CartAdapter cartAdapter; // שימוש ב-CartAdapter
    private List<Item> allIitems = new ArrayList<>();
    private String current_userId = "";

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

        if (mAuth.getCurrentUser() != null) {
            current_userId = mAuth.getCurrentUser().getUid();
        }

        RecyclerView recyclerView = findViewById(R.id.RcCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvPay = findViewById(R.id.tvPay);

        // אתחול ה-CartAdapter עם ה-Listener המתאים
        cartAdapter = new CartAdapter(this, allIitems, new CartAdapter.CartClickListener() {
            @Override
            public void onClick(Item item) {
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(CartList.this, Item_page.class);
                intent.putExtra("Item_UID", item.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Item item, int position) {
                // דיאלוג הסרה מהעגלה
                new MaterialAlertDialogBuilder(CartList.this)
                        .setTitle("הסרה מהעגלה")
                        .setMessage("האם אתה בטוח שברצונך להסיר את " + item.getpName() + " מהסל?")
                        .setBackground(getResources().getDrawable(R.drawable.dialog_rounded_bg, getTheme()))
                        .setIcon(R.drawable.baseline_shopping_cart_24)
                        .setPositiveButton("כן, הסר", (dialog, which) -> {
                            if (allIitems != null && position < allIitems.size()) {
                                // 1. הסרה מהרשימה המקומית
                                allIitems.remove(position);
                                // 2. עדכון ה-Adapter
                                cartAdapter.setItem(allIitems);
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
        FirebaseDatabase.getInstance().getReference("users")
                .child(current_userId)
                .child("cart")
                .child("itemArrayList")
                .setValue(allIitems)
                .addOnSuccessListener(aVoid -> Toast.makeText(CartList.this, "הסל עודכן בהצלחה", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(CartList.this, "שגיאה בסנכרון", Toast.LENGTH_SHORT).show();
                    fetchCartFromFirebase(); // טעינה מחדש במקרה של שגיאה
                });
    }

    private void fetchCartFromFirebase() {
        databaseService.getCart(current_userId, new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Cart cart) {
                if (cart != null && cart.getItemArrayList() != null) {
                    allIitems = cart.getItemArrayList();
                } else {
                    allIitems = new ArrayList<>();
                }
                cartAdapter.setItem(allIitems);
                sumPrice();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load cart", e);
                allIitems = new ArrayList<>();
                cartAdapter.setItem(allIitems);
                sumPrice();
            }
        });
    }

    private void sumPrice() {
        double sum = 0.0;
        if (allIitems != null) {
            for (Item item : allIitems) {
                if (item != null) {
                    sum += (item.getPrice() * item.getQuantity());
                }
            }
        }
        tvPay.setText("Total sum is: " + String.format("%.2f", sum) + "$");
    }
}
