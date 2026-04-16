package com.example.pr.screens;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pr.R;
import com.example.pr.adapers.OrderAdapter;
import com.example.pr.model.Order;
import com.example.pr.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class OrderRowCart extends AppCompatActivity {
    private DatabaseService databaseService;
    private OrderAdapter orderAdapter;
    private RecyclerView rcOrders;
    private FirebaseAuth mAuth;
    private String current_userId = "";
    private List<Order> order = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_row_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();

        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        current_userId=mAuth.getUid();
        fetchOrdersFromFirebase();
    }
    private void initViews() {
        rcOrders = findViewById(R.id.rcOrderItem);
    }

    private void setupRecyclerView() {
        rcOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, (OrderAdapter.OrderClickListener) this);
        rcOrders.setAdapter(orderAdapter);
    }

    private void fetchOrdersFromFirebase() {
        databaseService.getUserOrders(current_userId, new DatabaseService.DatabaseCallback<List<Order>>() {
            @Override
            public void onCompleted(List<Order> orders) {
                if (orders != null) {
                    order = orders;
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load orders", e);
                Toast.makeText(OrderRowCart.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}