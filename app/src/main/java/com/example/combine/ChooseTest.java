package com.example.combine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseTest extends AppCompatActivity {

    private boolean isTestSelected = false;
    private String selectedTest = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConstraintLayout test1 = findViewById(R.id.Test1);
        ConstraintLayout test2 = findViewById(R.id.Test2);
        setSlotButtonListener(test1, "DASS-21");
        setSlotButtonListener(test2, "PHQ-9");

        Button startButton = findViewById(R.id.Start);
        startButton.setOnClickListener(v -> {
            // Check if a test has been selected
            if (isTestSelected) {
                // Show a confirmation dialog before proceeding
                showConfirmationDialog();
            } else {
                // Show a toast asking the user to choose a test first
                Toast.makeText(ChooseTest.this, "Please choose a test first!", Toast.LENGTH_SHORT).show();
            }
        });

        TextView learnmore = findViewById(R.id.learnmore);
        learnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });

    }

    private void setSlotButtonListener(ConstraintLayout test, String testName) {
        test.setOnClickListener(v -> {
            // Reset the background of the previously selected layout
            resetLayouts();

            // Change the background color of the clicked layout
            test.setBackgroundResource(R.drawable.testchoosen);  // Set the selected color
            isTestSelected = true;
            selectedTest = testName;
        });
    }

    // Method to reset the background color of the layouts
    private void resetLayouts() {
        ConstraintLayout test1 = findViewById(R.id.Test1);
        ConstraintLayout test2 = findViewById(R.id.Test2);

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
        new AlertDialog.Builder(this)
                .setTitle(selectedTest)  // Set the title as the selected test name
                .setMessage(testMessage + "\n\nAre you sure you want to start the test?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Proceed to the next activity if confirmed
                        Intent intent = new Intent(ChooseTest.this, test.class);
                        intent.putExtra("selectedTest", selectedTest);  // Pass the selected test to the next activity
                        startActivity(intent);  // Launch the test activity
                    }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View customView = getLayoutInflater().inflate(R.layout.learnmoredialog, null);

        // Set the custom view for the dialog
        builder.setView(customView);

        // Create the dialog
        final AlertDialog dialog = builder.create();

        TextView close = customView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Now access the faq TextView from the customView
        TextView faq = customView.findViewById(R.id.faq);
        // Set an OnClickListener on the faq TextView
        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity3 when FAQ is clicked
                Intent intent = new Intent(ChooseTest.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }


}