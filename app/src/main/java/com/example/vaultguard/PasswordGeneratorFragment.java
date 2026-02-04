package com.example.vaultguard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

public class PasswordGeneratorFragment extends Fragment {

    CheckBox checkUppercase;
    CheckBox checkLowercase;
    CheckBox checkNumbers;
    CheckBox checkSymbols;

    SeekBar lengthSeekbar;
    TextView lengthValue;
    TextView generatedPassword;

    Button generateButton;
    Button copyButton;

    int length;

    public PasswordGeneratorFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_generator, container, false);

        checkUppercase = view.findViewById(R.id.check_uppercase);
        checkLowercase = view.findViewById(R.id.check_lowercase);
        checkNumbers = view.findViewById(R.id.check_numbers);
        checkSymbols = view.findViewById(R.id.check_symbols);

        lengthSeekbar = view.findViewById(R.id.length_seekbar);
        lengthValue = view.findViewById(R.id.length_value);
        generatedPassword = view.findViewById(R.id.generated_password);

        generateButton = view.findViewById(R.id.generate_button);
        copyButton = view.findViewById(R.id.copy_button);

        length = 8 + lengthSeekbar.getProgress();
        lengthValue.setText(length + " Zeichen");

        lengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                length = 8 + progress;
                lengthValue.setText(length + " Zeichen");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = generatePassword(length);
                if (pw == null) {
                    Toast.makeText(getContext(), "Bitte mindestens eine Option auswählen", Toast.LENGTH_SHORT).show();
                    return;
                }
                generatedPassword.setText(pw);
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = generatedPassword.getText().toString();
                if (text.trim().isEmpty() || text.equals("— — — — — —")) {
                    Toast.makeText(getContext(), "Bitte zuerst ein Passwort generieren", Toast.LENGTH_SHORT).show();
                    return;
                }
                copyToClipboard(text);
            }
        });

        return view;
    }

    private String generatePassword(int length) {

        boolean useUppercase = checkUppercase.isChecked();
        boolean useLowercase = checkLowercase.isChecked();
        boolean useNumbers = checkNumbers.isChecked();
        boolean useSymbols = checkSymbols.isChecked();

        if (!useUppercase && !useLowercase && !useNumbers && !useSymbols) {
            return null;
        }

        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{};:,.<>?/";

        SecureRandom random = new SecureRandom();
        StringBuilder pool = new StringBuilder();
        StringBuilder result = new StringBuilder();


        if (useUppercase) {
            pool.append(upperCase);
            result.append(upperCase.charAt(random.nextInt(upperCase.length())));
        }
        if (useLowercase) {
            pool.append(lowerCase);
            result.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        }
        if (useNumbers) {
            pool.append(numbers);
            result.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        if (useSymbols) {
            pool.append(symbols);
            result.append(symbols.charAt(random.nextInt(symbols.length())));
        }

        while (result.length() < length) {
            result.append(pool.charAt(random.nextInt(pool.length())));
        }

        return result.toString();
    }

    private void copyToClipboard(String text) {

        ClipboardManager clipboard =
                (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("password", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(requireContext(), "Passwort kopiert", Toast.LENGTH_SHORT).show();
    }
}
