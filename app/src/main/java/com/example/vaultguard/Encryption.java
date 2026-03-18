package com.example.vaultguard;

import android.util.Base64;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    // Fester Schlüssel für die symmetrische Verschlüsselung (AES)
    static final String secret_key = "VaultGuardKey123";

    // Verschlüsselt ein Passwort vor dem Speichern in Firebase
    public static String encrypt(String password) {
        try {
            // Schlüssel aus String erzeugen
            Key key = new SecretKeySpec(secret_key.getBytes(), "AES");

            // AES-Verschlüsselungsinstanz erstellen
            Cipher cipher = Cipher.getInstance("AES");

            // Cipher in Verschlüsselungsmodus setzen
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // Klartext-Passwort -> verschlüsselte Bytes umwandeln
            byte[] encrypted = cipher.doFinal(password.getBytes());

            // Bytes -> Base64-String (damit Firebase es speichern kann)
            return Base64.encodeToString(encrypted, Base64.DEFAULT);

        } catch (Exception e) {
            return null;
        }
    }

    // Entschlüsselt ein Passwort nach dem Laden aus Firebase
    public static String decrypt(String encryptedPassword) {
        try {
            // Schlüssel erneut erzeugen (muss identisch sein)
            Key key = new SecretKeySpec(secret_key.getBytes(), "AES");

            // AES-Cipher erstellen
            Cipher cipher = Cipher.getInstance("AES");

            // Cipher in Entschlüsselungsmodus setzen
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Base64-String -> verschlüsselte Bytes zurückwandeln
            byte[] decoded = Base64.decode(encryptedPassword, Base64.DEFAULT);

            // Verschlüsselte Bytes -> originale Bytes (Passwort)
            byte[] original = cipher.doFinal(decoded);

            // Bytes -> lesbarer String (Klartext-Passwort)
            return new String(original);

        } catch (Exception e) {
            return null;
        }
    }
}