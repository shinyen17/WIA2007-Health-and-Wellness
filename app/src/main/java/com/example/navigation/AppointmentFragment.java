package com.example.navigation;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentFragment extends Fragment {

    private EditText doctorInsert;
    private ImageButton btnFind;
    private LinearLayout mentalHealth, Diet, general;
    private TextView seeAll;
    private ConnectionClass connectionClass;
    private Connection con;
    private int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AppointmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubscriptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppointmentFragment newInstance(String param1, String param2) {
        AppointmentFragment fragment = new AppointmentFragment();
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
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

//        //Check if userId is valid
//        if (userId != -1) {
//            // Proceed with user-specific logic
//            Toast.makeText(getActivity(), "User ID: " + userId, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getActivity(), "User ID not found!", Toast.LENGTH_SHORT).show();
//        }

        // Initialize the connection class
        connectionClass = new ConnectionClass();

        // Initialize UI elements
        doctorInsert = view.findViewById(R.id.dcotorInsert);
        btnFind = view.findViewById(R.id.BtnFind);
        seeAll = view.findViewById(R.id.seeAll);
        mentalHealth = view.findViewById(R.id.mentalHealth);
        Diet = view.findViewById(R.id.diet);
        general = view.findViewById(R.id.general);

        // Set up the event handlers
        setupUIListeners(view);

        return view;
    }

    private void setupUIListeners(View view) {
        // Listener for the "See All" button
        seeAll.setOnClickListener(v -> {
            // Replace the current fragment with CategoryFragment
            CategoryFragment categoryFragment = new CategoryFragment();

            // Begin the transaction
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.Appointment, categoryFragment);  // Use the correct container ID here
            transaction.addToBackStack(null); // Optionally add to back stack
            transaction.commit();
        });

        // Listener for the "Appointment Schedule" button
        ImageButton appointmentSchedule = view.findViewById(R.id.appoitmentSchedule);
        appointmentSchedule.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            ScheduleFragment fragment = new ScheduleFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.Appointment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Click listeners for doctor selections
        view.findViewById(R.id.DR1).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("doctor_name", "Dr. Sarah");
            bundle.putString("specialization", "Clinical Psychologist");
            bundle.putInt("doctor_image", R.drawable.dr1);  // Pass image resource ID

            ChooseSlotFragment fragment = new ChooseSlotFragment();
            fragment.setArguments(bundle);  // Set the data for the fragment

            // Replace the current fragment with ChooseSlotFragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.Appointment, fragment)
                    .addToBackStack(null)  // Optionally add to back stack for navigation
                    .commit();
        });

        view.findViewById(R.id.DR3).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("doctor_name", "Dr. Adam");
            bundle.putString("specialization", "Dietitian");
            bundle.putInt("doctor_image", R.drawable.dr3);

            ChooseSlotFragment fragment = new ChooseSlotFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.Appointment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.DR5).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("doctor_name", "Dr. John");
            bundle.putString("specialization", "General Practitioner (GP)");
            bundle.putInt("doctor_image", R.drawable.dr5);

            ChooseSlotFragment fragment = new ChooseSlotFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.Appointment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Categories click listeners
        mentalHealth.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("fragment_to_load", "MentalFragment");  // Pass the category type

            CategoryFragment fragment = new CategoryFragment();
            fragment.setArguments(bundle);  // Set the data for the fragment

            getFragmentManager().beginTransaction()
                    .replace(R.id.Appointment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        Diet.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("fragment_to_load", "DietFragment");

            CategoryFragment fragment = new CategoryFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.Appointment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        general.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("fragment_to_load", "GeneralFragment");

            CategoryFragment fragment = new CategoryFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.Appointment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Search doctor functionality
        btnFind.setOnClickListener(v -> {
            String doctorName = doctorInsert.getText().toString().trim();
            if (doctorName.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a doctor's name.", Toast.LENGTH_SHORT).show();
            } else {
                searchDoctor(doctorName);
            }
        });
    }

    private void searchDoctor(String doctorName) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionClass.CONN(); // Database connection
                if (con != null) {
                    String query = "SELECT * FROM Doctor WHERE Doctor_Name = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setString(1, doctorName);
                    ResultSet rs = stmt.executeQuery();

                    boolean found = rs.next(); // Check if the doctor was found
                    getActivity().runOnUiThread(() -> {
                        if (found) {
                            showDialog("Doctor " + doctorName + " is available!");
                        } else {
                            showDialog("Doctor " + doctorName + " not found.");
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Database connection failed.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });

        // Create and show the dialog
        AlertDialog alert = builder.create();
        alert.show();
    }
}