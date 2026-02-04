package com.example.vaultguard;

import android.util.Base64;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

static final String secret_key = "VaultGuardKey123";

    public static String encrypt(String password) {
        try {
            Key key = new SecretKeySpec(secret_key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(password.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);

        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String encryptedPassword) {
        try {
            Key key = new SecretKeySpec(secret_key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decoded = Base64.decode(encryptedPassword, Base64.DEFAULT);
            byte[] original = cipher.doFinal(decoded);

            return new String(original);

        } catch (Exception e) {
            return null;
        }
    }
}