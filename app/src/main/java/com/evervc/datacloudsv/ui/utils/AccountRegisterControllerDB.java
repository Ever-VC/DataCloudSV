package com.evervc.datacloudsv.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.adapters.AccountRegisterAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountRegisterControllerDB {
    public static void insertAccountRegister(AppCompatActivity activity, AccountRegister accountRegister) {
        Handler handler = new Handler(Looper.getMainLooper());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(activity.getApplicationContext());
            Long resultado = db.accountRegisterDAO().insertNewRegister(accountRegister);

            handler.post(() -> {
                if (resultado > 0) {
                    Toast.makeText(activity.getApplicationContext(), "Registro creado con éxito.", Toast.LENGTH_SHORT).show();
                    activity.setResult(activity.RESULT_OK);
                    activity.finish();
                }
            });
        });
    }

    public static void updateAccountRegister(AppCompatActivity activity, AccountRegister accountRegister) {
        Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(activity.getApplicationContext());
            int resultado = db.accountRegisterDAO().updateRegister(accountRegister);

            handler.post(() -> {
                if (resultado > 0) {
                    Toast.makeText(activity.getApplicationContext(), "Registro actualizado con éxito.", Toast.LENGTH_SHORT).show();
                    activity.setResult(activity.RESULT_OK);
                    activity.finish();
                }
            });
        });
    }

    public static void deleteAccountRegister(AccountRegister accountRegister, Context context, IAccountRegisterListener listener, DialogFragment dialogFragment) {
        Handler handler = new Handler(Looper.getMainLooper());
        AlertDialog.Builder mensajeDeConfirmacion = new AlertDialog.Builder(context);
        mensajeDeConfirmacion.setTitle("¿Está seguro que desea eliminar el registro?");
        mensajeDeConfirmacion.setMessage("Esta acción no puede revertirse, por lo tanto los cambios serán permanentes.");
        mensajeDeConfirmacion.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    AccountRegistersDB db = AccountRegistersDB.getInstance(context);
                    int resultado = db.accountRegisterDAO().deleteRegister(accountRegister);
                    handler.post(() -> {
                        if (resultado > 0) {
                            Toast.makeText(context, "Registro eliminado con éxito.", Toast.LENGTH_SHORT).show();
                            listener.onChangeAccountRegistersList();
                            dialogFragment.dismiss();
                        }
                    });
                });
            }
        }).setNegativeButton("No", null).show();
    }



    public static void showAccountRegisters(RecyclerView rcvAccountRegisters, TextView tvInformationMessage, Context context, FragmentManager fragmentManager, IAccountRegisterListener listener, ActivityResultLauncher<Intent> newAccountRegisterLauncher) {
        Handler handler = new Handler(Looper.getMainLooper());

        // Obteniendo los valores insertados
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(context);
            List<AccountRegister> lstAccountRegisters = db.accountRegisterDAO().getAllAccountRegisters();
            handler.post(() -> {

                if (!lstAccountRegisters.isEmpty()) {
                    tvInformationMessage.setVisibility(View.GONE);
                } else {
                    tvInformationMessage.setVisibility(View.VISIBLE);
                }

                AccountRegisterAdapter accountRegisterAdapter = new AccountRegisterAdapter(lstAccountRegisters, context, listener, fragmentManager, newAccountRegisterLauncher);
                rcvAccountRegisters.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false));
                rcvAccountRegisters.setAdapter(accountRegisterAdapter);
            });
        });
    }
}
