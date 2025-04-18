package com.evervc.datacloudsv.ui.utils;

import android.app.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.evervc.datacloudsv.R;

public class ActivityTransitionUtil {
    // Llamar esto en onCreate() de cualquier Activity para mostrar la animación de cierre
    public static void applyBackTransition(AppCompatActivity activity) {
        activity.getOnBackPressedDispatcher().addCallback(activity,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
    }

    // Llamar esto justo después de iniciar un Activity
    public static void applyEnterTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
