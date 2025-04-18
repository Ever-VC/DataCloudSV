package com.evervc.datacloudsv.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.ui.NewRegisterActivity;
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.ActivityTransitionUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    private FloatingActionButton fabAddRegister;
    private RecyclerView rcvAccountRegisters;
    private TextView tvInformationMessage;
    private ActivityResultLauncher<Intent> newAccountRegisterLauncher;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Associating graphic elements
        bindElementsXml(view);

        newAccountRegisterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        onChangeAccountRegistersList();
                    }
                }
        );

        // Listener to add new register
        fabAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show activity for create a new register
                Intent newAccountRegisterIntent = new Intent(getContext(), NewRegisterActivity.class);
                newAccountRegisterLauncher.launch(newAccountRegisterIntent);
                ActivityTransitionUtil.applyEnterTransition(requireActivity());
            }
        });

        onChangeAccountRegistersList();

        return view;
    }

    private void bindElementsXml(View view) {
        fabAddRegister = view.findViewById(R.id.fabAddRegister);
        rcvAccountRegisters = view.findViewById(R.id.rcvAccountRegisters);
        tvInformationMessage = view.findViewById(R.id.tvInformationMessage);
    }

    private void onChangeAccountRegistersList() {
        AccountRegisterControllerDB.showAccountRegisters(rcvAccountRegisters, tvInformationMessage, getContext(), getChildFragmentManager(), this::onChangeAccountRegistersList);
    }
}