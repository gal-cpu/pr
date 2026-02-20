package com.example.pr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;

import org.jetbrains.annotations.Nullable;

public class UpdateItem extends AppCompatActivity {

    DatabaseService databaseService;
    Item current_item;
    private EditText namelField, typeField, noteField, priceField;
    private ImageView ivItemField;
    private Button updateBtn, deleteBtn;
    private String selectedItemId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_item);
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
            Toast.makeText(UpdateItem.this,
                    " " + selectedItemId,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        namelField = findViewById(R.id.ItemNameUpdate);
        typeField = findViewById(R.id.ItemTypeUpdate);
        noteField = findViewById(R.id.ItemNoteUpdate);
        priceField = findViewById(R.id.ItemPriceUpdate);
        ivItemField = findViewById(R.id.imageViewPicture);

        updateBtn = findViewById(R.id.updatItemrBtn);
        deleteBtn = findViewById(R.id.deleteItemBtn);
    }

    private void setupListeners() {
        updateBtn.setOnClickListener(v -> updateItem());
        deleteBtn.setOnClickListener(v -> deleteItem());
    }

    @SuppressLint("SetTextI18n")
    private void populateFields() {

        if (current_item != null) {

            namelField.setText(current_item.getpName());
            typeField.setText(current_item.getType());
            typeField.setEnabled(false);
            noteField.setText(current_item.getpNote());
            priceField.setText(current_item.getPrice() + "");
            @Nullable Bitmap iv = ImageUtil.convertFrom64base(current_item.getImage());
            ivItemField.setImageBitmap(iv);
        }
    }

    private void updateItem() {
        if (selectedItemId == null) return;

        String name = namelField.getText().toString().trim();
        String type = typeField.getText().toString().trim();
        String note = noteField.getText().toString().trim();
        String price = priceField.getText().toString().trim();


        if (name.isEmpty() || type.isEmpty() || note.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        double priceD = Double.parseDouble(price);

        //Update item details
        current_item.setpName(name);
        current_item.setType(type);
        current_item.setpNote(note);
        current_item.setPrice(priceD);

        //Save item
        DatabaseService.getInstance().updateItem(current_item, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(UpdateItem.this,
                        "Item updated successfully",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UpdateItem.this, TableItems.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateItem.this,
                        "Failed to update stats: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteItem() {
        if (selectedItemId == null) return;

        DatabaseService.getInstance().deleteItem(current_item.getId(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(UpdateItem.this, "Item deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateItem.this, TableItems.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateItem.this,
                        "Delete failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}


