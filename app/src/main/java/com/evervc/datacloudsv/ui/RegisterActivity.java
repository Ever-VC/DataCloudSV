package com.evervc.datacloudsv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.R;

import org.mindrot.jbcrypt.BCrypt;

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
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bindElementsXml();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPasswordRegister.getText().toString().trim();
                String confirmPassword = etConfirmPasswordRegister.getText().toString().trim();

                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Ambos campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPasswordStrong(password)) {
                    Toast.makeText(RegisterActivity.this,
                            "La contraseña debe tener mínimo 8 caracteres, incluir mayúsculas, minúsculas, números y símbolos.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Hasheamos la contraseña antes de guardarla
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                // Guardar la contraseña hasheada en SharedPreferences
                getSharedPreferences("AppData", MODE_PRIVATE)
                        .edit()
                        .putString("hashedPassword", hashedPassword)
                        .apply();

                Toast.makeText(RegisterActivity.this, "Cuenta registrada con éxito, bienvenido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void bindElementsXml() {
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPasswordRegister = findViewById(R.id.etConfirmPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);

    }

    // Método de validación de contraseña segura
    private boolean isPasswordStrong(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";
        return password.matches(regex);
    }
}

