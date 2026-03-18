package com.example.vaultguard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.bumptech.glide.Glide;


public class PasswordListFragment extends Fragment {

    // Container für alle Passwort-Karten
    LinearLayout passwordContainer;

    // Inflater zum dynamischen Erstellen von Karten
    LayoutInflater cardInflater;

    // "Liste leer"-Text
    TextView emptyText;

    // UI-Elemente einer einzelnen Karte
    MaterialCardView passwordCard;
    TextView card_title;
    TextInputEditText card_email;
    TextInputEditText card_password;
    ImageView card_image;
    TextView placeholderLetter;

    View view;
    View card;

    // Firebase
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    String uid;

    public PasswordListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Firebase initialisieren
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();

        // Fragment-Layout laden
        view = inflater.inflate(R.layout.fragment_password_list, container, false);

        // UI-Elemente verbinden
        passwordContainer = view.findViewById(R.id.password_container);
        emptyText = view.findViewById(R.id.empty_list);

        // Inflater für Karten
        cardInflater = LayoutInflater.from(getContext());

        return view;
    }

    // Wird aufgerufen wenn Fragment sichtbar wird -> lädt Daten neu
    @Override
    public void onResume() {
        super.onResume();
        loadPasswords();
    }

    // Lädt alle Passwörter aus Firebase
    private void loadPasswords() {

        // Alte Karten entfernen
        passwordContainer.removeAllViews();

        db.collection("users")
                .document(uid)
                .collection("passwords")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // Fehlerfall → leere Liste anzeigen
                        if (!task.isSuccessful()) {
                            emptyText.setVisibility(View.VISIBLE);
                            return;
                        }

                        if(task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();

                            // Alle Dokumente durchgehen
                            for (int i = 0; i < query.getDocuments().size(); i++) {
                                DocumentSnapshot doc = query.getDocuments().get(i);

                                // Neue Karte aus XML erstellen
                                card = cardInflater.inflate(R.layout.item_password, passwordContainer, false);

                                // UI-Elemente der Karte verbinden
                                passwordCard = card.findViewById(R.id.password_card);
                                card_title = card.findViewById(R.id.platform_title);
                                card_email = card.findViewById(R.id.item_email_input);
                                card_password = card.findViewById(R.id.item_password_input);
                                card_image = card.findViewById(R.id.platform_icon);
                                placeholderLetter = card.findViewById(R.id.placeholder_letter);

                                // Daten aus Firestore holen
                                String title = doc.getString("title");
                                String email = doc.getString("email");
                                String encryptedPassword = doc.getString("password");
                                String imageURL = doc.getString("imageURL");

                                // Passwort entschlüsseln
                                String decryptedPassword = Encryption.decrypt(encryptedPassword);

                                // Daten in UI setzen
                                card_title.setText(title);
                                card_email.setText(email);
                                card_password.setText(decryptedPassword);

                                // Bild laden (Glide)
                                Glide.with(PasswordListFragment.this)
                                        .load(imageURL)
                                        .placeholder(R.drawable.rounded_rectangle_bg)
                                        .error(R.drawable.rounded_rectangle_bg)
                                        .into(card_image);

                                // Falls kein Bild vorhanden -> ersten Buchstaben anzeigen
                                if (imageURL == null) {
                                    placeholderLetter.setVisibility(View.VISIBLE);
                                    placeholderLetter.setText(getFirstLetter(title));
                                } else {
                                    placeholderLetter.setVisibility(View.GONE);
                                }

                                // Klick auf Karte -> Detailansicht öffnen
                                passwordCard.setOnClickListener(new View.OnClickListener() {
                                    Intent detail_password = new Intent(requireContext(), DetailPasswordActivity.class);

                                    @Override
                                    public void onClick(View v) {
                                        detail_password.putExtra("docId", doc.getId());
                                        startActivity(detail_password);
                                    }
                                });

                                // Karte zum Container hinzufügen
                                passwordContainer.addView(card);
                            }
                        }
                    }
                });
    }

    // Gibt den ersten Buchstaben eines Textes zurück (für Placeholder)
    private String getFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase();
    }
}