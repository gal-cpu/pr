package com.example.pr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pr.model.User;
import com.example.pr.services.DatabaseService;

public class SignUp extends AppCompatActivity implements View.OnClickListener {


    String fname, lname, email, phone, password, mfname="", mlname="", memail="", mphone="", mpassword="";
    TextView tvfname, tvlname, tvemail, tvphone, tvpassword;
    boolean fnamecheck=true, lnamecheck=true, emailcheck=false, phonecheck=false, passwordcheck=false;

    private static final String TAG = "RegisterActivity";

    private EditText etEmail, etPassword, etFName, etLName, etPhone;
    private Button btnRegister;

    DatabaseService databaseService;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



        databaseService = DatabaseService.getInstance();


        initViews();

        /// get the views

    }


    private void initViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFName = findViewById(R.id.etFname);
        etLName = findViewById(R.id.etLname);
        etPhone = findViewById(R.id.etPhone);

        btnRegister = findViewById(R.id.btSendSignUP);

        /// set the click listener
        btnRegister.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == btnRegister.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String fName = etFName.getText().toString();
            String lName = etLName.getText().toString();
            String phone = etPhone.getText().toString();


            tvfname=findViewById(R.id.TvFnameMessage);
            tvlname=findViewById(R.id.TvLnameMessage);
            tvemail=findViewById(R.id.TvEmailMessage);
            tvphone=findViewById(R.id.TvPhoneMessage);
            tvpassword=findViewById(R.id.TvPasswordMessage);



            mfname=""; mlname=""; memail=""; mphone=""; mpassword="";

            fnamecheck=true; lnamecheck=true; emailcheck=false; phonecheck=false; passwordcheck=false;

            fname=etFName.getText().toString();
            lname=etLName.getText().toString();
            email=etEmail.getText().toString();
            phone=etPhone.getText().toString();
            password=etPassword.getText().toString();


            if (fname.length()>1 && fname.length()<=10)
            {
                for (int i=0; i<=10;i++)
                {

                    if(fname.contains(i+""))
                    {
                        fnamecheck=false;
                        mfname="no digits";
                    }
                }
            }
            else
            {
                mfname="the length should be 2-10";
            }

            tvfname.setText(mfname);

            if (lname.length()>1 && lname.length()<=20)
            {
                for (int i=0; i<=10;i++)
                {

                    if(lname.contains(i+""))
                    {
                        lnamecheck=false;
                        mlname="no digits";
                    }
                }
            }
            else
            {
                mlname="the length should be 2-20";
            }
            tvlname.setText(mlname);

            if (phone.length()==10)
            {
                phonecheck=true;
            }
            else
            {
                mphone="phone number has 10 digits";
            }
            tvphone.setText(mphone);


            if (email.length()>=6 && email.length()<=30)
            {
                if(email.contains("@gmail.com"))
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

            if (password.length()<=12 && password.length()>=6)
            {
                passwordcheck=true;
            }
            else
            {
                mpassword="The length should be 6-12";
            }
            tvpassword.setText(mpassword);

            if (fnamecheck == true && lnamecheck == true && emailcheck == true && phonecheck == true && passwordcheck == true)
            {
                /// Validate input
                //   Log.d(TAG, "onClick: Validating input...");


                Log.d(TAG, "onClick: Registering user...");

                /// Register user
                registerUser(fName, lName, phone, email, password);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("email", email);

                editor.putString("password", password);

                editor.commit();

            }
        }
    }

    /// Register the user
    private void registerUser(String fname, String lname, String phone, String email, String password) {
        Log.d(TAG, "registerUser: Registering user...");


        /// create a new user object
        User user = new User("54", fname, lname, email, phone, password);


        /// proceed to create the user
        createUserInDatabase(user);

    }

    private void createUserInDatabase(User user) {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("email", email);

        editor.putString("password", password);

        editor.commit();

        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                user.setId(uid);




                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(SignUp.this, MainActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(SignUp.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }
    public void onClickBackSignUp(View view)
    {
        Intent go= new Intent(SignUp.this, HomePage.class);
        startActivity(go);
    }
}