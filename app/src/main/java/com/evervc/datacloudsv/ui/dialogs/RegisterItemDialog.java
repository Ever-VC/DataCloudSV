package com.evervc.datacloudsv.ui.dialogs;

import android.app.Dialog;
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
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.IAccountRegisterListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterItemDialog extends DialogFragment {
    private TextView tvRegisterItemTitle;
    private ImageView ivHideDialogRegisterView;
    private Button btnHideDialogRegisterView;
    private Button btnDeleteRegister;

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
        /*if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }*/
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            getDialog().getWindow().setLayout(width, height);
            getDialog().getWindow().setGravity(Gravity.CENTER); // Asegura que esté centrado
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Hacer fondo del DialogFragment transparente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_item_view, container, false);
        bindElementsXml(view);

        int registerId = getArguments() != null ? getArguments().getInt("registerId", -1) : -1;
        if (registerId == -1) {
            dismiss();
            return view;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            AccountRegistersDB db = AccountRegistersDB.getInstance(getContext());
            accountRegister = db.accountRegisterDAO().getAccountRegisterById(registerId);

            requireActivity().runOnUiThread(() -> {
                if (accountRegister == null) {
                    System.out.println("El registro es nulooooooooo!!!! ----------------------------");
                    dismiss();
                    return;
                }

                tvRegisterItemTitle.setText(accountRegister.getTitle());

                ivHideDialogRegisterView.setOnClickListener(v -> dismiss());
                btnHideDialogRegisterView.setOnClickListener(v -> dismiss());

                btnDeleteRegister.setOnClickListener(v -> {
                    AccountRegisterControllerDB.deleteAccountRegister(accountRegister, getContext(), listener, this);
                });
            });
        });

        return view;
    }


    private void bindElementsXml(View view) {
        btnDeleteRegister = view.findViewById(R.id.btnDeleteRegister);
        ivHideDialogRegisterView = view.findViewById(R.id.ivHideDialogRegisterView);
        btnHideDialogRegisterView = view.findViewById(R.id.btnHideDialogRegisterView);
        tvRegisterItemTitle = view.findViewById(R.id.tvRegisterItemTitle);
    }
}