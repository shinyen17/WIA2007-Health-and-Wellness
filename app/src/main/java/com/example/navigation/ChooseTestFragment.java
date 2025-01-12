package com.example.navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseTestFragment extends Fragment {

    private boolean isTestSelected = false;
    private String selectedTest = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChooseTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseTestFragment newInstance(String param1, String param2) {
        ChooseTestFragment fragment = new ChooseTestFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_test, container, false);

        ConstraintLayout test1 = view.findViewById(R.id.Test1);
        ConstraintLayout test2 = view.findViewById(R.id.Test2);

        setSlotButtonListener(test1, "DASS-21", view);
        setSlotButtonListener(test2, "PHQ-9", view);

        TextView startButton = view.findViewById(R.id.Start);
        startButton.setOnClickListener(v -> {
            // Check if a test has been selected
            if (isTestSelected) {
                // Show a confirmation dialog before proceeding
                showConfirmationDialog();
            } else {
                // Show a toast asking the user to choose a test first
                Toast.makeText(getContext(), "Please choose a test first!", Toast.LENGTH_SHORT).show();
            }
        });

        TextView learnmore = view.findViewById(R.id.learnmore);
        learnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });

        return view;
    }

    private void setSlotButtonListener(ConstraintLayout test, String testName, View view) {
        test.setOnClickListener(v -> {
            // Reset the background of the previously selected layout
            resetLayouts(view);

            // Change the background color of the clicked layout
            test.setBackgroundResource(R.drawable.testchoosen);  // Set the selected color
            isTestSelected = true;
            selectedTest = testName;
        });
    }

    // Method to reset the background color of the layouts
    private void resetLayouts(View view) {
        ConstraintLayout test1 = view.findViewById(R.id.Test1);
        ConstraintLayout test2 = view.findViewById(R.id.Test2);

        // Reset both layouts back to their default background color
        test1.setBackgroundResource(R.drawable.whitebutton);  // Default color
        test2.setBackgroundResource(R.drawable.whitebutton);  // Default color
    }

    // Method to show the confirmation dialog
    // Method to show the confirmation dialog with dynamic message based on the selected test
    private void showConfirmationDialog() {
        String testMessage = "";  // Message to display in the dialog

        // Set the appropriate message based on the selected test
        if (selectedTest.equals("DASS-21")) {
            testMessage = "This depression test is a 21 question test for diagnosing, monitoring, and measuring the severity of depression, anxiety and stress.";
        } else if (selectedTest.equals("PHQ-9")) {
            testMessage = "This depression test is a 9 question test for diagnosing, monitoring, and measuring the severity of depression.";
        }

        // Create and show the confirmation dialog
        new AlertDialog.Builder(requireContext()) // Use requireContext() to get the proper context
                .setTitle(selectedTest)  // Set the title as the selected test name
                .setMessage(testMessage + "\n\nAre you sure you want to start the test?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Proceed to the next fragment if confirmed
                    TestQuestionFragment testQuestionFragment = new TestQuestionFragment();
                    Bundle args = new Bundle();
                    args.putString("selectedTest", selectedTest);  // Pass the selected test to the next fragment
                    testQuestionFragment.setArguments(args);

                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, testQuestionFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();  // Launch the test fragment
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog if canceled
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showInfoDialog() {
        // Create a new AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View customView = getLayoutInflater().inflate(R.layout.learnmoredialog, null);

        // Set the custom view for the dialog
        builder.setView(customView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog when OK is clicked
                        dialog.dismiss();
                    }
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}