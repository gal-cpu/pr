package com.example.pr.screens;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.example.pr.model.Order;
import com.example.pr.services.DatabaseService;
import com.example.pr.services.NotificationHelper;

public class UpdateOrder extends AppCompatActivity {

    TextView tvOrderId;
    TextView tvUserName;
    TextView tvBuyerPhone;
    TextView tvTimestamp;
    TextView tvTotalPrice;
    TextView tvPaymentMethod;
    TextView tvStatus;
    Order currentOrder=null;

    String orderId=null;
    private RecyclerView rcOrderItems;

    Intent takeit;


    DatabaseService databaseService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        initViews();
        takeit=getIntent();

        orderId=  takeit.getStringExtra("orderId");

        if(orderId!=null) {

            databaseService.getOrder(orderId, new DatabaseService.DatabaseCallback<Order>() {
                @Override
                public void onCompleted(Order order) {
                    currentOrder=order;


                    setData();
                    Toast.makeText(UpdateOrder.this, currentOrder.toString(), LENGTH_LONG).show();
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
    }

    private void setData() {

        tvOrderId.setText(currentOrder.getOrderId());


        // Buyer details
        if (currentOrder.getUser() != null) {
            tvUserName.setText(currentOrder.getUser().getfName() + " " +currentOrder.getUser().getlName());
            tvBuyerPhone.setText(currentOrder.getUser().getPhone());
        }

        // Date
     tvTimestamp.setText(currentOrder.getFormattedDate());

        // Total price
        tvTotalPrice.setText(String.format("₪%.2f", currentOrder.getTotalPrice()));

        // Status badge
       tvStatus.setText(currentOrder.getStatus());

        // הגדרת לחיצה על כל השורה



        // Items RecyclerView
        if (currentOrder.getItems() != null) {
            com.example.pr.adapers.OrderItemAdapter itemAdapter = new com.example.pr.adapers.OrderItemAdapter(currentOrder.getItems());

            rcOrderItems.setLayoutManager(new LinearLayoutManager(this));

            rcOrderItems.setAdapter(itemAdapter);
           rcOrderItems.setNestedScrollingEnabled(false);
        }
    }

    private void initViews() {

        databaseService=DatabaseService.getInstance();

        tvOrderId       = findViewById(R.id.tvOrderIdValueOUp);
        tvUserName      = findViewById(R.id.tvUserValueOUp);
        tvBuyerPhone    = findViewById(R.id.tvBuyerPhoneUp);
        tvTimestamp     = findViewById(R.id.tvTimestampValueOUp);
        tvTotalPrice    = findViewById(R.id.tvTotalPriceValueOUp);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethodUp);
        tvStatus        = findViewById(R.id.tvStatusBadgeUp);
        rcOrderItems     = findViewById(R.id.rcOrderItemsUp);
    }

    public void UpdateOrderAndSave(View view) {
        currentOrder.setStatus("Done");
        databaseService.updateOrder(currentOrder, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

                // ✅ שלח התראה
                NotificationHelper.sendOrderReadyNotification(
                        UpdateOrder.this,
                        currentOrder.getOrderId()
                );

                Toast.makeText(UpdateOrder.this, "Order status updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateOrder.this, OrderHistory.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Intent intent = new Intent(UpdateOrder.this, TableOrders.class);
                startActivity(intent);
            }
        });
    }

    public void goBackToAllOrders(View view) {
    }
}