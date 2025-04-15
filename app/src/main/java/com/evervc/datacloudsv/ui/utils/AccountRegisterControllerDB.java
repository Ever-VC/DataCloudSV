package com.evervc.datacloudsv.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;


import androidx.appcompat.app.AppCompatActivity;
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
    public void insertAccountRegister(AppCompatActivity activity, AccountRegister accountRegister, IAccountRegisterListener listener) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(activity.getApplicationContext());
            Long resultado = db.accountRegisterDAO().insertNewRegister(accountRegister);

            if (resultado > 0) {
                listener.onChangeAccountRegistersList();
                activity.finish();
            }
        });
    }

    public void updateAccountRegister(AppCompatActivity activity, AccountRegister accountRegister, IAccountRegisterListener listener) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(activity.getApplicationContext());
            int resultado = db.accountRegisterDAO().updateRegister(accountRegister);

            if (resultado > 0) {
                listener.onChangeAccountRegistersList();
                activity.finish();
            }
        });
    }

    public void deleteAccountRegister(AccountRegister accountRegister, Context context, IAccountRegisterListener listener) {
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
                    if (resultado > 0) {
                        listener.onChangeAccountRegistersList();
                    }
                });
            }
        }).setNegativeButton("No", null).show();
    }

    public void showAccountRegisters(RecyclerView rcvAccountRegisters, Context context, IAccountRegisterListener listener, FragmentManager fragmentManager) {
        Handler handler = new Handler(Looper.getMainLooper());

        // Obteniendo los valores insertados
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(context);
            List<AccountRegister> lstAccountRegisters = db.accountRegisterDAO().getAllAccountRegisters();
            handler.post(() -> {
                AccountRegisterAdapter accountRegisterAdapter = new AccountRegisterAdapter(lstAccountRegisters, context, listener);
                rcvAccountRegisters.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false));
                rcvAccountRegisters.setAdapter(accountRegisterAdapter);
            });
        });
    }
}
