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
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class RegisterActivity extends AppCompatActivity {
    private EditText etPasswordRegister, etConfirmPasswordRegister;
    private Button btnRegister, btnIrLogin;
    private static final String SALT = "salt_secure_password"; // No cambiar nombre, Es el mismo para verificar las contraseñas y si se cambia cambiarlo en LoginActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindElementsXml();

        btnRegister.setOnClickListener(v -> {
            String password = etPasswordRegister.getText().toString();
            String confirm = etConfirmPasswordRegister.getText().toString();

            // Verificacion de contraseñas iguales
            if (!password.equals(confirm)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // Derivamos la clave
                SecretKey secretKey = generateKey(password);

                // Ciframos un texto de validación
                String encrypted = encrypt("clave_valida", secretKey);

                // Guardamos en SharedPreferences
                getSharedPreferences("vault_prefs", MODE_PRIVATE)
                        .edit()
                        .putString("enc_val", encrypted)
                        .apply();

                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void bindElementsXml() {
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPasswordRegister = findViewById(R.id.etConfirmPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);

    }

        //  usamos el metodo de derivacion de clave para generar una clave de 256 bits usando PBKDF2
    private SecretKey generateKey(String password) throws Exception {
        byte[] saltBytes = SALT.getBytes();
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 10000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES"); // 🔐 AES usa esta clave
    }

    private String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] initializationVector = new byte[12]; // 12 bytes para GCM
        new SecureRandom().nextBytes(initializationVector ); // Generamos initializationVector aleatorio

        GCMParameterSpec spec = new GCMParameterSpec(128, initializationVector);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        //  Guardamos initializationVector + texto cifrado juntos en Base64
        ByteBuffer buffer = ByteBuffer.allocate(initializationVector.length + cipherText.length);
        buffer.put(initializationVector);
        buffer.put(cipherText);
        return Base64.encodeToString(buffer.array(), Base64.DEFAULT);
    }
}

