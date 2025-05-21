package com.evervc.datacloudsv.ui.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.ui.utils.ThemeHelper;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ConfigFragment extends Fragment {
    private MaterialButtonToggleGroup theme_toggle;

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
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_config, container, false);

        bindXmlElements(view);

        setTheme();

        return view;
    }

    private void bindXmlElements(View view) {
        theme_toggle = view.findViewById(R.id.theme_toggle);
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
}