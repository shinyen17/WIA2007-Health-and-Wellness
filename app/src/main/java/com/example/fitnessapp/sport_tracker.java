package com.example.fitnessapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class sport_tracker extends Fragment {

    private View stopwatchOverlay;
    private TextView stopwatchTime;
    private Button buttonStart, buttonStop, buttonComplete, buttonCancel;

    private Handler handler;
    private Runnable timerRunnable;
    private long startTime = 0;
    private long pausedTime = 0;
    private boolean isRunning = false;
    private int elapsedTimeInSeconds = 0;
    private int caloriesPerHour = 0; // This will vary based on the button clicked
    private String selectedSport = ""; // To store the selected sport

    String[] sportNames = {
            "Jump Rope", "Swimming", "Cycling", "Running", "Badminton",
            "Basketball", "Tennis", "Walking", "Football", "Hiking"
    };

    int[] sportButtonIds = {
            R.id.buttonJumpRope, R.id.buttonSwimming, R.id.buttonCycling, R.id.buttonRunning,
            R.id.buttonBadminton, R.id.buttonBasketball, R.id.buttonTennis, R.id.buttonWalking,
            R.id.buttonFootball, R.id.buttonHiking
    };

    int[] caloriesPerHourValues = {700, 600, 500, 600, 400, 600, 550, 280, 600, 400};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_tracker, container, false);

        stopwatchOverlay = view.findViewById(R.id.stopwatchOverlay);
        stopwatchTime = view.findViewById(R.id.stopwatchTime);
        buttonStart = view.findViewById(R.id.buttonStart);
        buttonStop = view.findViewById(R.id.buttonStop);
        buttonComplete = view.findViewById(R.id.buttonComplete);
        buttonCancel = new Button(getContext());
        buttonCancel.setText("Cancel");
        buttonCancel.setVisibility(View.GONE);

        // Dynamically add buttonCancel to the layout
        ViewGroup parentLayout = (ViewGroup) view.findViewById(R.id.stopwatchOverlay);
        parentLayout.addView(buttonCancel);

        handler = new Handler();

        // Define the timer logic
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTime;
                elapsedTimeInSeconds = (int) (elapsedTime / 1000);

                int seconds = elapsedTimeInSeconds % 60;
                int minutes = (elapsedTimeInSeconds / 60) % 60;
                int hours = (elapsedTimeInSeconds / 3600);

                String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                stopwatchTime.setText(time);

                // Repeat the runnable every second
                handler.postDelayed(this, 1000);
            }
        };

        // Set listeners for sport buttons
        for (int i = 0; i < sportButtonIds.length; i++) {
            final String sportName = sportNames[i];  // Get the sport name based on the button clicked
            final int sportCalories = caloriesPerHourValues[i];  // Create a final variable for calories per hour

            Button sportButton = view.findViewById(sportButtonIds[i]);
            if (sportButton != null) {
                sportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        caloriesPerHour = sportCalories;  // Use the final variable
                        selectedSport = sportName;  // Store the selected sport name
                        showStopwatchOverlay();
                    }
                });
            }
        }

        // Button actions
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis() - pausedTime;
                    handler.post(timerRunnable);
                    isRunning = true;
                }
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    handler.removeCallbacks(timerRunnable);
                    pausedTime = System.currentTimeMillis() - startTime;
                    isRunning = false;
                }
            }
        });

        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    handler.removeCallbacks(timerRunnable);
                    isRunning = false;
                }
                calculateCaloriesBurned();
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.GONE);
                buttonComplete.setVisibility(View.GONE);
                buttonCancel.setVisibility(View.VISIBLE);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStopwatchOverlay();
            }
        });

        return view;
    }

    private void calculateCaloriesBurned() {
        if (caloriesPerHour > 0) {
            double caloriesBurned = (caloriesPerHour * elapsedTimeInSeconds) / 3600.0;
            String message = String.format("Calories Burned: %.2f", caloriesBurned);
            Log.d("CaloriesBurned", message);

            // Insert into the database
            storeActivityInDatabase(caloriesBurned);

            // Update the stopwatch with the calories burned message
            stopwatchTime.setText(message);
        }
    }

    private void storeActivityInDatabase(double caloriesBurned) {
        // Replace with the actual user ID
        int userId = 1; // Example user ID. Replace with actual user ID from your app

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Using your custom ConnectionClass
                    ConnectionClass connectionClass = new ConnectionClass();
                    Connection connection = connectionClass.CONN();
                    if (connection != null) {
                        String insertQuery = "INSERT INTO sport_tracker (user_id, sport, duration_seconds, calories_burned) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                            preparedStatement.setInt(1, userId);
                            preparedStatement.setString(2, selectedSport);  // Use the selected sport name
                            preparedStatement.setInt(3, elapsedTimeInSeconds);  // Duration in seconds
                            preparedStatement.setDouble(4, caloriesBurned);  // Calories burned
                            preparedStatement.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void showStopwatchOverlay() {
        stopwatchOverlay.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.GONE);
    }

    public void hideStopwatchOverlay() {
        stopwatchOverlay.setVisibility(View.GONE);
    }
}
