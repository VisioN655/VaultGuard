package com.example.androidprojekt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
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

        if (emailText.isEmpty() && passwordText.isEmpty()) {
            missingEmailAndPassword.setVisibility(View.VISIBLE);
            return false;
        } else if (emailText.isEmpty()) {
            missingEmail.setVisibility(View.VISIBLE);
            return false;
        } else if (passwordText.isEmpty()) {
            missingPassword.setVisibility(View.VISIBLE);
            return false;
        } else if (!emailText.contains("@")) {
            invalidEmail.setVisibility(View.VISIBLE);
            return false;
        } else if (passwordText.length() < 6) {
            invalidPassword.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }
}
