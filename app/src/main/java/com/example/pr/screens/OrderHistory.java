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
import com.example.pr.adapers.OrderAdapter;
import com.example.pr.model.Order;
import com.example.pr.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends AppCompatActivity implements View.OnClickListener, OrderAdapter.OrderClickListener {

    private static final String TAG = "OrderHistory";
    private DatabaseService databaseService;
    private OrderAdapter orderAdapter;
    private RecyclerView rcOrders;
    private View btnShowOptionsOrder;
    private LinearLayout optionsContainerOrder;
    private TextView option1, option2, option3, option4;
    private List<Order> allOrders = new ArrayList<>();
    private String selectedSort = "without";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);

        // הגדרת Padding למערכת (סטטוס בר וכו')
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
        fetchOrdersFromFirebase();
    }

    private void initViews() {
        rcOrders = findViewById(R.id.rcOrders);
        btnShowOptionsOrder = findViewById(R.id.btnShowOptionsOrder);
        optionsContainerOrder = findViewById(R.id.optionsContainerOrder);

        // קישור האופציות מה-XML שלך
        option1 = findViewById(R.id.option1); // without
        option2 = findViewById(R.id.option2); // date
        option3 = findViewById(R.id.option3); // price (השתמשתי ב-option3 למחיר)
        option4 = findViewById(R.id.option4); // Committed

        // הגדרת מאזינים
        btnShowOptionsOrder.setOnClickListener(this);
        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        rcOrders.setLayoutManager(new LinearLayoutManager(this));
        // שליחת this פעמיים: פעם אחת כ-Context ופעם אחת כ-OrderClickListener
        orderAdapter = new OrderAdapter(this, this);
        rcOrders.setAdapter(orderAdapter);
    }

    private void fetchOrdersFromFirebase() {
        databaseService.getAllOrders(new DatabaseService.DatabaseCallback<List<Order>>() {
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
                Toast.makeText(OrderHistory.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFiltersAndSorting() {
        List<Order> filteredList = new ArrayList<>(allOrders);

        // לוגיקת מיון וסינון
        switch (selectedSort) {
            case "date ftl":
                // מהחדש לישן
                filteredList.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                break;
            case "date ltf":
                // מהישן לחדש
                filteredList.sort((a, b) -> Double.compare(a.getTimestamp(), b.getTimestamp()));
                break;
            case "committed":
                // סינון רק של אלו שסטטוס שלהם Committed
                List<Order> onlyCommitted = new ArrayList<>();
                for (Order o : allOrders) {
                    if ("Committed".equalsIgnoreCase(o.getStatus())) {
                        onlyCommitted.add(o);
                    }
                }
                filteredList = onlyCommitted;
                break;
        }

        orderAdapter.setOrders(filteredList);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnShowOptionsOrder) {
            int visibility = (optionsContainerOrder.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
            optionsContainerOrder.setVisibility(visibility);
        } else if (id == R.id.option1) {
            selectedSort = "without";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        } else if (id == R.id.option2) {
            selectedSort = "date ftl";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        } else if (id == R.id.option3) {
            selectedSort = "date  ltf";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        } else if (id == R.id.option4) {
            selectedSort = "committed";
            applyFiltersAndSorting();
            optionsContainerOrder.setVisibility(View.GONE);
        }
    }

    // מימוש ה-Interface של האדפטר (לחיצה רגילה)
    @Override
    public void onClick(Order order) {
        Toast.makeText(this, "הזמנה: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // כאן תוכל להוסיף מעבר למסך פרטי הזמנה (Intent)
    }

    // מימוש ה-Interface של האדפטר (לחיצה ארוכה)
    @Override
    public void onLongClick(Order order, int position) {
        // דוגמה: הצגת הודעה בלחיצה ארוכה
        Log.d(TAG, "Long click on: " + order.getOrderId());
    }
}
