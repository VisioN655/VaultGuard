package com.example.vaultguard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    Button resetPasswordButton;
    String passwordText;
    String emailText;
    ImageView eyeView;
    boolean isPasswordVisible;
    AlertDialog dialogResetPassword;
    AlertDialog dialogConfirmReset;


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
        resetPasswordButton = findViewById(R.id.reset_password_button);
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
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
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

    private void showResetPasswordDialog() {
        LayoutInflater dialogInflater = getLayoutInflater();
        View dialogView = dialogInflater.inflate(R.layout.dialog_reset_password, null);
        TextInputEditText emailFeld = dialogView.findViewById(R.id.dialog_email);
        Button sendenButton = dialogView.findViewById(R.id.send_button);
        Button abbrechenButton = dialogView.findViewById(R.id.cancel_button);

        dialogResetPassword = new AlertDialog.Builder(LoginActivity.this).setView(dialogView).setCancelable(true).create();
        dialogResetPassword.show();
        dialogResetPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        abbrechenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResetPassword.dismiss();
            }
        });

        sendenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String email = emailFeld.getText().toString().trim();
            if (!validateEmail(email)) {
                return;
            }
            fireBaseResetPassword(email);
            }
        });
    }

    private void showConfirmResetDialog() {
        LayoutInflater dialogInflater = getLayoutInflater();
        View dialogView = dialogInflater.inflate(R.layout.dialog_confirm_reset, null);
        Button bestaetigenButton = dialogView.findViewById(R.id.confirm_button);

        dialogConfirmReset = new AlertDialog.Builder(LoginActivity.this).setView(dialogView).setCancelable(true).create();
        dialogConfirmReset.show();
        dialogConfirmReset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bestaetigenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmReset.dismiss();
            }
        });
    }

    private void clearErrors() {
        missingEmail.setVisibility(View.INVISIBLE);
        missingPassword.setVisibility(View.INVISIBLE);
        missingEmailAndPassword.setVisibility(View.INVISIBLE);
        invalidEmail.setVisibility(View.INVISIBLE);
        invalidPassword.setVisibility(View.INVISIBLE);
        loginFailed.setVisibility(View.INVISIBLE);
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(this,"Das E-Mail-Feld darf nicht leer sein!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private void fireBaseResetPassword(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> resetPassword) {
                if (resetPassword.isSuccessful()) {
                    if (dialogResetPassword != null)  {
                    dialogResetPassword.dismiss();
                    }
                    showConfirmResetDialog();
                    return;
                }
                Exception e = resetPassword.getException();

                if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(LoginActivity.this,"Bitte geben Sie eine gültige E-Mail-Adresse ein!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void fireBaseAuthLogin() {
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
            fireBaseAuthLogin();
            return true;
        }
    }
}