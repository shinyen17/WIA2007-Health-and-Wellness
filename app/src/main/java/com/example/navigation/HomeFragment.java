package com.example.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;




/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private int userId; // This will hold the userId for the whole session.

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

//         //Check if userId is valid
//        if (userId != -1) {
//            // Proceed with user-specific logic
//            Toast.makeText(getActivity(), "User ID: " + userId, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getActivity(), "User ID not found!", Toast.LENGTH_SHORT).show();
//        }

        // Find references to the views
        TextView btnroutine = view.findViewById(R.id.btnroutine);
        TextView btnmental = view.findViewById(R.id.btnmental);
        View dimOverlay = view.findViewById(R.id.dim_overlay);
        LinearLayout dialogContent = view.findViewById(R.id.dialog_content);
        TextView btnAppointment = view.findViewById(R.id.btnAppointment);
        TextView btnTest = view.findViewById(R.id.btnTest);
        TextView btnFAQ = view.findViewById(R.id.btnFaqs);
        ImageButton btnClose = view.findViewById(R.id.BtnClose);
        TextView btnphysical = view.findViewById(R.id.btnphysical);
        TextView btnDiet = view.findViewById(R.id.btndiet);

        // When the "MENTAL HEALTH" button is clicked
        btnmental.setOnClickListener(v -> {
            // Show the dim overlay and the dialog content
            dimOverlay.setVisibility(View.VISIBLE);
            dialogContent.setVisibility(View.VISIBLE);
        });

        // When the close button in the dialog is clicked
        btnClose.setOnClickListener(v -> {
            // Hide the dim overlay and dialog content
            dimOverlay.setVisibility(View.GONE);
            dialogContent.setVisibility(View.GONE);
        });

        btnroutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutineFragment routineFragment = new RoutineFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Get the FragmentManager for the parent activity
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Start a FragmentTransaction
                fragmentTransaction.replace(R.id.frame_layout, routineFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppointmentFragment appointmentFragment = new AppointmentFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Get the FragmentManager for the parent activity
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Start a FragmentTransaction
                fragmentTransaction.replace(R.id.frame_layout, appointmentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FAQsFragment faQsFragment = new FAQsFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Get the FragmentManager for the parent activity
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Start a FragmentTransaction
                fragmentTransaction.replace(R.id.frame_layout, faQsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MentalTestFragment mentalTestFragment = new MentalTestFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Get the FragmentManager for the parent activity
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Start a FragmentTransaction
                fragmentTransaction.replace(R.id.frame_layout, mentalTestFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        btnphysical.setOnClickListener(v -> {
            // Replace the current fragment with a new fragment containing the ViewPager and TabLayout
            PhysicalHealthFragment physicalHealthFragment = new PhysicalHealthFragment();
            FragmentManager fragmentManager = getParentFragmentManager(); // Use the FragmentManager for this Fragment
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, physicalHealthFragment);
            fragmentTransaction.addToBackStack(null); // Allow the user to navigate back
            fragmentTransaction.commit();
        });

        btnDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Diet_Fragment diet_fragment = new Diet_Fragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Use the FragmentManager for this Fragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, diet_fragment);
                fragmentTransaction.addToBackStack(null); // Allow the user to navigate back
                fragmentTransaction.commit();
            }
        });

        return view;
    }

}

