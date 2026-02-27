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

            @Override
            public void onLongClick(Item item, int position) {
                new AlertDialog.Builder(CartList.this)
                        .setTitle("הסרה מהעגלה")
                        .setMessage("האם להסיר את " + item.getpName() + " מהעגלה שלך?")
                        .setPositiveButton("כן", (dialog, which) -> {

                            // 1. קבלת ה-UID של המשתמש המחובר
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // 2. הצבעה על המיקום המדויק בתוך ה-User ומחיקת הפריט
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(uid)
                                    .child("cart") // וודא שזה השם המדויק של ה-node של העגלה אצלך
                                    .child(item.getId())
                                    .removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // 3. עדכון התצוגה רק אחרי שהמחיקה ב-Firebase הצליחה
                                        allIitems.remove(position);
                                        itemsAdapter.notifyItemRemoved(position);
                                        Toast.makeText(CartList.this, "הוסר מהעגלה", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CartList.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("ביטול", null)
                        .show();
                sumPrice();
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

    private void sumPrice(){
        double sum=0;
        for (int i=0; i<allIitems.toArray().length;i++)
            sum = sum + allIitems.get(i).getPrice();
        tvPay.setText("Total sum is: " + sum + "$");
    }
}