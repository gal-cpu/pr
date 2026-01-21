package com.example.pr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.Item;
import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

public class UpdateItem extends AppCompatActivity {

    private EditText namelField, typeField, noteField, priceField;
    private Button updateBtn, deleteBtn;
    private String selectedItemId="";
    DatabaseService databaseService;
    User current_item=null;

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
        databaseService=DatabaseService.getInstance();

        selectedItemId = getIntent().getSerializableExtra("Item_UID").toString();

        if(selectedItemId!="") {

            databaseService.getUser(selectedItemId, new DatabaseService.DatabaseCallback<Item>() {
                @Override
                public void onCompleted(Item item) {
                    current_item=item;
                    setupListeners();
                    populateFields();
                }

                @Override
                public void onFailed(Exception e) {

                }
            });

        }

        else {    Toast.makeText(UpdateIem.this,
                " "+ selectedItemId,
                Toast.LENGTH_SHORT).show(); }
    }
    private void initViews() {
        namelField = findViewById(R.id.ItemNameUpdate);
        typeField = findViewById(R.id.ItemTypeUpdate);
        noteField = findViewById(R.id.ItemNoteUpdate);
        priceField = findViewById(R.id.ItemPriceUpdate);

        updateBtn = findViewById(R.id.updateUserBtn);
        deleteBtn = findViewById(R.id.deleteItemBtn);
    }
    private void setupListeners() {
        updateBtn.setOnClickListener(v -> updateItem());
        deleteBtn.setOnClickListener(v -> deleteItem());
    }
    private void populateFields() {

        if (current_item != null) {

            namelField.setText(current_item.getEmail() + "");
            passwordField.setText(current_user.getPassword());
            firstnameField.setText(current_user.getfName());
            lastnameField.setText(current_user.getlName());
            phoneField.setText(current_user.getPhone());
            isAdminCheckBox.setChecked(current_user.gatIsAd());
        }
    }
    private void updateUser() {
        if (selectedUserId == null) return;

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String firstname = firstnameField.getText().toString().trim();
        String lastname = lastnameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        boolean isAdmin = isAdminCheckBox.isChecked();

        if (email.isEmpty() || password.isEmpty() || phone.isEmpty() ||  lastname.isEmpty() || firstname.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user details
        current_user.setEmail(email);
        current_user.setPassword(password);
        current_user.setfName(firstname);
        current_user.setlName(lastname);
        current_user.setPhone(phone);
        current_user.setIsAd(isAdmin);

        // Save user
        DatabaseService.getInstance().updateUser(current_user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(UpdateUser.this,
                        "User updated successfully",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(UpdateUser.this, TableUsers.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateUser.this,
                        "Failed to update stats: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        if (selectedUserId == null) return;

        DatabaseService.getInstance().deleteUser(current_user.getId(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(UpdateUser.this, "User deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateUser.this, TableUsers.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(UpdateUser.this,
                        "Delete failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}