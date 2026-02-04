package com.example.vaultguard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreenActivity extends AppCompatActivity {
    MaterialCardView addButton;
    ImageView dropdownMenuButton;
    FirebaseAuth auth;
    AlertDialog dialogResetPassword;
    AlertDialog dialogConfirmReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        addButton = findViewById(R.id.add_button);
        dropdownMenuButton = findViewById(R.id.dropdown_menu_button);
        auth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
            .beginTransaction()
                    .replace(R.id.fragment_container, new PasswordListFragment())
                    .commit();
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            Intent create_password = new Intent(HomeScreenActivity.this, AddPasswordActivity.class);
            @Override
            public void onClick(View v) {
                startActivity(create_password);
            }
        });

        dropdownMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(HomeScreenActivity.this, v);

                menu.getMenu().add("Logout");
                menu.getMenu().add("Passwort zurücksetzen");

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {

                        String text = item.getTitle().toString();

                        if (text.equals("Logout")) {
                            doLogout();
                            return true;
                        }

                        if (text.equals("Passwort zurücksetzen")) {
                            showResetPasswordDialog();
                            return true;
                        }

                        return false;
                    }
                });
                menu.show();
            }
        });
    }

    private void doLogout() {
        Intent logout = new Intent(HomeScreenActivity.this, LoginActivity.class);
        startActivity(logout);
        finish();
    }

    private void showResetPasswordDialog() {
        LayoutInflater dialogInflater = getLayoutInflater();
        View dialogView = dialogInflater.inflate(R.layout.dialog_reset_password, null);
        TextInputEditText emailFeld = dialogView.findViewById(R.id.dialog_email);
        Button sendenButton = dialogView.findViewById(R.id.send_button);
        Button abbrechenButton = dialogView.findViewById(R.id.cancel_button);

        dialogResetPassword = new AlertDialog.Builder(HomeScreenActivity.this).setView(dialogView).setCancelable(true).create();
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
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(this,"Das E-Mail-Feld darf nicht leer sein!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HomeScreenActivity.this,"Bitte geben Sie eine gültige E-Mail-Adresse ein!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showConfirmResetDialog() {
        LayoutInflater dialogInflater = getLayoutInflater();
        View dialogView = dialogInflater.inflate(R.layout.dialog_confirm_reset, null);
        Button bestaetigenButton = dialogView.findViewById(R.id.confirm_button);

        dialogConfirmReset = new AlertDialog.Builder(HomeScreenActivity.this).setView(dialogView).setCancelable(true).create();
        dialogConfirmReset.show();
        dialogConfirmReset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bestaetigenButton.setOnClickListener(new View.OnClickListener() {
            Intent login_screen = new Intent(HomeScreenActivity.this, LoginActivity.class);
            @Override
            public void onClick(View v) {
                dialogConfirmReset.dismiss();
                startActivity(login_screen);
                finish();
            }
        });
    }
}
