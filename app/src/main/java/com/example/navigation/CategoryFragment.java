package com.example.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.sql.Connection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {

    private ConnectionClass connectionClass;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        connectionClass = new ConnectionClass();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Set up the back button
        Button backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            // Replace the current fragment with AppointmentFragment
            AppointmentFragment appointmentFragment = new AppointmentFragment();
            // Use the fragment's Activity to get the FragmentManager
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, appointmentFragment); // Replace with your actual container ID
            fragmentTransaction.addToBackStack(null); // Optional: Add to back stack for navigation
            fragmentTransaction.commit();
        });

        // Set up the TabLayout
        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        // Get the fragment name from the arguments passed during fragment replacement
        String fragmentToLoad = getArguments() != null ? getArguments().getString("fragment_to_load") : null;

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
        getChildFragmentManager().beginTransaction()
                .replace(R.id.cat, fragment)
                .commit();

        // Set the selected tab in TabLayout
        tabLayout.selectTab(tabLayout.getTabAt(tabIndex));

        // Tab selection listener
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
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
                // Replace fragment when tab is selected
                getChildFragmentManager().beginTransaction().replace(R.id.cat, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle unselected tab if necessary
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle reselected tab if necessary
            }
        });

        return view;
    }
}