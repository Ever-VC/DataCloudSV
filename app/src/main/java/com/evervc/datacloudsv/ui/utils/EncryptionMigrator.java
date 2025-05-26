package com.evervc.datacloudsv.ui.utils;

import android.util.Base64;

import com.evervc.datacloudsv.models.AccountRegister;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.SecretKey;

public class EncryptionMigrator {

    /**
     * Re-cifra un registro con una nueva contraseña maestra.
     *
     * @param registro          Registro que se va a migrar.
     * @param oldMasterPassword Contraseña maestra actual (para descifrar).
     * @param newMasterPassword Nueva contraseña maestra (para re-cifrar).
     */
    public static void reencryptRegister(AccountRegister registro, String oldMasterPassword, String newMasterPassword) throws Exception {
        // Decodifica salt e IV antiguos
        byte[] oldSalt = CryptoUtils.decodeFromBase64(registro.getSaltBase64());
        byte[] oldIV = CryptoUtils.decodeFromBase64(registro.getIvBase64());

        // Deriva clave antigua y descifra la contraseña
        SecretKey oldKey = CryptoUtils.deriveKey(oldMasterPassword, oldSalt);
        String decryptedPassword = CryptoUtils.decrypt(registro.getPassword(), oldKey, oldIV);

        // Genera nuevos salt e IV
        byte[] newSalt = CryptoUtils.generateSalt();
        byte[] newIV = CryptoUtils.generateIV();

        // Deriva nueva clave y cifra la contraseña
        SecretKey newKey = CryptoUtils.deriveKey(newMasterPassword, newSalt);
        String encryptedPassword = CryptoUtils.encrypt(decryptedPassword, newKey, newIV);

        // Actualiza el registro con la nueva información cifrada
        registro.setPassword(encryptedPassword);
        registro.setSaltBase64(CryptoUtils.encodeToBase64(newSalt));
        registro.setIvBase64(CryptoUtils.encodeToBase64(newIV));
    }

}
