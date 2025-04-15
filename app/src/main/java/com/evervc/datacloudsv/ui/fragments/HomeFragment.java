package com.evervc.datacloudsv.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.ui.NewRegisterActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    private FloatingActionButton fabAddRegister;

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

        // Listener to add new register
        fabAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show activity for create a new register
                startActivity(new Intent(getContext(), NewRegisterActivity.class));
            }
        });

        return view;
    }

    private void bindElementsXml(View view) {
        fabAddRegister = view.findViewById(R.id.fabAddRegister);
    }
}