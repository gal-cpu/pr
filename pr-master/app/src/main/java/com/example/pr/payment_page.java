package com.example.pr;

import android.os.Bundle;
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

public class payment_page extends AppCompatActivity implements View.OnClickListener {
    double price;
    EditText edId, edFullName, edNumberCard, edCVV;
    TextView tvPricePayment;
    Button btnPayment;
    Spinner spinnerMonth, spinnerYear;

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
        price = getIntent().getDoubleExtra("price", 0.0);

        edId = findViewById(R.id.edId);
        edFullName = findViewById(R.id.edFullName);
        edNumberCard = findViewById(R.id.edNumberCard);
        edCVV = findViewById(R.id.edCVV);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear =findViewById(R.id.spinnerYear);

        tvPricePayment = findViewById(R.id.tvPricePayment);
        tvPricePayment.setText("Total price: "+price+ "$");

        btnPayment = findViewById(R.id.btnPayment);
        btnPayment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String stId = edId.getText().toString().trim();
        String stFullName = edFullName.getText().toString().trim();
        String stNumberCard = edNumberCard.getText().toString().trim();
        String stCVV = edCVV.getText().toString().trim();


    }
}