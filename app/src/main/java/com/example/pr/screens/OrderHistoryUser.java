package com.example.pr.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.pr.adapers.OrdersAdapter;
import com.example.pr.model.Order;
import com.example.pr.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryUser extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OrderHistory";
    private DatabaseService databaseService;
    private OrdersAdapter orderAdapter;
    private RecyclerView rcOrders;
    private View btnShowOptionsOrder;
    private String current_userId = "";
    private LinearLayout optionsContainerOrder;
    private TextView option1, option2, option3, option4, option5;
    private List<Order> allOrders = new ArrayList<>();
    private String selectedSort = "without";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history_user);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();
        setupRecyclerView();

        databaseService = DatabaseService.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            current_userId = mAuth.getCurrentUser().getUid();
        }
        fetchOrdersFromFirebase();
    }

    private void initViews() {
        rcOrders = findViewById(R.id.rcOrdersUser);
        btnShowOptionsOrder = findViewById(R.id.btnShowOptionsOrderUser);
        optionsContainerOrder = findViewById(R.id.optionsContainerOrderUser);

        option1 = findViewById(R.id.option1User);
        option2 = findViewById(R.id.option2User);
        option3 = findViewById(R.id.option3User);
        option4 = findViewById(R.id.option4User);
        option5= findViewById(R.id.option5User);

        btnShowOptionsOrder.setOnClickListener(this);
        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        option5.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        rcOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrdersAdapter(allOrders, new OrdersAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {}

            @Override
            public void onOrderLongClick(Order order) {}
        });
        rcOrders.setAdapter(orderAdapter);
    }

    private void fetchOrdersFromFirebase() {
        databaseService.getUserOrders(current_userId, new DatabaseService.DatabaseCallback<List<Order>>() {
            @Override
            public void onCompleted(List<Order> orders) {
                if (orders != null) {
                    allOrders = orders;
                    applyFiltersAndSorting();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to load orders", e);
                Toast.makeText(OrderHistoryUser.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFiltersAndSorting() {
        List<Order> filteredList = new ArrayList<>(allOrders);

        switch (selectedSort) {
            case "date ftl":
                filteredList.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                break;
            case "date ltf":
                filteredList.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
                break;
            case "Done":
                List<Order> onlyCommitted = new ArrayList<>();
                for (Order o : allOrders) {
                    if ("Done".equalsIgnoreCase(o.getStatus())) {
                        onlyCommitted.add(o);
                    }
                }
                filteredList = onlyCommitted;
                break;
            case "new":
                List<Order> onlyNew = new ArrayList<>();
                for (Order o : allOrders) {
                    if ("new".equalsIgnoreCase(o.getStatus())) {
                        onlyNew.add(o);
                    }
                }
                filteredList = onlyNew;
                break;
        }

        orderAdapter.setOrderList(filteredList);
        orderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // *** תיקון: שימוש ב-IDs הנכונים עם סיומת User ***
        if (id == R.id.btnShowOptionsOrderUser) {
            int visibility = (optionsContainerOrder.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
            optionsContainerOrder.setVisibility(visibility);
        } else if (id == R.id.option1User) {
            selectedSort = "without";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        } else if (id == R.id.option2User) {
            selectedSort = "date ftl";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        } else if (id == R.id.option3User) {
            selectedSort = "date ltf"; // *** תיקון נוסף: היה רווח כפול "date  ltf" ***
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        } else if (id == R.id.option4User) {
            selectedSort = "committed";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        }else if (id == R.id.option5User) {
            selectedSort = "new";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        }
    }
}