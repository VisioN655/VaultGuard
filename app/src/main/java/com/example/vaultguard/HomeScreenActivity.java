package com.example.vaultguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class HomeScreenActivity extends AppCompatActivity {
    MaterialCardView addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        addButton = findViewById(R.id.add_button);

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

    }
}
