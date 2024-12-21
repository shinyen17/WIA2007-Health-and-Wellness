package com.example.combine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class Schedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button backButton = findViewById(R.id.back);

        backButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(Schedule.this, MainActivity2.class);
            startActivity(mainIntent);
        });

        TabLayout tabLayout = findViewById(R.id.tablayout);

        //open first time will default open 1st fragment (run 1st fragment by default)
        UpComingFragment upComingFragment = new UpComingFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, upComingFragment)
                .addToBackStack(null)
                .commit();

        // Use a handler to delay the execution until the fragment is fully loaded
        new android.os.Handler().postDelayed(() -> {
            upComingFragment.fetchUpcomingAppointments(2);
        }, 100);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment fragment = null;
                switch(tab.getPosition()){
                    case 0:
                        fragment = new UpComingFragment();
                        break;
                    case 1:
                        fragment = new PastFragment();
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




}