package com.evervc.datacloudsv.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.ui.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private boolean doubleTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindElementsXml();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onSelectedItem(item);
            }
        });
    }

    private void bindElementsXml() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private boolean onSelectedItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnHome:
                chnageFragment(new HomeFragment());
                return true;
            case R.id.btnSettings:
                Toast.makeText(HomeActivity.this, "Módulo en desarrollo...", Toast.LENGTH_SHORT).show();
                return false;
            case R.id.btnInfo:
                Toast.makeText(HomeActivity.this, "Módulo en desarrollo...", Toast.LENGTH_SHORT).show();
                return false;
            case R.id.btnLogout:
                logOut();
                return false;
        }
        return  false;
    }

    private void chnageFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fcvContainer, fragment).commit();
    }

    private void logOut() {
        AlertDialog.Builder mensajeDeConfirmacion = new AlertDialog.Builder(this);
        mensajeDeConfirmacion.setTitle("¿Está seguro que desea salir?");
        mensajeDeConfirmacion.setMessage("Esta acción cerrará completamente la aplicación.");
        mensajeDeConfirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
                System.exit(0);
            }
        }).setNegativeButton("No", null).show();
    }

    @Override
    public void onBackPressed() {
        if (doubleTouch) {
            super.onBackPressed();
            return;
        }

        this.doubleTouch = true;
        Toast.makeText(this, "Precione dos veces para cerrar la app", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleTouch = false;
            }
        }, 2000);
    }
}