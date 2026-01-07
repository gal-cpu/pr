package com.example.pr;

import static androidx.core.content.ContextCompat.startActivity;

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

import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

public class UpdateUser extends AppCompatActivity {
    private EditText emailField, passwordField, firstnameField, lastnameField,phoneField;
    private CheckBox isAdminCheckBox;
    private Button updateBtn, deleteBtn;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedUser = (User) getIntent().getSerializableExtra("user");
       //selectedUser = getIntent().getStringExtra("user");

        initViews();
        setupListeners();

        populateFields();

    }
    private void initViews() {
        emailField = findViewById(R.id.Email);
        passwordField = findViewById(R.id.Password);
        firstnameField = findViewById(R.id.FirstName);
        lastnameField = findViewById(R.id.LastName);
        phoneField=findViewById(R.id.Phone);
        isAdminCheckBox= findViewById(R.id.isAdminCheckBox);
        updateBtn = findViewById(R.id.updateUserBtn);
        deleteBtn = findViewById(R.id.deleteUserBtn);
    }

    private void setupListeners() {
        updateBtn.setOnClickListener(v -> updateUser());
        deleteBtn.setOnClickListener(v -> deleteUser());
    }
    private void populateFields() {
        if (selectedUser == null) return;

        emailField.setText(selectedUser.getEmail());
        passwordField.setText(selectedUser.getPassword());
        firstnameField.setText(selectedUser.getfName());
        lastnameField.setText(selectedUser.getlName());
        phoneField.setText(selectedUser.getPhone());
        isAdminCheckBox.setChecked(selectedUser.getAdmin());
    }
    private void updateUser() {
        if (selectedUser == null) return;

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String firstname = firstnameField.getText().toString().trim();
        String lastname = lastnameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        //Boolean isAdmin = isAdminCheckBox.isChecked();

        if (email.isEmpty() || password.isEmpty() || phone.isEmpty() ||  lastname.isEmpty() || firstname.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user details
        selectedUser.setEmail(email);
        selectedUser.setPassword(password);
        selectedUser.setfName(firstname);
        selectedUser.setlName(lastname);
        selectedUser.setPhone(phone);
        //selectedUser.setAdmin(isAdmin);


        // Save user
        DatabaseService.getInstance().updateUser(selectedUser, new DatabaseService.DatabaseCallback<Void>() {
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
    if (selectedUser == null) return;

    DatabaseService.getInstance().deleteUser(selectedUser.getId(), new DatabaseService.DatabaseCallback<Void>() {
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