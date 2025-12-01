package com.example.pr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.common.util.SharedPreferencesUtils;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
     Button btnLogIn;
    EditText etEmail, etPassword;
    TextView tvemail, tvpassword, tvNotExist;
    boolean emailcheck=false, passwordcheck=false;
    String emailUserInput, passwordUserInput,mNotExist="", memail="", mpassword="";
    DatabaseService databaseService;


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

        etEmail=findViewById(R.id.etEmailLogIn);
        etPassword=findViewById(R.id.etPasswordLogIn);
        btnLogIn=findViewById(R.id.btnSendLogIn);

        btnLogIn.setOnClickListener(this);
    }

    private void loginUser(String emailUserInput, String passwordUserInput){
        databaseService.LoginUser(emailUserInput, passwordUserInput, new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onCompleted(String userId) {
                       // saveUserById(userId);
                        Intent go= new Intent(LogIn.this, MainActivity.class);
                        startActivity(go);
                    }

                    @Override
                    public void onFailed(Exception e) {
                       // SharedPreferencesUtils.signOutUser(LogIn.this);

                        tvNotExist=findViewById(R.id.TvNotExistMessage);
                        mNotExist="The user is not found";
                        tvNotExist.setText(mNotExist);
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        if(view==btnLogIn) {
            emailUserInput = etEmail.getText().toString() + "";
            passwordUserInput = etPassword.getText().toString() + "";

            tvemail=findViewById(R.id.TvEmailMessage);
            tvpassword=findViewById(R.id.TvPasswordMessage);

            memail=""; mpassword="";

            emailcheck=false; passwordcheck=false;


            if (emailUserInput.length()>=6 && emailUserInput.length()<=30)
            {
                if(emailUserInput.contains("@gmail.com"))
                {
                    emailcheck=true;
                }
                else
                {
                    memail="@gmail.com does not exist";
                }
            }
            else
            {
                memail="the length should be 6-30";
            }

            tvemail.setText(memail);

            if (passwordUserInput.length()<=12 && passwordUserInput.length()>=6)
            {
                passwordcheck=true;
            }
            else
            {
                mpassword="The length should be 6-12";
            }
            tvpassword.setText(mpassword);

            if (emailcheck == true && passwordcheck == true)
            {
                loginUser(emailUserInput, passwordUserInput);
            }
        }
    }
}
