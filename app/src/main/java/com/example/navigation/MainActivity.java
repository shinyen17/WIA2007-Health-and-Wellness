package com.example.navigation;

import androidx.core.view.GravityCompat;
import androidx. fragment. app. Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx. appcompat. widget. Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        // Setting up the ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setting default fragment on first launch
        if(savedInstanceState == null){
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
            bottomNavigationView.setSelectedItemId(R.id.Home); // Ensure bottom nav is synced
        }

//        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.Diet) {
                replaceFragment(new FoodFragment());
            } else if (item.getItemId() == R.id.PhysicalHealth) {
                replaceFragment(new PhysicalHealthFragment());
            } else if (item.getItemId() == R.id.MentalHealth) {
                replaceFragment(new AppointmentFragment());
            } else if (item.getItemId() == R.id.Profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;  // Return true to indicate the item was selected
        });

        // Setting NavigationView item selection listener
        navigationView.setNavigationItemSelectedListener(item -> {
            Log.d("MainActivity", "Menu item clicked: " + item.getItemId());

            if (item.getItemId() == R.id.nav_home) {
                Log.d("MainActivity", "Navigating to Home fragment.");
                // Don't need to start MainActivity since it's already the current activity
                drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer
            }else if (item.getItemId() == R.id.nav_mentaltest) {
                Log.d("MainActivity", "Starting MentalHealthTest activity.");
                startActivity(new Intent(MainActivity.this, MentalHealthTest.class));
            }else if (item.getItemId() == R.id.nav_faq) {
                Log.d("MainActivity", "Starting FAQs activity.");
                startActivity(new Intent(MainActivity.this, FAQs.class));
            }
            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}