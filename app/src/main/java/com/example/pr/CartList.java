package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.model.Cart;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CartList extends AppCompatActivity {

    private static final String TAG = "cart";
    DatabaseService databaseService;
    private TextView tvPay;
    private ItemsAdapter itemsAdapter;
    private List<Item> allIitems;
    private String current_userId="";

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

        current_userId = mAuth.getCurrentUser().getUid();

        RecyclerView recyclerView = findViewById(R.id.RcCart);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvPay = findViewById(R.id.tvPay);

        itemsAdapter = new ItemsAdapter(new ItemsAdapter.ItemClickListener() {
            @Override
            public void onClick(Item item) {
                // Handle item click
                Log.d(TAG, "Item clicked: " + item);
                Intent intent = new Intent(CartList.this, Item_page.class);
                intent.putExtra("Item_UID", item.getId());
                startActivity(intent);
            }
            // בתוך ה-onLongClick של ה-ItemsAdapter
            @Override
            public void onLongClick(Item item, int position) {
                // יצירת הדיאלוג המעוצב של Material Design
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(CartList.this)
                        .setTitle("הסרה מהעגלה")
                        .setMessage("האם אתה בטוח שברצונך להסיר את " + item.getpName() + " מהסל?")

                        // חיבור קובץ ה-XML של הפינות המעוגלות שיצרת ב-drawable
                        .setBackground(getResources().getDrawable(R.drawable.dialog_rounded_bg, getTheme()))

                        // הוספת אייקון (אופציונלי, מוסיף המון לעיצוב)
                        .setIcon(R.drawable.baseline_shopping_cart_24)

                        // הגדרת כפתור המחיקה (חיובי)
                        .setPositiveButton("כן, הסר", (dialog, which) -> {

                            // בדיקת הגנה למניעת קריסה
                            if (allIitems != null && position < allIitems.size()) {

                                // 1. הסרה מהרשימה המקומית ב-Activity
                                allIitems.remove(position);

                                // 2. עדכון ה-Adapter (יפעיל את ה-setItem שכתבת עם notifyDataSetChanged)
                                itemsAdapter.setItem(allIitems);

                                // 3. עדכון סכום המחיר לתשלום במסך
                                sumPrice();

                                // 4. עדכון ה-Firebase - דריסת הרשימה הישנה ברשימה החדשה (הכי בטוח)
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(current_userId)
                                        .child("cart")
                                        .child("itemArrayList")
                                        .setValue(allIitems)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(CartList.this, "המוצר הוסר בהצלחה", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(CartList.this, "שגיאה בסנכרון מול השרת", Toast.LENGTH_SHORT).show();
                                            // במקרה של שגיאה, נטען את העגלה מחדש מה-DB כדי להישאר מסונכרנים
                                            fetchCartFromFirebase();
                                        });
                            }
                        })

                        // הגדרת כפתור הביטול (שלילי)
                        .setNegativeButton("ביטול", (dialog, which) -> {
                            dialog.dismiss(); // פשוט סוגר את הדיאלוג בלי לעשות כלום
                        })

                        // הצגת הדיאלוג על המסך
                        .show();
            }

        });
        recyclerView.setAdapter(itemsAdapter);

       // fetchItemsFromFirebase();

        fetchCartFromFirebase();
    }

    private void fetchItemsFromFirebase() {

        // טעינת המוצרים
        databaseService.getUser(current_userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                Log.d(TAG, "onCompleted: " + user);
                allIitems = user.getCart().getItemArrayList();
                itemsAdapter.setItem(allIitems);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load items: ", e);
                new android.app.AlertDialog.Builder(CartList.this)
                        .setMessage("נראה שקרתה תקלה בטעינת המוצרים, נסה שוב מאוחר יותר")
                        .setPositiveButton("אוקי", null)
                        .show();
            }
        });
    }

    private void fetchCartFromFirebase() {

        databaseService.getCart(current_userId, new DatabaseService.DatabaseCallback<Cart>() {
            @Override
            public void onCompleted(Cart cart) {

                if(cart==null || cart.getItemArrayList()==null)
                {
                    cart=new Cart();
                    allIitems=new ArrayList<>();}
                else if (cart.getItemArrayList() !=null && cart!=null)
                 {
                    allIitems=(cart.getItemArrayList());
                  }
               // else allIitems=new ArrayList<>();
                itemsAdapter.setItem(allIitems);
                sumPrice();
            }

            @Override
            public void onFailed(Exception e) {

                allIitems=new ArrayList<>();
                itemsAdapter.setItem(allIitems);
            }
        });
    }

    private void sumPrice() {
        double sum = 0.0;
        if (allIitems != null) {
            for (Item item : allIitems) {
                if (item != null) {
                    sum += item.getPrice();
                }
            }
        }
        // עיגול ל-2 ספרות אחרי הנקודה כדי שזה יראה טוב
        tvPay.setText("Total sum is: " + String.format("%.2f", sum) + "$");
    }

}