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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.zip.Inflater;

public class RegisterActivity extends AppCompatActivity {

    // Firebase Auth für Registrierung
    FirebaseAuth auth;

    // Eingabefelder
    TextInputEditText emailFeld;
    TextInputEditText passwortFeld;

    // Buttons
    Button registerButton;
    Button loginButton;

    // Fehlermeldungen (UI)
    TextView missingEmailAndPassword;
    TextView missingEmail;
    TextView missingPassword;
    TextView invalidEmail;
    TextView invalidPassword;
    TextView emailAlreadyUsed;

    // Passwort anzeigen/verbergen
    ImageView eyeView;
    boolean isPasswordVisible;

    // Bestätigungsdialog nach Registrierung
    AlertDialog dialogConfirmRegister;

    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase initialisieren
        auth = FirebaseAuth.getInstance();

        // UI-Elemente verbinden
        emailFeld = findViewById(R.id.email_input);
        passwortFeld = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);

        missingEmailAndPassword = findViewById(R.id.missing_email_and_password);
        missingEmail = findViewById(R.id.missing_email);
        missingPassword = findViewById(R.id.missing_password);
        invalidEmail = findViewById(R.id.invalid_email);
        invalidPassword = findViewById(R.id.invalid_password);
        emailAlreadyUsed = findViewById(R.id.email_already_used);

        eyeView = findViewById(R.id.show_password);
        isPasswordVisible = false;

        email = emailFeld.getText().toString().trim();
        password = passwortFeld.getText().toString().trim();

        // Registrieren-Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                validateRegister();
            }
        });

        // Zurück zum Login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                finish();
            }
        });

        // Passwort sichtbar/unsichtbar
        eyeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    // Toggle Passwort-Anzeige
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

    // Setzt alle Fehlermeldungen zurück
    private void clearErrors() {
        missingEmail.setVisibility(View.INVISIBLE);
        missingPassword.setVisibility(View.INVISIBLE);
        missingEmailAndPassword.setVisibility(View.INVISIBLE);
        invalidEmail.setVisibility(View.INVISIBLE);
        invalidPassword.setVisibility(View.INVISIBLE);
        emailAlreadyUsed.setVisibility(View.INVISIBLE);
    }

    // Validiert Eingaben (Frontend)
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


    // Zeigt Bestätigungsdialog nach erfolgreicher Registrierung
    private void showConfirmRegisterDialog() {
        LayoutInflater dialogInflater = getLayoutInflater();
        View dialogView = dialogInflater.inflate(R.layout.dialog_confirm_register, null);
        Button bestaetigenButton = dialogView.findViewById(R.id.confirm_button);
        Intent loginScreen = new Intent(RegisterActivity.this, LoginActivity.class);

        dialogConfirmRegister = new AlertDialog.Builder(RegisterActivity.this).setView(dialogView).setCancelable(true).create();
        dialogConfirmRegister.show();
        dialogConfirmRegister.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bestaetigenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmRegister.dismiss();
                startActivity(loginScreen);
            }
        });
    }

    // Sendet Verifizierungs-Mail nach Registrierung
    private void fireBaseConfirmRegister() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> verifyTask) {
                    showConfirmRegisterDialog();
                }
            });
        }
    }

    // Registrierung bei Firebase
    private void fireBaseRegister() {
        email = emailFeld.getText().toString().trim();
        password = passwortFeld.getText().toString().trim();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> registerTask) {
                Exception e = registerTask.getException();
                if (registerTask.isSuccessful()) {
                    fireBaseConfirmRegister();
                    Log.d("REGISTER", "Erfolgreich registriert!");
                    return;
                } else {

                    // Fehlerbehandlung
                    if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                        emailAlreadyUsed.setVisibility(View.VISIBLE);
                    }
                    else if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                        invalidEmail.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    // Gesamt-Registrierungsflow
    private boolean validateRegister() {
        if (!validateData()) {
            return false;
        } else {
            fireBaseRegister();
            return true;
        }
    }
}