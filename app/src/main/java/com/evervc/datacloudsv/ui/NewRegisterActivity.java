package com.evervc.datacloudsv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.ActivityTransitionUtil;
import com.evervc.datacloudsv.ui.utils.CryptoUtils;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.crypto.SecretKey;

public class NewRegisterActivity extends AppCompatActivity {
    private EditText etTitleNewRegister, etAccountNewRegister, etUsernameNewRegister, etPasswordNewRegister, etWebSiteNewRegister, etNotesNewRegister;
    private Button btnAddNewRegister;
    private MaterialToolbar toolbar;
    private int accountRegisterEditId = -1;
    private AccountRegister accountRegisterUpdate = null;

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

        accountRegisterEditId = getIntent().getIntExtra("idAccountRegister", -1);
        bindElementsXml();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Nuevo Registro");
            if (accountRegisterEditId != -1) {
                actionBar.setTitle("Actualizar Registro");
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ActivityTransitionUtil.applyBackTransition(this);

        if (accountRegisterEditId != -1) {
            // Extrae la información desde la base de datos y la muestra
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                AccountRegistersDB db = AccountRegistersDB.getInstance(this);
                AccountRegister accountRegister = db.accountRegisterDAO().getAccountRegisterById(accountRegisterEditId);
                if (accountRegister != null) {
                    accountRegisterUpdate = accountRegister;

                    // Descifrar la contraseña
                    byte[] recoveredSalt = CryptoUtils.decodeFromBase64(accountRegister.getSaltBase64());
                    byte[] recoveredIv = CryptoUtils.decodeFromBase64(accountRegister.getIvBase64());
                    try {
                        SecretKey recoveredKey = CryptoUtils.deriveKey("miClaveMaestra123", recoveredSalt);
                        String passwordCracked = CryptoUtils.decrypt(accountRegister.getPassword(), recoveredKey, recoveredIv);

                        runOnUiThread(() -> {
                            etTitleNewRegister.setText(accountRegister.getTitle());
                            etAccountNewRegister.setText(accountRegister.getAcount());
                            etUsernameNewRegister.setText(accountRegister.getUsername());
                            etPasswordNewRegister.setText(passwordCracked);
                            etWebSiteNewRegister.setText(accountRegister.getWebsite());
                            etNotesNewRegister.setText(accountRegister.getNotes());
                            btnAddNewRegister.setText("Actualizar");
                            btnAddNewRegister.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_edit_24, 0, 0 ,0);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error al descifrar la contraseña.", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                } else {
                    Toast.makeText(this, "Parece que ha ocurrido un error, por favor inténtelo nuevamente.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }

    }

    private void bindElementsXml() {
        etTitleNewRegister = findViewById(R.id.etTitleNewRegister);
        etAccountNewRegister = findViewById(R.id.etAccountNewRegister);
        etUsernameNewRegister = findViewById(R.id.etUsernameNewRegister);
        etPasswordNewRegister = findViewById(R.id.etPasswordNewRegister);
        etWebSiteNewRegister = findViewById(R.id.etWebSiteNewRegister);
        etNotesNewRegister = findViewById(R.id.etNotesNewRegister);
        btnAddNewRegister = findViewById(R.id.btnAddNewRegister);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Cierra la actividad
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addRegister(View view) {
        // Almacena todos los datos en variables temporales
        String title = etTitleNewRegister.getText().toString().trim();
        String accountInput = etAccountNewRegister.getText().toString().trim();
        String username = etUsernameNewRegister.getText().toString().trim();
        String password = etPasswordNewRegister.getText().toString().trim();
        String websiteInput = etWebSiteNewRegister.getText().toString().trim();
        String notesInput = etNotesNewRegister.getText().toString().trim();

        // Valida que todos los campos obligatorios estén completamente llenos
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Asegúrese de haber llenado los campos que tienen un [*]", Toast.LENGTH_SHORT).show();
            return;
        }

        // Asigna null si los campos opcionales están vacíos
        String account = TextUtils.isEmpty(accountInput) ? null : accountInput;
        String website = TextUtils.isEmpty(websiteInput) ? null : websiteInput;
        String notes = TextUtils.isEmpty(notesInput) ? null : notesInput;

        // Genera salt e IV aleatorios
        byte[] salt = CryptoUtils.generateSalt();
        byte[] iv = CryptoUtils.generateIV();

        String encryptedPassword;
        String saltBase64;
        String ivBase64;

        try {
            // Derivar la clave desde la contraseña maestra usando SharedPreferences (pronto a agregar)
            SecretKey key = CryptoUtils.deriveKey("miClaveMaestra123", salt);

            // Cifra la contraseña
            encryptedPassword = CryptoUtils.encrypt(password, key, iv);

            // Codifica salt e IV
            saltBase64 = CryptoUtils.encodeToBase64(salt);
            ivBase64 = CryptoUtils.encodeToBase64(iv);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cifrar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea un objeto desde el contructor, enviando los datos obtenidos y filtrados
        AccountRegister accountRegister = new AccountRegister(title, account, username, encryptedPassword, website, notes, System.currentTimeMillis(), null, saltBase64, ivBase64);

        // Manda a llamar la función que almacena o actualiza el registro
        if (accountRegisterUpdate != null) {
            accountRegister.setId(accountRegisterUpdate.getId());
            accountRegister.setModifiedAt(System.currentTimeMillis());
            AccountRegisterControllerDB.updateAccountRegister(this, accountRegister);
        } else {
            AccountRegisterControllerDB.insertAccountRegister(this, accountRegister);
        }
    }

}