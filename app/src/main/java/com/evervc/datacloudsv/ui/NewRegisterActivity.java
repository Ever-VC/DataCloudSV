package com.evervc.datacloudsv.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.ui.utils.ActivityTransitionUtil;
import com.google.android.material.appbar.MaterialToolbar;

public class NewRegisterActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_register);

        toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Nuevo Registro");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ActivityTransitionUtil.applyBackTransition(this);

    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_register_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Cierra la actividad
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        } /*else if (item.getItemId() == R.id.btnSaveRegister) {
            Toast.makeText(this, "Se ha guardado el registro...", Toast.LENGTH_SHORT).show();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void addRegister(View view) {
        Toast.makeText(this, "Se ha guardado el registro...", Toast.LENGTH_SHORT).show();
    }
}