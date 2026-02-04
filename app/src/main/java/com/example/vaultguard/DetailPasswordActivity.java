package com.example.vaultguard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailPasswordActivity extends AppCompatActivity {

    TextView titleView;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    ImageView iconView;
    ImageView eyeView;
    Button cancelButton;
    Button editButton;
    Button deleteButton;
    Button copyEmailOrUsernameButton;
    Button copyPasswordButton;
    String docId;
    String title;
    String email;
    String password;
    String imageURL;
    boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_password);

        titleView = findViewById(R.id.platform_title);
        emailInput = findViewById(R.id.item_email_input);
        passwordInput = findViewById(R.id.item_password_input);
        iconView = findViewById(R.id.platform_icon);
        eyeView = findViewById(R.id.show_password);
        cancelButton = findViewById(R.id.cancel_button);
        editButton = findViewById(R.id.edit_button);
        copyEmailOrUsernameButton = findViewById(R.id.copy_email_or_username_button);
        copyPasswordButton = findViewById(R.id.copy_password_button);
        deleteButton = findViewById(R.id.delete_button);
        isPasswordVisible = false;

        docId = getIntent().getStringExtra("docId");

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

        editButton.setOnClickListener(new View.OnClickListener() {
            Intent edit_password = new Intent(DetailPasswordActivity.this, EditPasswordActivity.class);
            @Override
            public void onClick(View v) {
                edit_password.putExtra("docId", docId);
                edit_password.putExtra("title", title);
                edit_password.putExtra("email", email);
                edit_password.putExtra("password", password);
                edit_password.putExtra("imageURL", imageURL);
                startActivity(edit_password);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePassword(docId);
            }
        });

        copyEmailOrUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard =
                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                clipboard.setPrimaryClip(
                        ClipData.newPlainText("email", emailInput.getText().toString())
                );

                Toast.makeText(DetailPasswordActivity.this,
                        "E-Mail/Nutzername kopiert", Toast.LENGTH_SHORT).show();
            }
        });

        copyPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard =
                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                clipboard.setPrimaryClip(
                        ClipData.newPlainText("password", passwordInput.getText().toString())
                );

                Toast.makeText(DetailPasswordActivity.this,
                        "Passwort kopiert", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPassword();
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

    private void deletePassword(String docId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("passwords")
                .document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Toast.makeText(DetailPasswordActivity.this, "Passwort gelöscht", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void loadPassword() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("passwords")
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {

                        title = doc.getString("title");
                        email = doc.getString("email");
                        password = doc.getString("password");
                        imageURL = doc.getString("imageURL");

                        titleView.setText(title);
                        emailInput.setText(email);
                        passwordInput.setText(password);

                        Glide.with(DetailPasswordActivity.this)
                                .load(imageURL)
                                .placeholder(R.drawable.rounded_rectangle_bg)
                                .error(R.drawable.rounded_rectangle_bg)
                                .into(iconView);
                    }
                });
    }
}