package com.evervc.datacloudsv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.ui.HomeActivity;
import com.evervc.datacloudsv.ui.LoginActivity;
import com.evervc.datacloudsv.ui.RegisterActivity;
import com.evervc.datacloudsv.ui.utils.ThemeHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        int theme = new ThemeHelper(this).getSelectedTheme();
        new ThemeHelper(this).setSelectedTheme(theme);
        loadSplash();
    }

    private void loadSplash() {
        new Handler().postDelayed(() -> {
            // Revisamos si ya hay un hash guardado (usuario registrado)
            SharedPreferences prefs = getSharedPreferences("AppData", MODE_PRIVATE);
            String hashedPassword = prefs.getString("hashedPassword", null);

            if (hashedPassword != null) {
                // Si hay un usuario registrado, de un solo al Login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                // No hay un usuario registrado, de un solo al Registro
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
            finish();
        }, 2500);
    }
}