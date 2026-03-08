package com.example.pr;

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

    // אובייקט לניהול בחירת תמונה מהגלריה
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    // עדכון ה-ImageView בתמונה החדשה שנבחרה
                    ivItemField.setImageURI(imageUri);
                }
            }
    );

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

        // קבלת ה-ID של המוצר מה-Intent
        if (getIntent().getSerializableExtra("Item_UID") != null) {
            selectedItemId = getIntent().getSerializableExtra("Item_UID").toString();

            databaseService.getItem(selectedItemId, new DatabaseService.DatabaseCallback<Item>() {
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
        // כפתור עדכון
        updateBtn.setOnClickListener(v -> updateItem());

        // כפתור מחיקה
        deleteBtn.setOnClickListener(v -> deleteItem());

        // לחיצה על התמונה פותחת את הגלריה
        ivItemField.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void populateFields() {
        if (current_item != null) {
            namelField.setText(current_item.getpName());
            typeField.setText(current_item.getType());
            typeField.setEnabled(false); // בדרך כלל לא משנים סוג מוצר קיים
            noteField.setText(current_item.getpNote());
            priceField.setText(current_item.getPrice() + "");

            // המרת המחרוזת מה-DB חזרה לתמונה
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

            // 1. עדכון נתוני הטקסט באובייקט
            current_item.setpName(name);
            current_item.setpNote(note);
            current_item.setPrice(priceD);

            // 2. המרת התמונה שמוצגת כרגע ב-ImageView ל-Base64
            // שים לב: השם הוא convertTo64base עם b קטנה בדיוק כמו ב-ImageUtil שלך
            String updatedImageBase64 = ImageUtil.convertTo64Base(ivItemField);

            if (updatedImageBase64 != null) {
                current_item.setImage(updatedImageBase64);
            }

            // 3. שמירה ב-Firebase
            databaseService.updateItem(current_item, new DatabaseService.DatabaseCallback<Void>() {
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

        databaseService.deleteItem(current_item.getId(), new DatabaseService.DatabaseCallback<Void>() {
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
