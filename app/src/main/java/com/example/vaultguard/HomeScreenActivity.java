package com.example.vaultguard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HomeScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
            .beginTransaction()
                    .replace(R.id.fragment_container, new PasswordListFragment())
                    .commit();
        }
    }
}
