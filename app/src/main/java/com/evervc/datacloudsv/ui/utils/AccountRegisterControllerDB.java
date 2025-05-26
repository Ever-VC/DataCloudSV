package com.evervc.datacloudsv.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

    public static void showAccountRegistersByTitle(String title, RecyclerView rcvAccountRegisters, TextView tvInformationMessage, Context context, FragmentManager fragmentManager, IAccountRegisterListener listener, ActivityResultLauncher<Intent> newAccountRegisterLauncher) {
        Handler handler = new Handler(Looper.getMainLooper());

        // Obteniendo los valores insertados que coinciden con el titulo
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(context);
            List<AccountRegister> lstAccountRegisters = db.accountRegisterDAO().getAllAccountsByTitle(title);
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

    public static void showAccountRegistersSorted(SortOption sortOption, RecyclerView rcvAccountRegisters, TextView tvInformationMessage, Context context, FragmentManager fragmentManager, IAccountRegisterListener listener, ActivityResultLauncher<Intent> newAccountRegisterLauncher) {
        Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(context);
            List<AccountRegister> lstAccountRegisters;

            // Decidir la consulta según la opción seleccionada
            switch (sortOption) {
                case TITLE_ASC:
                    lstAccountRegisters = db.accountRegisterDAO().getAllByTitleAsc();
                    break;
                case TITLE_DESC:
                    lstAccountRegisters = db.accountRegisterDAO().getAllByTitleDesc();
                    break;
                case NEWEST_FIRST:
                    lstAccountRegisters = db.accountRegisterDAO().getAllByNewest();
                    break;
                case OLDEST_FIRST:
                    lstAccountRegisters = db.accountRegisterDAO().getAllByOldest();
                    break;
                default:
                    lstAccountRegisters = db.accountRegisterDAO().getAllAccountRegisters(); // fallback
            }

            List<AccountRegister> finalLstAccountRegisters = lstAccountRegisters;
            handler.post(() -> {
                if (!finalLstAccountRegisters.isEmpty()) {
                    tvInformationMessage.setVisibility(View.GONE);
                } else {
                    tvInformationMessage.setVisibility(View.VISIBLE);
                }

                AccountRegisterAdapter accountRegisterAdapter = new AccountRegisterAdapter(finalLstAccountRegisters, context, listener, fragmentManager, newAccountRegisterLauncher);rcvAccountRegisters.setLayoutManager(new GridLayoutManager(context, 1));
                rcvAccountRegisters.setAdapter(accountRegisterAdapter);
            });
        });
    }

    public static void deleteAllAccountRegisters(Context context) {
        Handler handler = new Handler(Looper.getMainLooper());

        new AlertDialog.Builder(context)
                .setTitle("¿Eliminar todos los registros?")
                .setMessage("Esta acción eliminará permanentemente todos los registros. ¿Está seguro?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        AccountRegistersDB db = AccountRegistersDB.getInstance(context);
                        int resultado = db.accountRegisterDAO().deleteAllRegisters();

                        handler.post(() -> {
                            if (resultado > 0) {
                                Toast.makeText(context, "Se eliminaron todos los registros.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "No hay registros para eliminar.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}
