package com.example.pr;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.ItemCart;
import com.example.pr.model.Order;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

import java.util.Objects;

public class payment_page extends AppCompatActivity implements View.OnClickListener {
    double price;
    EditText edId, edFullName, edNumberCard, edCVV;
    TextView tvPricePayment;
    Button btnPayment;
    Spinner spinnerMonth, spinnerYear;
    User current_user;
    ItemCart itemCart;
    DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseService = DatabaseService.getInstance();

        itemCart = getIntent().getParcelableExtra("ItemCart");
        price = getIntent().getDoubleExtra("price", 0.0);

        edId = findViewById(R.id.edId);
        edFullName = findViewById(R.id.edFullName);
        edNumberCard = findViewById(R.id.edNumberCard);
        edCVV = findViewById(R.id.edCVV);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);

        tvPricePayment = findViewById(R.id.tvPricePayment);
        tvPricePayment.setText("Total price: " + price + "$");

        btnPayment = findViewById(R.id.btnPayment);
        btnPayment.setOnClickListener(this);

        String selectedUserId = Objects.requireNonNull(getIntent().getSerializableExtra("USER_UID")).toString();

        if (!selectedUserId.isEmpty()) {

            databaseService.getUser(selectedUserId, new DatabaseService.DatabaseCallback<>() {
                @Override
                public void onCompleted(User user) {
                    current_user = user;
                }

                @Override
                public void onFailed(Exception e) {

                }
            });

        }
    }

    @Override
    public void onClick(View v) {


        String stId = edId.getText().toString().trim();
        String stFullName = edFullName.getText().toString().trim();
        String stNumberCard = edNumberCard.getText().toString().trim();
        String stCVV = edCVV.getText().toString().trim();

       // Order(stId, itemCart, price, s, current_user, time) {

        //}

    }
}