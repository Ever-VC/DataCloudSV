package com.evervc.datacloudsv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.R;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {
    private EditText etPasswordLogin;
    private Button btnLogin, btnIrRegistro;
    private static final String SALT = "salt_secure_password"; //No cambiar nombre, Es el mismo para verificar las contraseñas y si se cambia cambiarlo en RegisterActivity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindElementsXml();


        btnLogin.setOnClickListener(v -> {
            String password = etPasswordLogin.getText().toString();

            try {
                SecretKey secretKey = generateKey(password);

                String encryptedData = getSharedPreferences("vault_prefs", MODE_PRIVATE)
                        .getString("enc_val", null);

                if (encryptedData == null) {
                    Toast.makeText(this, "No se encontró cuenta registrada", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Aqui descrifamos nuestra contraseña
                String decrypted = decrypt(encryptedData, secretKey);

                // Si la contraseña es correcta, logueamos al usuario
                if ("clave_valida".equals(decrypted)) {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void bindElementsXml() {
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);


    }
    //  usamos el metodo de derivacion de clave para generar una clave de 256 bits usando PBKDF2
    private SecretKey generateKey(String password) throws Exception {
        byte[] saltBytes = SALT.getBytes();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 10000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    //  Descifra la clave usando AES/GCM
    private String decrypt(String encryptedData, SecretKey key) throws Exception {
        byte[] data = Base64.decode(encryptedData, Base64.DEFAULT);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte[] initializationVector  = new byte[12]; // 🧊 Leemos el initializationVector del principio
        buffer.get(initializationVector);

        byte[] cipherText = new byte[buffer.remaining()];
        buffer.get(cipherText);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, initializationVector);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }

}