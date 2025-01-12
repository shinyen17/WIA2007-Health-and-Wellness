package com.example.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

    private int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the userId from the parent activity (MainActivity)
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

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

        TabLayout tabLayout = view.findViewById(R.id.tablayout);

        //open first time will default open 1st fragment (run 1st fragment by default)
        UpComingFragment upComingFragment = new UpComingFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.schedule, upComingFragment)
                .addToBackStack(null)
                .commit();

        // Delay the call to fetch upcoming appointments
        new Handler().postDelayed(() -> {
            // Fetch upcoming appointments from UpComingFragment
            upComingFragment.fetchUpcomingAppointments(userId);
        }, 100); // Delay of 100ms

        // Set TabLayout listener to switch between fragments
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        // Show UpComingFragment
                        fragment = new UpComingFragment();
                        break;
                    case 1:
                        // Show PastFragment
                        fragment = new PastFragment();
                        break;
                }

                // Replace the fragment inside the schedule container
                if (fragment != null) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.schedule, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
}