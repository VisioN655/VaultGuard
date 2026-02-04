package com.example.vaultguard;

import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditPasswordActivity extends AppCompatActivity {

    TextInputEditText titleInput;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    Button cancelButton;
    Button saveButton;
    CardView uploadImage;
    TextView uploadText;
    ImageView eyeView;

    Uri selectedImage;
    ActivityResultLauncher<String> imagePickerLauncher;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageRef;

    boolean isPasswordVisible;

    String docId;
    String title;
    String email;
    String password;
    String currentImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        titleInput = findViewById(R.id.title_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
        uploadImage = findViewById(R.id.upload_border);
        uploadText = findViewById(R.id.upload_text);
        eyeView = findViewById(R.id.show_password);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        isPasswordVisible = false;
        docId = getIntent().getStringExtra("docId");
        title = getIntent().getStringExtra("title");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        currentImageUrl = getIntent().getStringExtra("imageURL");

        titleInput.setText(title);
        emailInput.setText(email);
        passwordInput.setText(password);

        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            uploadText.setText("Bild ausgewählt ✓");
        } else {
            uploadText.setText("Bild hochladen");
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            selectedImage = uri;
                            uploadText.setText("Bild ausgewählt ✓");
                        } else {
                            selectedImage = null;
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateInput()) {
                    return;
                }

                if (selectedImage != null) {
                    uploadImageAndUpdate();
                } else {
                    updatePassword(currentImageUrl);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        eyeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private boolean validateInput() {

        String titleText = titleInput.getText().toString().trim();
        String emailText = emailInput.getText().toString().trim();
        String passwordText = passwordInput.getText().toString().trim();

        if (titleText.isEmpty()) {
            Toast.makeText(this, "Bitte Titel eingeben", Toast.LENGTH_LONG).show();
            return false;
        }
        if (emailText.isEmpty() && passwordText.isEmpty()) {
            Toast.makeText(this, "Bitte E-Mail und Passwort eingeben", Toast.LENGTH_LONG).show();
            return false;
        }
        if (emailText.isEmpty()) {
            Toast.makeText(this, "Bitte E-Mail eingeben", Toast.LENGTH_LONG).show();
            return false;
        }
        if (passwordText.isEmpty()) {
            Toast.makeText(this, "Bitte Passwort eingeben", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void uploadImageAndUpdate() {

        StorageReference imageRef = storageRef
                .child("users")
                .child(auth.getUid())
                .child("images")
                .child(System.currentTimeMillis() + ".jpg");

        imageRef.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        updatePassword(uri.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditPasswordActivity.this, "Upload fehlgeschlagen", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updatePassword(String imageUrl) {

        String title = titleInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String encryptedPassword = Encryption.encrypt(password);

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("email", email);
        data.put("password", encryptedPassword);
        data.put("imageURL", imageUrl);

        db.collection("users")
                .document(uid)
                .collection("passwords")
                .document(docId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void v) {
                        Toast.makeText(EditPasswordActivity.this, "Passwort aktualisiert", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void togglePasswordVisibility() {

        if (isPasswordVisible) {
            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPasswordVisible = false;
        } else {
            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isPasswordVisible = true;
        }

        if (passwordInput.getText() != null) {
            passwordInput.setSelection(passwordInput.getText().length());
        }
    }
}