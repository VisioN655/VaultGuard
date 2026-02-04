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

    LinearLayout passwordContainer;
    LayoutInflater cardInflater;
    MaterialCardView passwordCard;
    TextView emptyText;
    TextView card_title;
    TextInputEditText card_email;
    TextInputEditText card_password;
    ImageView card_image;
    TextView placeholderLetter;
    View view;
    View card;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    String uid;

    public PasswordListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();
        view = inflater.inflate(R.layout.fragment_password_list, container, false);
        passwordContainer = view.findViewById(R.id.password_container);
        emptyText = view.findViewById(R.id.empty_list);
        cardInflater = LayoutInflater.from(getContext());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPasswords();
    }

    private void loadPasswords() {
        passwordContainer.removeAllViews();
        db.collection("users")
                .document(uid)
                .collection("passwords")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.isSuccessful()) {
                            emptyText.setVisibility(View.VISIBLE);
                            return;
                        }

                        if(task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();

                            for (int i = 0; i < query.getDocuments().size(); i++) {
                                DocumentSnapshot doc = query.getDocuments().get(i);
                                card = cardInflater.inflate(R.layout.item_password, passwordContainer, false);
                                passwordCard = card.findViewById(R.id.password_card);
                                card_title = card.findViewById(R.id.platform_title);
                                card_email = card.findViewById(R.id.item_email_input);
                                card_password = card.findViewById(R.id.item_password_input);
                                card_image = card.findViewById(R.id.platform_icon);
                                placeholderLetter = card.findViewById(R.id.placeholder_letter);


                                String title = doc.getString("title");
                                String email = doc.getString("email");
                                String encryptedPassword = doc.getString("password");
                                String imageURL = doc.getString("imageURL");

                                String decryptedPassword = Encryption.decrypt(encryptedPassword);

                                card_title.setText(title);
                                card_email.setText(email);
                                card_password.setText(decryptedPassword);
                                Glide.with(PasswordListFragment.this)
                                        .load(imageURL)
                                        .placeholder(R.drawable.rounded_rectangle_bg)
                                        .error(R.drawable.rounded_rectangle_bg)
                                        .into(card_image);

                                if (imageURL == null) {
                                    placeholderLetter.setVisibility(View.VISIBLE);
                                    placeholderLetter.setText(getFirstLetter(title));
                                } else {
                                    placeholderLetter.setVisibility(View.GONE);
                                }

                                passwordCard.setOnClickListener(new View.OnClickListener() {
                                    Intent detail_password = new Intent(requireContext(), DetailPasswordActivity.class);
                                    @Override
                                    public void onClick(View v) {
                                        detail_password.putExtra("docId", doc.getId());
                                        startActivity(detail_password);
                                    }
                                });
                                passwordContainer.addView(card);
                            }
                        }
                    }
                });
    }

    private String getFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase();
    }
}