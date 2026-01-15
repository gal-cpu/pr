package com.example.pr;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private String selectedUserId="";
    DatabaseService  databaseService;
    User current_user=null;

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

        initViews();
        databaseService=DatabaseService.getInstance();

        selectedUserId = getIntent().getSerializableExtra("USER_UID").toString();

        if(selectedUserId!="") {

            databaseService.getUser(selectedUserId, new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    current_user=user;
                    setupListeners();
                    populateFields();

                }

                @Override
                public void onFailed(Exception e) {

                }
            });

        }

        else {    Toast.makeText(UpdateUser.this,
                "jkj"+ selectedUserId,
                Toast.LENGTH_SHORT).show(); }



    }
    private void initViews() {
        emailField = findViewById(R.id.EmailUpdate);
        passwordField = findViewById(R.id.PasswordUpdate);
        firstnameField = findViewById(R.id.FirstNameUpdate);
        lastnameField = findViewById(R.id.LastNameUpdate);
        phoneField=findViewById(R.id.PhoneUpdate);
        isAdminCheckBox= findViewById(R.id.isAdminCheckBoxUpdate);
        updateBtn = findViewById(R.id.updateUserBtn);
        deleteBtn = findViewById(R.id.deleteUserBtn);
    }
    private void setupListeners() {
        updateBtn.setOnClickListener(v -> updateUser());
        deleteBtn.setOnClickListener(v -> deleteUser());
    }
    private void populateFields() {

        if (current_user != null) {

            emailField.setText(current_user.getEmail() + "");
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
        Boolean isAdmin = isAdminCheckBox.isChecked();

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