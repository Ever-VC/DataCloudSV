package com.evervc.datacloudsv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.ui.HomeActivity;
import com.evervc.datacloudsv.ui.LoginActivity;
import com.evervc.datacloudsv.ui.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadSplash();
    }

    private void loadSplash() {
        new Handler().postDelayed(() -> {
            //Verificamos si ya hay un usuario en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("vault_prefs", MODE_PRIVATE);
            String encryptedData = prefs.getString("enc_val", null);

            // Si ya hay un usuario, abrimos LoginActivity
            if (encryptedData != null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            // Si no hay un usuario, abrimos RegisterActivity
            else {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
            finish();
        }, 2500);
    }

}