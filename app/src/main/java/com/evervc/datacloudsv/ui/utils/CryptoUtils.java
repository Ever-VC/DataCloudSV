package com.evervc.datacloudsv.ui.utils;
import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static final String KEY_DERIVATION_FUNCTION = "PBKDF2WithHmacSHA256";
    private static final int KEY_SIZE = 256; // bits
    private static final int ITERATIONS = 65536;
    private static final int SALT_LENGTH = 16; // bytes
    private static final int IV_LENGTH = 16;   // bytes (AES block size)

    // Genera un salt aleatorio
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Genera un IV aleatorio
    public static byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    // Deriva una clave AES desde la contraseña y salt
    public static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_FUNCTION);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Cifra un texto plano
    public static String encrypt(String plainText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    // Descifra un texto cifrado
    public static String decrypt(String cipherText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decoded = Base64.decode(cipherText, Base64.NO_WRAP);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }

    // Utilidades para codificar y decodificar salt/iv a texto
    public static String encodeToBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static byte[] decodeFromBase64(String data) {
        return Base64.decode(data, Base64.NO_WRAP);
    }
}

