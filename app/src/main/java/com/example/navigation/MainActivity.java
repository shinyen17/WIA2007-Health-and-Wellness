package com.example.navigation;

import android.app.Dialog;

import androidx. fragment. app. Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    BottomNavigationView bottomNavigationView;
    int userId; // This will hold the userId for the whole session.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the userId from the intent
        userId = getIntent().getIntExtra("userId", -1);  // -1 is the default value if "userId" is not found

//        // Check if userId is valid
//        if (userId != -1) {
//            // Proceed with user-specific logic
//            Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
//        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Use R.id.Home, R.id.Shorts, etc., which are the actual IDs defined in the XML
            if (item.getItemId() == R.id.Home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.Profile) {
                replaceFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.Logout) {
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            return true;  // Return true to indicate the item was selected
        });
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // You can get userId in any fragment like this:
    public int getUserId() {
        return userId;
    }
}