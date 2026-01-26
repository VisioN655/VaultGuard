package com.example.vaultguard;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.*;
import com.google.firebase.firestore.*;


public class PasswordListFragment extends Fragment {

    LinearLayout passwordContainer;
    LayoutInflater cardInflater;
    MaterialCardView passwordCard;
    TextView emptyText;
    TextView card_title;
    TextInputEditText card_email;
    TextInputEditText card_password;
    ImageView card_image;
    View view;
    View card;

    public PasswordListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_password_list, container, false);
        passwordContainer = view.findViewById(R.id.password_container);
        emptyText = view.findViewById(R.id.empty_list);
        cardInflater = LayoutInflater.from(getContext());

        for (int i = 0; i < 5; i++) {
            card = cardInflater.inflate(R.layout.item_password, passwordContainer, false);
            passwordCard = card.findViewById(R.id.password_card);
            card_title = card.findViewById(R.id.platform_title);
            card_email = card.findViewById(R.id.item_email_input);
            card_password = card.findViewById(R.id.item_password_input);
            card_image = card.findViewById(R.id.platform_icon);

            passwordCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Card klick!", Toast.LENGTH_SHORT).show();
                }
            });
            passwordContainer.addView(card);
        }
        if (passwordContainer.getChildCount() > 0) {
            emptyText.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.VISIBLE);
        }
        return view;
    }
}