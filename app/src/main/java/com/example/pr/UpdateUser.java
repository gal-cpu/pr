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
    private EditText emailField, passwordField, firstnameField, lastnameField, phoneField;
    private TextView tvfname, tvlname, tvemail, tvphone, tvpassword;
    private String mfname = "", mlname = "", mphone = "";
    private CheckBox isAdminCheckBox;
    private Button updateBtn, deleteBtn;
    private String selectedUserId = "";
    DatabaseService databaseService;
    User current_user = null;

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
        databaseService = DatabaseService.getInstance();

        selectedUserId = getIntent().getSerializableExtra("USER_UID").toString();

        if (selectedUserId != "") {

            databaseService.getUser(selectedUserId, new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    current_user = user;
                    setupListeners();
                    populateFields();
                }

                @Override
                public void onFailed(Exception e) {

                }
            });

        } else {
            Toast.makeText(UpdateUser.this,
                    " " + selectedUserId,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        tvfname = findViewById(R.id.TvFnameMessageUpdate);
        tvlname = findViewById(R.id.TvLnameMessageUpdate);
        tvphone = findViewById(R.id.TvPhoneMessageUpdate);

        emailField = findViewById(R.id.EmailUpdate);
        passwordField = findViewById(R.id.PasswordUpdate);
        firstnameField = findViewById(R.id.FirstNameUpdate);
        lastnameField = findViewById(R.id.LastNameUpdate);
        phoneField = findViewById(R.id.PhoneUpdate);
        isAdminCheckBox = findViewById(R.id.isAdminCheckBoxUpdate);
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
            emailField.setEnabled(false);
            passwordField.setText(current_user.getPassword());
            passwordField.setEnabled(false);
            firstnameField.setText(current_user.getfName());
            lastnameField.setText(current_user.getlName());
            phoneField.setText(current_user.getPhone());
            isAdminCheckBox.setChecked(current_user.gatIsAd());
        }
    }

    private void updateUser() {
        if (selectedUserId == null) return;

        String firstname = firstnameField.getText().toString().trim();
        String lastname = lastnameField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        boolean isAdmin = isAdminCheckBox.isChecked();

        boolean fnamecheck = true;
        boolean lnamecheck = true;
        boolean phonecheck = false;

        if (firstname.length() > 1 && firstname.length() <= 10) {
            for (int i = 0; i <= 10; i++) {

                if (firstname.contains(i + "")) {
                    fnamecheck = false;
                    mfname = "no digits";
                }
            }
        } else {
            fnamecheck = false;
            mfname = "the length should be 2-10";
        }

        tvfname.setText(mfname);

        if (lastname.length() > 1 && lastname.length() <= 20) {
            for (int i = 0; i <= 10; i++) {

                if (lastname.contains(i + "")) {
                    lnamecheck = false;
                    mlname = "no digits";
                }
            }
        } else {
            lnamecheck = false;
            mlname = "the length should be 2-20";
        }
        tvlname.setText(mlname);

        if (phone.length() == 10) {
            phonecheck = true;
        } else {
            mphone = "phone number has 10 digits";
        }
        tvphone.setText(mphone);

        // Update user details

        if (phone.isEmpty() || lastname.isEmpty() || firstname.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fnamecheck == false || lnamecheck == false || phonecheck == false) {
            return;
        }

        current_user.setfName(firstname);
        current_user.setlName(lastname);
        current_user.setPhone(phone);
        current_user.setAd(isAdmin);

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
