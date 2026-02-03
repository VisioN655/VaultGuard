package com.example.vaultguard;


import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPasswordActivity extends AppCompatActivity {
    TextInputEditText titleInput;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    Button cancelButton;
    Button saveButton;
    CardView uploadImage;
    TextView uploadText;
    Uri selectedImage;
    ActivityResultLauncher<String> imagePickerLauncher;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageRef;
    String imageUrl;



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
        uploadText = findViewById(R.id.upload_text);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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
                            uploadText.setText("Bild hochladen");
                        }
                    }
                }
        );

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateInput()) {
                    return;
                }

                if (selectedImage != null) {
                    uploadImage();
                } else {
                    savePasswordToFirebase(null);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
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
            return false;
        }
        return true;
    }

    private void uploadImage() {
        if (selectedImage == null) {
            savePasswordToFirebase(null);
            return;
        }

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
                                        imageUrl = uri.toString();
                                        savePasswordToFirebase(imageUrl);
                                    }
                                });
                    }
                });
    }

    private void savePasswordToFirebase(String imageUrl) {
        String title = titleInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("email", email);
        data.put("password", password);
        data.put("imageURL", imageUrl);

        db.collection("users")
                .document(uid)
                .collection("passwords")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AddPasswordActivity.this, "Passwort gespeichert!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}