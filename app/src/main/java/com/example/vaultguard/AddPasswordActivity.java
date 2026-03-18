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

import com.google.android.gms.tasks.OnSuccessListener;
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

    // UI-Elemente für Eingaben und Aktionen
    TextInputEditText titleInput;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    Button cancelButton;
    Button saveButton;
    CardView uploadImage;
    TextView uploadText;
    ImageView eyeView;

    // Bildauswahl
    Uri selectedImage;
    ActivityResultLauncher<String> imagePickerLauncher;

    // Firebase Instanzen
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageRef;
    String imageUrl;

    // Status für Passwort-Sichtbarkeit
    boolean isPasswordVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        // UI-Elemente initialisieren
        titleInput = findViewById(R.id.title_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
        uploadImage = findViewById(R.id.upload_border);
        uploadText = findViewById(R.id.upload_text);
        eyeView = findViewById(R.id.show_password);

        // Firebase initialisieren
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        isPasswordVisible = false;

        // Öffnet Galerie und erlaubt Bildauswahl
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

        // Speichern-Button: Validierung + Upload starten
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateInput()) {
                    return;
                }
                uploadImage();
            }
        });

        // Abbrechen → Activity schließen
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Klick auf Upload-Bereich öffnet Galerie
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        // Eye-Icon toggelt Passwort sichtbar/unsichtbar
        eyeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    // Prüft ob alle Pflichtfelder ausgefüllt sind
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

    // Lädt Bild zu Firebase Storage hoch (falls vorhanden)
    private void uploadImage() {
        if (selectedImage == null) {
            // Kein Bild → direkt speichern
            savePasswordToFirebase(null);
            return;
        }

        // Speicherpfad: users/{uid}/images/{timestamp}.jpg
        StorageReference imageRef = storageRef
                .child("users")
                .child(auth.getUid())
                .child("images")
                .child(System.currentTimeMillis() + ".jpg");

        // Bild hochladen
        imageRef.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Nach Upload URL holen
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

    // Speichert Passwort-Daten in Firestore
    private void savePasswordToFirebase(String imageUrl) {
        String title = titleInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Passwort wird vor dem Speichern verschlüsselt
        String encryptedPassword = Encryption.encrypt(password);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        // Daten als Map vorbereiten
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("email", email);
        data.put("password", encryptedPassword);
        data.put("imageURL", imageUrl);

        // In Firestore speichern (users/{uid}/passwords)
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

    // Schaltet Passwort zwischen sichtbar und versteckt um
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