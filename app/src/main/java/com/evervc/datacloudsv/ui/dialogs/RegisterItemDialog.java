package com.evervc.datacloudsv.ui.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.NewRegisterActivity;
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.ActivityTransitionUtil;
import com.evervc.datacloudsv.ui.utils.CryptoUtils;
import com.evervc.datacloudsv.ui.utils.IAccountRegisterListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;

public class RegisterItemDialog extends DialogFragment {
    private TextView tvRegisterItemTitle, tvEmail, tvUsername, tvPassword, tvWebsite, tvRegisterDate,tvModifiedDate, tvNotes;
    private ImageView ivHideDialogRegisterView, ivIcon;
    private Button btnDeleteRegister, btnUpdateRegister;
    private IAccountRegisterListener listener;
    private AccountRegister accountRegister;

    public RegisterItemDialog() {}

    public static RegisterItemDialog newInstance(int registerId) {
        RegisterItemDialog dialog = new RegisterItemDialog();
        Bundle args = new Bundle();
        args.putInt("registerId", registerId);
        dialog.setArguments(args);
        return dialog;
    }

    public void setListener(IAccountRegisterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Se define el tamaño del dialogo como el 90% del alto y ancho de la pantalla
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().setGravity(Gravity.CENTER); // Asegura que esté centrado
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Hace el fondo del DialogFragment transparente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_item_view, container, false);
        bindElementsXml(view); // Asocia los elementos del xml

        // Almacena el id del registro seleccionado, si hubo un error y no se ha enviando ningún id, cierra el dialogo
        int registerId = getArguments() != null ? getArguments().getInt("registerId", -1) : -1;
        if (registerId == -1) {
            dismiss();
            return view;
        }

        // Extrae la información desde la base de datos y la muestra
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(getContext());
            accountRegister = db.accountRegisterDAO().getAccountRegisterById(registerId);

            requireActivity().runOnUiThread(() -> {
                // En caso de no haber un registro con esa información, cierra el dialogo
                if (accountRegister == null) {
                    dismiss();
                    return;
                }

                byte[] recoveredSalt = CryptoUtils.decodeFromBase64(accountRegister.getSaltBase64());
                byte[] recoveredIv = CryptoUtils.decodeFromBase64(accountRegister.getIvBase64());

                try {
                    SecretKey recoveredKey = CryptoUtils.deriveKey("miClaveMaestra123", recoveredSalt);
                    String passwordCracked = CryptoUtils.decrypt(accountRegister.getPassword(), recoveredKey, recoveredIv);

                    // Carga la información del registro de la base de datos en el dialogo
                    tvRegisterItemTitle.setText(accountRegister.getTitle());
                    tvEmail.setText(accountRegister.getAcount());
                    tvUsername.setText(accountRegister.getUsername());
                    tvPassword.setText(passwordCracked);
                    tvWebsite.setText(accountRegister.getWebsite());
                    tvNotes.setText(accountRegister.getNotes());
                    ivIcon.setImageResource(R.drawable.encrypted);

                    // Se valida si los campos no obligatorios estan vacios
                    validarCampoVacio(tvEmail, "No se ha registrado ningún correo...");
                    validarCampoVacio(tvWebsite, "No se ha registrado ningún sitio web...");
                    validarCampoVacio(tvNotes, "No se ha registrado ninguna nota...");

                    // Manejar la fecha de registro
                    if (accountRegister.getCreatedAt() != 0) {
                        String fechaFormateada = formatearFecha(accountRegister.getCreatedAt());
                        tvRegisterDate.setText(fechaFormateada);
                    } else {
                        tvRegisterDate.setText("Fecha no disponible");
                    }

                    // Manejar la fecha de modificación
                    Long fechaModificadaRaw = accountRegister.getModifiedAt();
                    if (fechaModificadaRaw != null && fechaModificadaRaw != 0) {
                        String fechaModificada = formatearFecha(fechaModificadaRaw);
                        tvModifiedDate.setText(fechaModificada);
                    } else {
                        tvModifiedDate.setText("Sin modificar");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al cargar los datos.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                ivHideDialogRegisterView.setOnClickListener(v -> dismiss()); // Evento de cerrar el dialogo con el boton "X"

                // Evento de eliminar el registro que se esta observando
                btnDeleteRegister.setOnClickListener(v -> {
                    AccountRegisterControllerDB.deleteAccountRegister(accountRegister, getContext(), listener, this);
                });

                // Evento de actualizar registro (llama al activity y setea el registro a editar)
                btnUpdateRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent updateAccountRegisterIntent = new Intent(getContext(), NewRegisterActivity.class);
                        updateAccountRegisterIntent.putExtra("idAccountRegister", accountRegister.getId());
                        startActivity(updateAccountRegisterIntent);
                        ActivityTransitionUtil.applyEnterTransition(requireActivity());
                        dismiss();
                    }
                });
            });
        });

        return view;
    }

    private void bindElementsXml(View view) {
        btnDeleteRegister = view.findViewById(R.id.btnDeleteRegister);
        btnUpdateRegister = view.findViewById(R.id.btnUpdateRegister);
        ivHideDialogRegisterView = view.findViewById(R.id.ivHideDialogRegisterView);
        tvRegisterItemTitle = view.findViewById(R.id.tvRegisterItemTitle);

        tvEmail = view.findViewById(R.id.tvEmail);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvPassword = view.findViewById(R.id.etPassword);
        tvWebsite = view.findViewById(R.id.tvWebsite);
        tvRegisterDate = view.findViewById(R.id.tvRegisterDate);
        tvNotes = view.findViewById(R.id.tvNotes);
        ivIcon = view.findViewById(R.id.ivIcon);
        tvModifiedDate = view.findViewById(R.id.tvModifiedDate);

    }

    //Funcion para validar campos vacios no obligatorios
    private void validarCampoVacio(TextView textView, String mensaje) {
        String texto = textView.getText().toString().trim();
        if (texto.isEmpty()) {
            textView.setText(mensaje);
        }
    }

    //Funcion para el formateo de fecha/long
    private String formatearFecha(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault()); // Establecemos zona horaria
        Date date = new Date(timestamp);
        return sdf.format(date);
    }



}