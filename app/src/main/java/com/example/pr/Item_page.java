package com.example.pr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.adapers.ItemsAdapter;
import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Item_page extends AppCompatActivity {
    private static final String TAG = "ItemsActivity";
    DatabaseService databaseService;
    private Button BuyItemrBtn, CartItemBtn;
    private ImageView ivItemField;
    private String selectedItemId;
    Item current_item;
    TextView tvName, tvNote, tvPrice, tvRate;


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
        databaseService = DatabaseService.getInstance();

        selectedItemId = getIntent().getSerializableExtra("Item_UID").toString();


        if (selectedItemId != "") {

            databaseService.getItem(selectedItemId, new DatabaseService.DatabaseCallback<Item>() {
                @Override
                public void onCompleted(Item item) {
                    current_item = item;
                    setupListeners();
                    populateFields();
                }

                @Override
                public void onFailed(Exception e) {

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
        tvRate = findViewById(R.id.tvRateItem);
        ivItemField = findViewById(R.id.ivItemAddCart);

        BuyItemrBtn = findViewById(R.id.BuyItemrBtn);
        CartItemBtn = findViewById(R.id.CartItemBtn);
    }

    private void setupListeners() {
        BuyItemrBtn.setOnClickListener(v -> buyItem());
        CartItemBtn.setOnClickListener(v -> addCartItem());
    }

    @SuppressLint("SetTextI18n")
    private void populateFields() {

        if (current_item != null) {

            tvName.setText("Name: " + current_item.getpName());
            tvNote.setText("Information: " + current_item.getpNote());
            tvRate.setText("Rate: " + current_item.getRate()+"⭐");
            tvPrice.setText("Price: " + current_item.getPrice() + "$");
            @Nullable Bitmap iv = ImageUtil.convertFrom64base(current_item.getImage());
            ivItemField.setImageBitmap(iv);
        }
    }
    private void buyItem() {
    }

    private void addCartItem() {
    }

}