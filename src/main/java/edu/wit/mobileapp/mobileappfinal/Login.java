package edu.wit.mobileapp.mobileappfinal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    //initialize class variables
    EditText mEmail, mPassword;
    TextView mRegisterBtn;
    Button mLoginBtn;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        //link class variables with layout display variables in xml
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mRegisterBtn = findViewById(R.id.login_text2);
        mLoginBtn = findViewById(R.id.login_button);

        fAuth = FirebaseAuth.getInstance();

        //route to register page
        mRegisterBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));

        //when login button is clicked
        mLoginBtn.setOnClickListener(v -> {

            //this line is just so we don't have to sign in every time
            //testing purposes
            startActivity(new Intent(getApplicationContext(), Home.class));

            final String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            //checks if inputs are empty
            //can implement more errors to display to user for invalid logins
            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Email input is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Password input is required.");
                return;
            }

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                    //after authentication is complete - route to homepage
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                } else {
                    //can further implement here
                    //highlight text fields in red
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            });

        });
    }
}