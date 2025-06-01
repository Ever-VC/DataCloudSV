package com.evervc.datacloudsv.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.ChangeMastePasswordrActivity;
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.EncryptionMigrator;
import com.evervc.datacloudsv.ui.utils.ThemeHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigFragment extends Fragment {
    private MaterialButtonToggleGroup theme_toggle;
    private MaterialCardView cvDeleteAllRegisters, cvExportCSVFile, cvImportCSVFile, cvChangePassword;
    private static final int CREATE_CSV_FILE_REQUEST_CODE = 1001;
    private static final int IMPORT_CSV_FILE_REQUEST_CODE = 2001;
    private boolean isEmpty = false;

    public ConfigFragment() {
        // Required empty public constructor
    }

    public static ConfigFragment newInstance(String param1, String param2) {
        ConfigFragment fragment = new ConfigFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        bindXmlElements(view);

        setTheme();

        cvDeleteAllRegisters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Elimina todos los registros
                AccountRegisterControllerDB.deleteAllAccountRegisters(getContext());
            }
        });

        cvExportCSVFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exporta los registros y la clave maestra a un CSV
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executorService.execute(() -> {
                    AccountRegistersDB db = AccountRegistersDB.getInstance(getContext());
                    List<AccountRegister> registros = db.accountRegisterDAO().getAllAccountRegisters();
                    if (registros.isEmpty()) {
                        handler.post(() -> Toast.makeText(getContext(), "No hay ningún registro para exportar", Toast.LENGTH_SHORT).show());
                    } else {
                        exportToCSVFile();
                    }
                });
            }
        });

        cvImportCSVFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Importa desde un archivo CSV los registros
                importFromCSVFile();
            }
        });

        cvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hace cambio de contraaseña maestra
                Intent changePwd = new Intent(getContext(), ChangeMastePasswordrActivity.class);
                startActivity(changePwd);
            }
        });

        return view;
    }

    private void bindXmlElements(View view) {
        theme_toggle = view.findViewById(R.id.theme_toggle);
        cvDeleteAllRegisters = view.findViewById(R.id.cvDeleteAllRegisters);
        cvExportCSVFile = view.findViewById(R.id.cvExportCSVFile);
        cvImportCSVFile = view.findViewById(R.id.cvImportCSVFile);
        cvChangePassword = view.findViewById(R.id.cvChangePassword);
    }

    private void setTheme() {
        theme_toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnLight) {
                    new ThemeHelper(getActivity()).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.btnDark) {
                    new ThemeHelper(getActivity()).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    new ThemeHelper(getActivity()).setSelectedTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            }
        });

        int theme = new ThemeHelper(getActivity()).getSelectedTheme();

        // Select toggle btn
        theme_toggle.check(theme == AppCompatDelegate.MODE_NIGHT_NO ? R.id.btnLight :
                theme == AppCompatDelegate.MODE_NIGHT_YES ? R.id.btnDark :
                        R.id.btnSystem);
    }

    private void exportToCSVFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "RegistrosDataCloudSV.csv");
        startActivityForResult(intent, CREATE_CSV_FILE_REQUEST_CODE);
    }

    private void writeCSVToUri(Uri uri) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(getContext());
            List<AccountRegister> registros = db.accountRegisterDAO().getAllAccountRegisters();

            try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri);
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {

                String hashedPassword = getCurrentMasterPassword();
                writer.append("#HASH=").append(hashedPassword).append("\n");

                writer.append("Titulo,Cuenta,Usuario,ContraseñaCifrada,SitioWeb,Notas,FechaCreacion,FechaActualizacion,Salt,IV\n");

                for (AccountRegister registro : registros) {
                    writer.append(escapeCSV(registro.getTitle())).append(",");
                    writer.append(escapeCSV(registro.getAcount())).append(",");
                    writer.append(escapeCSV(registro.getUsername())).append(",");
                    writer.append(escapeCSV(registro.getPassword())).append(",");
                    writer.append(escapeCSV(registro.getWebsite())).append(",");
                    writer.append(escapeCSV(registro.getNotes())).append(",");
                    writer.append(escapeCSV(registro.getCreatedAt() != null ? registro.getCreatedAt().toString() : null)).append(",");
                    writer.append(escapeCSV(registro.getModifiedAt() != null ? registro.getModifiedAt().toString() : null)).append(",");
                    writer.append(escapeCSV(registro.getSaltBase64())).append(",");
                    writer.append(escapeCSV(registro.getIvBase64())).append("\n");
                }

                writer.flush();
                handler.post(() -> Toast.makeText(getContext(), "Archivo exportado exitosamente", Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                Log.e("ExportCSV", "Error al escribir archivo CSV", e);
                handler.post(() -> Toast.makeText(getContext(), "Error al exportar registros", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String escapeCSV(String value) {
        if (value == null) return "__NULL__"; // Marcador especial para null
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            escaped = "\"" + escaped + "\"";
        }
        return escaped;
    }

    private void importFromCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Acepta cualquier tipo
        startActivityForResult(Intent.createChooser(intent, "Selecciona el archivo CSV"), IMPORT_CSV_FILE_REQUEST_CODE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            if (requestCode == CREATE_CSV_FILE_REQUEST_CODE) {
                writeCSVToUri(uri);
            } else if (requestCode == IMPORT_CSV_FILE_REQUEST_CODE) {
                if (uri.toString().endsWith(".csv")) {
                    importCSVAndReencrypt(uri); // sin pedir clave
                } else {
                    Toast.makeText(getContext(), "Por favor selecciona un archivo CSV", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void importCSVAndReencrypt(Uri uri) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String line;
                String storedHash = null;
                boolean isFirstLine = true;
                boolean isHeaderLine = true;
                AccountRegistersDB db = AccountRegistersDB.getInstance(getContext());
                String newHash = getCurrentMasterPassword(); // hash actual

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        if (line.startsWith("#HASH=")) {
                            storedHash = line.substring(6);
                            continue;
                        } else {
                            handler.post(() -> Toast.makeText(getContext(), "Archivo inválido: falta hash", Toast.LENGTH_LONG).show());
                            return;
                        }
                    }

                    if (isHeaderLine) {
                        isHeaderLine = false;
                        continue;
                    }

                    String[] tokens = parseCSVLine(line);
                    if (tokens.length < 10) continue;

                    AccountRegister reg = new AccountRegister();
                    reg.setTitle(parseNull(tokens[0]));
                    reg.setAcount(parseNull(tokens[1]));
                    reg.setUsername(parseNull(tokens[2]));
                    reg.setPassword(tokens[3]);
                    reg.setWebsite(parseNull(tokens[4]));
                    reg.setNotes(parseNull(tokens[5]));
                    reg.setCreatedAt(parseLongOrNull(tokens[6]));
                    reg.setModifiedAt(parseLongOrNull(tokens[7]));
                    reg.setSaltBase64(tokens[8]);
                    reg.setIvBase64(tokens[9]);

                    try {
                        EncryptionMigrator.reencryptRegister(reg, storedHash, newHash);
                        db.accountRegisterDAO().insertNewRegister(reg);
                    } catch (Exception e) {
                        Log.e("ImportCSV", "Error al migrar registro", e);
                    }
                }

                handler.post(() -> Toast.makeText(getContext(), "Importación completada", Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                Log.e("ImportCSV", "Error al importar", e);
                handler.post(() -> Toast.makeText(getContext(), "Error al importar CSV", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private String parseNull(String value) {
        return "__NULL__".equals(value) ? null : value;
    }

    private String[] parseCSVLine(String line) {
        // Maneja comillas, comas y saltos de línea dentro de campos correctamente
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    private String getCurrentMasterPassword() {
        // Obtiene la contraseña actual
         SharedPreferences prefs = requireContext().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        return prefs.getString("hashedPassword", null);
    }

    private Long parseLongOrNull(String value) {
        try {
            return "__NULL__".equals(value) ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}