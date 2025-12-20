package com.example.vaultguard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextInputEditText emailFeld;
    TextInputEditText passwortFeld;
    TextView missingEmailAndPassword;
    TextView missingEmail;
    TextView missingPassword;
    TextView invalidEmail;
    TextView invalidPassword;
    Button loginButton;
    String passwordText;
    String emailText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        emailFeld = findViewById(R.id.email_input);
        passwortFeld = findViewById(R.id.password_input);
        missingEmailAndPassword = findViewById(R.id.missing_email_and_password);
        missingEmail = findViewById(R.id.missing_email);
        missingPassword = findViewById(R.id.missing_password);
        invalidEmail = findViewById(R.id.invalid_email);
        invalidPassword = findViewById(R.id.invalid_password);

        loginButton = findViewById(R.id.login_button);


        passwordText = passwortFeld.getText().toString().trim();
        emailText = emailFeld.getText().toString().trim();
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validateLogin();
            }

        });
    }

    private void clearErrors() {
        missingEmail.setVisibility(View.GONE);
        missingPassword.setVisibility(View.GONE);
        missingEmailAndPassword.setVisibility(View.GONE);
        invalidEmail.setVisibility(View.GONE);
        invalidPassword.setVisibility(View.GONE);

    }
    private boolean validateData() {
        clearErrors();

        String emailText = emailFeld.getText().toString().trim();
        String passwordText = passwortFeld.getText().toString().trim();

        if (emailText.isEmpty() && passwordText.isEmpty()) {
            Log.d("LOGIN", "Email und Passwort fehlt");
            missingEmailAndPassword.setVisibility(View.VISIBLE);
            return false;
        }
        if (emailText.isEmpty()) {
            Log.d("LOGIN", "Email fehlt");
            missingEmail.setVisibility(View.VISIBLE);
            return false;
        }
        if (!emailText.contains("@"))  {
            Log.d("LOGIN", "Email falsch");
            invalidEmail.setVisibility(View.VISIBLE);
            return false;
        }

        if (passwordText.isEmpty()) {
            Log.d("LOGIN", "Passwort fehlt");
            missingPassword.setVisibility(View.VISIBLE);
            return false;
        }

        if (passwordText.length() < 6) {
            Log.d("LOGIN", "Passwort kurz");
            invalidPassword.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }

    private void validateLogin() {
        validateData();
    }
}