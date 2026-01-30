package com.example.vaultguard;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;

public class AddPasswordActivity extends AppCompatActivity {
    TextInputEditText titleInput;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    Button cancelButton;
    Button saveButton;
    CardView uploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        titleInput = findViewById(R.id.title_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
        uploadImage = findViewById(R.id.upload_border);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private boolean validateInput() {
        String titleText = titleInput.getText().toString().trim();
        String emailText = emailInput.getText().toString().trim();
        String passwordText = passwordInput.getText().toString().trim();

        if (titleText.isEmpty()) {
            Toast.makeText(this, "Bitte geben Sie den Titel der Plattform ein.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (emailText.isEmpty() & passwordText.isEmpty()) {
            Toast.makeText(this, "Bitte geben Sie eine E-Mail/einen Benutzernamen und ein Passwort ein.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (emailText.isEmpty()) {
            Toast.makeText(this, "Bitte geben Sie eine E-Mail oder einen Benutzernamen ein.", Toast.LENGTH_LONG).show();
            return false;
        }
        if (passwordText.isEmpty()) {
            Toast.makeText(this, "Bitte geben Sie ein Passwort ein.", Toast.LENGTH_LONG).show();
        }
        return true;
    }
}
