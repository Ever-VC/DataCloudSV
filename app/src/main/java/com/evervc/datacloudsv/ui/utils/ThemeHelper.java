package com.evervc.datacloudsv.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private static final String SELECTED_THEME = "Theme.Helper.Selected.Theme";
    private final SharedPreferences sharedPreferences;

    public ThemeHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences("THEME", Context.MODE_PRIVATE);
    }

    public int getSelectedTheme() {
        return sharedPreferences.getInt(SELECTED_THEME, 1);
    }

    public void setSelectedTheme(int theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SELECTED_THEME, theme);
        editor.apply();

        AppCompatDelegate.setDefaultNightMode(theme);
    }
}
