package com.evervc.datacloudsv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.evervc.datacloudsv.ui.HomeActivity;

public class MainActivity extends AppCompatActivity {
    private ProgressBar prgsbrLoadActivity;
    private Handler handler = new Handler();
    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindElemtsXml();
        simulateProgress();
    }

    private void bindElemtsXml() {
        prgsbrLoadActivity = findViewById(R.id.prgsbrLoadActivity);
    }

    private void simulateProgress() {
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 1;
                handler.post(() -> prgsbrLoadActivity.setProgress(progressStatus));
                try {
                    Thread.sleep(20); // Velocidad del progreso
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Inicia HomeActivity una vez completado el progreso
            Intent homeActivity = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeActivity);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }).start();
    }
}