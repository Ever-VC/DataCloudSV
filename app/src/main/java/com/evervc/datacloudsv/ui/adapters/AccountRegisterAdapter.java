package com.evervc.datacloudsv.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.utils.IAccountRegisterListener;

import java.util.List;

public class AccountRegisterAdapter extends RecyclerView.Adapter<AccountRegisterAdapter.AccountRegisterViewHolder> {
    private List<AccountRegister> lstAccountRegisters;
    private Context context;
    private AccountRegistersDB db;
    private IAccountRegisterListener listener;

    public AccountRegisterAdapter(List<AccountRegister> lstAccountRegisters, Context context, IAccountRegisterListener listener) {
        this.lstAccountRegisters = lstAccountRegisters;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccountRegisterAdapter.AccountRegisterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_register_list, parent, false);
        return new AccountRegisterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountRegisterAdapter.AccountRegisterViewHolder holder, int position) {
        AccountRegister accountRegister = lstAccountRegisters.get(position);
        holder.tvTitle.setText(accountRegister.getTitle());
        holder.tvUsername.setText(accountRegister.getUsername());
        holder.btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return lstAccountRegisters.size();
    }

    public class AccountRegisterViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUsername;
        private ImageButton btnDeleteItem, btnEditItem;
        public AccountRegisterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteItem);
            btnEditItem = itemView.findViewById(R.id.btnEditItem);
        }
    }
}
