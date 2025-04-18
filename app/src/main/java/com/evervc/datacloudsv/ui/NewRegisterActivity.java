package com.evervc.datacloudsv.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.ActivityTransitionUtil;
import com.evervc.datacloudsv.ui.utils.IAccountRegisterListener;
import com.google.android.material.appbar.MaterialToolbar;

public class NewRegisterActivity extends AppCompatActivity {
    private EditText etTitleNewRegister, etAccountNewRegister, etUsernameNewRegister, etPasswordNewRegister, etWebSiteNewRegister, etNotesNewRegister;
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

        bindElementsXml();

    }

    private void bindElementsXml() {
        etTitleNewRegister = findViewById(R.id.etTitleNewRegister);
        etAccountNewRegister = findViewById(R.id.etAccountNewRegister);
        etUsernameNewRegister = findViewById(R.id.etUsernameNewRegister);
        etPasswordNewRegister = findViewById(R.id.etPasswordNewRegister);
        etWebSiteNewRegister = findViewById(R.id.etWebSiteNewRegister);
        etNotesNewRegister = findViewById(R.id.etNotesNewRegister);
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
        String title = etTitleNewRegister.getText().toString().trim();
        String accountInput = etAccountNewRegister.getText().toString().trim();
        String username = etUsernameNewRegister.getText().toString().trim();
        String password = etPasswordNewRegister.getText().toString().trim();
        String websiteInput = etWebSiteNewRegister.getText().toString().trim();
        String notesInput = etNotesNewRegister.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Asegúrese de haber llenado los campos que tienen un [*]", Toast.LENGTH_SHORT).show();
            return;
        }

        // Asigna null si los campos opcionales están vacíos
        String account = TextUtils.isEmpty(accountInput) ? null : accountInput;
        String website = TextUtils.isEmpty(websiteInput) ? null : websiteInput;
        String notes = TextUtils.isEmpty(notesInput) ? null : notesInput;

        AccountRegister accountRegister = new AccountRegister(title, account, username, password, website, notes, System.currentTimeMillis(), null);

        // Aquí deberías guardar el objeto con Room (no mostrado)
        AccountRegisterControllerDB.insertAccountRegister(this, accountRegister);
    }

}