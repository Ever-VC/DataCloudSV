package com.evervc.datacloudsv.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.ui.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private FragmentContainerView fcvContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindElementsXml();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btnHome:
                        chnageFragment(new HomeFragment());
                        return true;
                    case R.id.btnSettings:
                        Toast.makeText(HomeActivity.this, "Módulo en desarrollo...", Toast.LENGTH_SHORT).show();
                        return false;
                    case R.id.btnInfo:
                        Toast.makeText(HomeActivity.this, "Módulo en desarrollo...", Toast.LENGTH_SHORT).show();
                        return false;
                }
                return false;
            }
        });
    }

    private void bindElementsXml() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fcvContainer = findViewById(R.id.fcvContainer);
    }

    private void chnageFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fcvContainer, fragment).commit();
    }
}