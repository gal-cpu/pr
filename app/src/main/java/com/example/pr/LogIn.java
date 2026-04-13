package com.example.pr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    public static final String MyPREFERENCES = "MyPrefs";
    Button btnLogIn;
    EditText etEmail, etPassword;
    TextView tvemail, tvpassword, tvNotExist;
    boolean emailcheck = false, passwordcheck = false;
    String emailUserInput, passwordUserInput, mNotExist = "", memail = "", mpassword = "";
    User current_user;
    DatabaseService databaseService;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseService = DatabaseService.getInstance();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        etEmail = findViewById(R.id.etEmailLogIn);
        etPassword = findViewById(R.id.etPasswordLogIn);
        btnLogIn = findViewById(R.id.btnSendLogIn);

        tvNotExist = findViewById(R.id.TvNotExistMessage);

        tvemail = findViewById(R.id.TvEmailMessage);
        tvpassword = findViewById(R.id.TvPasswordMessage);


        emailUserInput = sharedpreferences.getString("email", "");
        passwordUserInput = sharedpreferences.getString("password", "");

        etEmail.setText(emailUserInput);
        etPassword.setText(passwordUserInput);

        btnLogIn.setOnClickListener(this);
    }

    private void loginUser(String emailUserInput, String passwordUserInput) {
        databaseService.LoginUser(emailUserInput, passwordUserInput, new DatabaseService.DatabaseCallback<>() {
                    @Override
                    public void onCompleted(String userId) {
                        //saveUserById(userId);

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString("email", emailUserInput);

                        editor.putString("password", passwordUserInput);

                        editor.commit();

                        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<>() {
                            @Override
                            public void onCompleted(User user) {

                                current_user = user;

                                Intent go;
                                if (current_user.isAd()) {
                                    go = new Intent(LogIn.this, AdminPage.class);

                                } else {
                                    go = new Intent(LogIn.this, MainActivity.class);
                                }
                                startActivity(go);
                                finish();
                            }
                            @Override
                            public void onFailed(Exception e) {
                            }
                        });
                    }

                    @Override
                    public void onFailed(Exception e) {
                        // SharedPreferencesUtils.signOutUser(LogIn.this);


                        mNotExist = "The user is not found";
                        tvNotExist.setText(mNotExist);
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogIn) {
            emailUserInput = etEmail.getText().toString();
            passwordUserInput = etPassword.getText().toString();

            memail = "";
            mpassword = "";

            emailcheck = false;
            passwordcheck = false;

            if (emailUserInput.length() >= 6 && emailUserInput.length() <= 30) {
                if (emailUserInput.contains("@")) {
                    emailcheck = true;
                } else {
                    memail = "@ does not exist";
                }
            } else {
                memail = "the length should be 6-30";
            }

            tvemail.setText(memail);

            if (passwordUserInput.length() <= 12 && passwordUserInput.length() >= 6) {
                passwordcheck = true;
            } else {
                mpassword = "The length should be 6-12";
            }
            tvpassword.setText(mpassword);

            if (emailcheck && passwordcheck) {
                loginUser(emailUserInput, passwordUserInput);
            }
        }
    }

    public void onClickBackToSignUp(View view) {
        Intent go = new Intent(LogIn.this, SignUp.class);
        startActivity(go);
    }
}
