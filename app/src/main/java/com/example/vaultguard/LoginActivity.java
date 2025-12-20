package com.example.vaultguard;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    TextView loginFailed;
    Button loginButton;
    Button registerButton;
    String passwordText;
    String emailText;
    ImageView eyeView;
    boolean isPasswordVisible;


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
        loginFailed = findViewById(R.id.login_failed);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        eyeView = findViewById(R.id.show_password);
        passwordText = passwortFeld.getText().toString().trim();
        emailText = emailFeld.getText().toString().trim();
        isPasswordVisible = false;

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validateLogin();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            Intent registrieren = new Intent(LoginActivity.this, RegisterActivity.class);
            @Override
            public void onClick (View v) {
                startActivity(registrieren);
            }
        });

        eyeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwortFeld.setTransformationMethod(
                    PasswordTransformationMethod.getInstance()
            );
            isPasswordVisible = false;
        } else {
            passwortFeld.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance()
            );
            isPasswordVisible = true;
        }
    }

    private void clearErrors() {
        missingEmail.setVisibility(View.INVISIBLE);
        missingPassword.setVisibility(View.INVISIBLE);
        missingEmailAndPassword.setVisibility(View.INVISIBLE);
        invalidEmail.setVisibility(View.INVISIBLE);
        invalidPassword.setVisibility(View.INVISIBLE);
        loginFailed.setVisibility(View.INVISIBLE);
    }

    private boolean validateData() {
        clearErrors();

        String emailText = emailFeld.getText().toString().trim();
        String passwordText = passwortFeld.getText().toString().trim();

        if (emailText.isEmpty() && passwordText.isEmpty()) {
            missingEmailAndPassword.setVisibility(View.VISIBLE);
            return false;
        }
        if (emailText.isEmpty()) {
            missingEmail.setVisibility(View.VISIBLE);
            return false;
        }
        if (!emailText.contains("@")) {
            invalidEmail.setVisibility(View.VISIBLE);
            return false;
        }

        if (passwordText.isEmpty()) {
            missingPassword.setVisibility(View.VISIBLE);
            return false;
        }

        if (passwordText.length() < 6) {
            invalidPassword.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }

    private void fireBaseAuth() {
        String emailText = emailFeld.getText().toString().trim();
        String passwordText = passwortFeld.getText().toString().trim();
        Intent HomeScreen = new Intent(LoginActivity.this, HomeScreenActivity.class);
        auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> loginResult) {
                if (loginResult.isSuccessful()) {
                    startActivity(HomeScreen);
                } else loginFailed.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean validateLogin() {
        if (!validateData()) {
            return false;
        } else {
            fireBaseAuth();
            return true;
        }
    }
}