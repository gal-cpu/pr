package com.example.pr.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.R;
import com.example.pr.model.Item;
import com.example.pr.services.DatabaseService;
import com.example.pr.util.ImageUtil;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UpdateItem extends AppCompatActivity {

    DatabaseService databaseService;
    Item current_item;
    private EditText namelField, typeField, noteField, priceField;
    private ImageView ivItemField;
    private Button updateBtn, deleteBtn;

    int SELECT_PICTURE = 200;

    // לכידת תמונה מהמצלמה — בדיוק כמו ב-AddItem
    private ActivityResultLauncher<Intent> captureImageLauncher;

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

        ImageUtil.requestPermission(this);

        // רישום launcher למצלמה — בדיוק כמו ב-AddItem
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
                        ivItemField.setImageBitmap(bitmap);
                    }
                });

        if (getIntent().getSerializableExtra("Item_UID") != null) {
            String selectedItemId = Objects.requireNonNull(getIntent().getSerializableExtra("Item_UID")).toString();

            databaseService.getItem(selectedItemId, new DatabaseService.DatabaseCallback<>() {
                @Override
                public void onCompleted(Item item) {
                    current_item = item;
                    setupListeners();
                    populateFields();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(UpdateItem.this, "שגיאה בטעינת המוצר", Toast.LENGTH_SHORT).show();
                }
            });
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

        // לחיצה על התמונה — פתיחת גלריה בדיוק כמו ב-AddItem
        ivItemField.setOnClickListener(v -> imageChooser());
    }

    // בדיוק אותה פונקציה כמו ב-AddItem
    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // בדיוק אותה פונקציה כמו ב-AddItem
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    ivItemField.setImageURI(selectedImageUri);
                }
            }
        }
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
            if (iv != null) {
                ivItemField.setImageBitmap(iv);
            }
        }
    }

    private void updateItem() {
        if (current_item == null) return;

        String name = namelField.getText().toString().trim();
        String note = noteField.getText().toString().trim();
        String price = priceField.getText().toString().trim();

        if (name.isEmpty() || note.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double priceD = Double.parseDouble(price);

            current_item.setpName(name);
            current_item.setpNote(note);
            current_item.setPrice(priceD);

            // בדיוק כמו ב-AddItem — convertTo64Base מקבל את ה-ImageView
            String updatedImageBase64 = ImageUtil.convertTo64Base(ivItemField);
            if (updatedImageBase64 != null) {
                current_item.setImage(updatedImageBase64);
            }

            databaseService.updateItem(current_item, new DatabaseService.DatabaseCallback<>() {
                @Override
                public void onCompleted(Void v) {
                    Toast.makeText(UpdateItem.this, "המוצר עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateItem.this, TableItems.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(UpdateItem.this, "עדכון נכשל: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "נא להזין מחיר תקין", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem() {
        if (current_item == null) return;

        databaseService.deleteItem(current_item.getId(), new DatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(UpdateItem.this, "המוצר נמחק", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateItem.this, TableItems.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateItem.this, "מחיקה נכשלה", Toast.LENGTH_SHORT).show();
            }
        });
    }
}