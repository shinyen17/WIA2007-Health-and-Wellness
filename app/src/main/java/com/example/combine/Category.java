package com.example.combine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Category extends AppCompatActivity {

    ConnectionClass connectionClass;
    Connection con;
    ResultSet rs;
    String name, str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        connectionClass = new ConnectionClass();
        connect();

        Button backButton = findViewById(R.id.back);

        backButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(Category.this, MainActivity2.class);
            startActivity(mainIntent);
        });

        TabLayout tabLayout = findViewById(R.id.tablayout);

        // Get the fragment name from the Intent
        String fragmentToLoad = getIntent().getStringExtra("fragment_to_load");

        Fragment fragment;
        int tabIndex;
        if (fragmentToLoad == null || fragmentToLoad.isEmpty()) {
            // Default to MentalFragment
            fragment = new MentalFragment();
            tabIndex = 0;
        } else {
            switch (fragmentToLoad) {
                case "DietFragment":
                    fragment = new DietFragment();
                    tabIndex = 1;
                    break;
                case "GeneralFragment":
                    fragment = new GeneralFragment();
                    tabIndex = 2;
                    break;
                default:
                    fragment = new MentalFragment(); // Fallback to default fragment
                    tabIndex = 0;
                    break;
            }
        }

        // Load the selected or default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();

        // Programmatically set the TabLayout to the correct tab
        tabLayout.selectTab(tabLayout.getTabAt(tabIndex));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment fragment = null;
                switch(tab.getPosition()){
                    case 0:
                        fragment = new MentalFragment();
                        break;
                    case 1:
                        fragment = new DietFragment();
                        break;
                    case 2:
                        fragment = new GeneralFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    public void connect() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Attempt to establish a connection
                con = connectionClass.CONN();
                if (con == null) {
                    str = "Error: Unable to connect to the MySQL server.";
                } else {
                    str = "Successfully connected to the MySQL server.";
                }
            } catch (Exception e) {
                str = "Connection failed: " + e.getMessage(); // Log the error message
                e.printStackTrace(); // Print stack trace for debugging
            }

            // Update UI on the main thread
            runOnUiThread(() -> {
                Toast.makeText(Category.this, str, Toast.LENGTH_SHORT).show();
            });
        });
    }


}