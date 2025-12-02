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
    TextInputEditText emailFeld = findViewById(R.id.email_input);
    TextInputEditText passwortFeld = findViewById(R.id.password_input);
    TextView missingEmailAndPassword = findViewById(R.id.missing_email_and_password);
    TextView missingEmail = findViewById(R.id.missing_email);
    TextView missingPassword = findViewById(R.id.missing_password);
    TextView invalidEmail = findViewById(R.id.invalid_email);
    TextView invalidPassword = findViewById(R.id.invalid_password);

    String passwordText = passwortFeld.getText().toString().trim();
    String emailText = emailFeld.getText().toString().trim();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    private void clearErrors() {
        missingEmail.setVisibility(View.GONE);
        missingPassword.setVisibility(View.GONE);
        missingEmailAndPassword.setVisibility(View.GONE);
        invalidEmail.setVisibility(View.GONE);
        invalidPassword.setVisibility(View.GONE);

    }
    private boolean validateLogin() {

        clearErrors();

        if (emailText.isEmpty() && passwordText.isEmpty()) {
            missingEmailAndPassword.setVisibility(View.VISIBLE);
            return false;
        }
        else if (emailText.isEmpty()) {
            missingEmail.setVisibility(View.VISIBLE);
            return false;
        }
        else if (passwordText.isEmpty()) {
            missingPassword.setVisibility(View.VISIBLE);
            return false;
        }
        else if (!emailText.contains("@"))  {
            invalidEmail.setVisibility(View.VISIBLE);
            return false;
        }
        else if (passwordText.length() < 6) {
            invalidPassword.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }
}

