package com.evervc.datacloudsv.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.database.AccountRegistersDB;
import com.evervc.datacloudsv.models.AccountRegister;
import com.evervc.datacloudsv.ui.dialogs.RegisterItemDialog;
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.DateUtils;
import com.evervc.datacloudsv.ui.utils.IAccountRegisterListener;

import java.util.List;

public class AccountRegisterAdapter extends RecyclerView.Adapter<AccountRegisterAdapter.AccountRegisterViewHolder> {
    private List<AccountRegister> lstAccountRegisters;
    private Context context;
    private AccountRegistersDB db;
    private IAccountRegisterListener listener;
    private FragmentManager fragmentManager;
    private ActivityResultLauncher<Intent> newAccountRegisterLauncher;

    public AccountRegisterAdapter(List<AccountRegister> lstAccountRegisters, Context context, IAccountRegisterListener listener, FragmentManager fragmentManager, ActivityResultLauncher<Intent> newAccountRegisterLauncher) {
        this.lstAccountRegisters = lstAccountRegisters;
        this.context = context;
        this.listener = listener;
        this.fragmentManager = fragmentManager;
        this.newAccountRegisterLauncher = newAccountRegisterLauncher;
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
        holder.imgItem.setImageResource(R.drawable.encrypted);
        holder.tvModificationDate.setText(DateUtils.getLastUpdatedMessage(accountRegister));
        holder.cvRegisterItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterItemDialog dialog = RegisterItemDialog.newInstance(accountRegister.getId());
                dialog.setListener(listener);
                dialog.setNewAccountRegisterLauncher(newAccountRegisterLauncher);
                dialog.show(fragmentManager, "register_item_view");
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstAccountRegisters.size();
    }

    public class AccountRegisterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvUsername, tvModificationDate;
        private ImageView imgItem;
        private CardView cvRegisterItem;
        public AccountRegisterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvModificationDate = itemView.findViewById(R.id.tvModificationDate);
            cvRegisterItem = itemView.findViewById(R.id.cvRegisterItem);
            imgItem = itemView.findViewById(R.id.imgItem);
        }
    }
}
