package com.example.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralFragment extends Fragment {

    private TextView appointmentButton5, appointmentButton6;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeneralFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralFragment newInstance(String param1, String param2) {
        GeneralFragment fragment = new GeneralFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);

        appointmentButton5 = view.findViewById(R.id.appoinment5);
        appointmentButton6 = view.findViewById(R.id.appoinment6);

        // Set click listeners to navigate to ChooseSlotFragment
        appointmentButton5.setOnClickListener(v -> {
            // Start ChooseSlotFragment and pass the doctor details
            ChooseSlotFragment chooseSlotFragment = new ChooseSlotFragment();
            Bundle args = new Bundle();
            args.putString("doctor_name", "Dr. John");
            args.putString("specialization", "General Practitioner (GP)");
            args.putInt("doctor_image", R.drawable.dr5); // Pass the image resource ID
            chooseSlotFragment.setArguments(args);

            // Begin fragment transaction
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, chooseSlotFragment); // R.id.fragment_container should be your container ID
            transaction.addToBackStack(null); // Optional: Add this transaction to the back stack to navigate back
            transaction.commit();
        });

        // Set click listeners to navigate to ChooseSlotFragment
        appointmentButton6.setOnClickListener(v -> {
            // Start ChooseSlotFragment and pass the doctor details
            ChooseSlotFragment chooseSlotFragment = new ChooseSlotFragment();
            Bundle args = new Bundle();
            args.putString("doctor_name", "Dr. Rebeca");
            args.putString("specialization", "Family Doctor");
            args.putInt("doctor_image", R.drawable.dr6); // Pass the image resource ID
            chooseSlotFragment.setArguments(args);

            // Begin fragment transaction
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, chooseSlotFragment); // R.id.fragment_container should be your container ID
            transaction.addToBackStack(null); // Optional: Add this transaction to the back stack to navigate back
            transaction.commit();
        });

        return view;
    }
}