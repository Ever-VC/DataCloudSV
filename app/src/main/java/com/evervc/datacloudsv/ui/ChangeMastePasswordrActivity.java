package com.evervc.datacloudsv.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.utils.EncryptionMigrator;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangeMastePasswordrActivity extends AppCompatActivity {
    private EditText etOldPassword,etNewPassword, etConfirmNewPassword;
    private Button btnChangePassword, btnCancelPasswordChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_maste_passwordr);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindXmlElements();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String newConfirmPassword = etConfirmNewPassword.getText().toString();
                
                if (oldPassword.isBlank()) {
                    Toast.makeText(ChangeMastePasswordrActivity.this, "Para realizar el cambio debe ingresar su contraseña antigua.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.isBlank()) {
                    Toast.makeText(ChangeMastePasswordrActivity.this, "Por favor ingrese la nueva contraseña.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(newConfirmPassword)) {
                    Toast.makeText(ChangeMastePasswordrActivity.this, "La nueva contraseña no coincide con la confirmación.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtener el hash almacenado de SharedPreferences
                SharedPreferences prefs = getSharedPreferences("AppData", Context.MODE_PRIVATE);
                String storedHash = prefs.getString("hashedPassword", null);

                if (storedHash != null && BCrypt.checkpw(oldPassword, storedHash)) {
                    // Pregunta al usuario si esta seguro de cambiar la contraseña maestra
                    AlertDialog.Builder mensajeDeConfirmacion = new AlertDialog.Builder(ChangeMastePasswordrActivity.this);
                    mensajeDeConfirmacion.setTitle("¿Está seguro que desea cambiar la contraseña?");
                    mensajeDeConfirmacion.setMessage("Asegurese de recordar su nueva contraseña, si es posible almacenarla en un lugar seguro. Al aceptar, la aplicación se cerrará para cargar los cambios.");
                    mensajeDeConfirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());

                            executor.execute(() -> {
                                try {
                                    // Obtener registros de la base de datos
                                    AccountRegistersDB db = AccountRegistersDB.getInstance(getApplicationContext());
                                    List<AccountRegister> registros = db.accountRegisterDAO().getAllAccountRegisters();


                                    // Guardar el nuevo hash de la contraseña maestra
                                    String nuevoHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                                    getSharedPreferences("AppData", MODE_PRIVATE)
                                            .edit()
                                            .putString("hashedPassword", nuevoHash)
                                            .apply();

                                    // Recifrar cada registro
                                    for (AccountRegister registro : registros) {
                                        EncryptionMigrator.reencryptRegister(registro, storedHash, nuevoHash);
                                        db.accountRegisterDAO().updateRegister(registro);
                                    }

                                    // Reiniciar la app para forzar nuevo login
                                    handler.post(() -> {
                                        Toast.makeText(ChangeMastePasswordrActivity.this, "Contraseña cambiada correctamente. La app se cerrará...", Toast.LENGTH_LONG).show();
                                        finishAffinity();
                                        System.exit(0);
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    handler.post(() -> Toast.makeText(ChangeMastePasswordrActivity.this, "Error al cambiar contraseña", Toast.LENGTH_LONG).show());
                                }
                            });
                        }
                    }).setNegativeButton("No", null).show();


                } else {
                    Toast.makeText(ChangeMastePasswordrActivity.this, "La contraseña antigua es incorrecta", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnCancelPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindXmlElements() {
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnCancelPasswordChange = findViewById(R.id.btnCancelPasswordChange);
    }
}