package com.example.vaultguard;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

public class DetailPasswordActivity extends AppCompatActivity {

    TextView titleView;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    ImageView iconView;
    ImageView eyeView;
    Button cancelButton;
    Button editButton;
    Button deleteButton;
    boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_password);

        String docId = getIntent().getStringExtra("docId");
        String title = getIntent().getStringExtra("title");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String imageURL = getIntent().getStringExtra("imageURL");

        titleView = findViewById(R.id.platform_title);
        emailInput = findViewById(R.id.item_email_input);
        passwordInput = findViewById(R.id.item_password_input);
        iconView = findViewById(R.id.platform_icon);
        eyeView = findViewById(R.id.show_password);
        cancelButton = findViewById(R.id.cancel_button);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);
        isPasswordVisible = false;

        eyeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setText(title);
        emailInput.setText(email);
        passwordInput.setText(password);

        Glide.with(this)
                .load(imageURL)
                .placeholder(R.drawable.rounded_rectangle_bg)
                .error(R.drawable.rounded_rectangle_bg)
                .into(iconView);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordInput.setTransformationMethod(
                    PasswordTransformationMethod.getInstance()
            );
            isPasswordVisible = false;
        } else {
            passwordInput.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance()
            );
            isPasswordVisible = true;
        }
    }
}
