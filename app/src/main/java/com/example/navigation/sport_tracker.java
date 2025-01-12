package com.example.navigation;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private int userId;
    ConnectionClass connectionClass;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_tracker, container, false);

        // Retrieve userId from MainActivity
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }

        connectionClass = new ConnectionClass();

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
            final String sportName = sportNames[i];
            final int sportCalories = caloriesPerHourValues[i];

            Button sportButton = view.findViewById(sportButtonIds[i]);
            if (sportButton != null) {
                sportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Reset any previous state and initialize the stopwatch for the selected sport
                        resetTimer();
                        caloriesPerHour = sportCalories;
                        selectedSport = sportName;
                        showStopwatchOverlay(); // Show the stopwatch UI
                        resetButtonsVisibility(); // Reset button visibility
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

    private void resetTimer() {
        // Reset the timer and sport-specific data
        startTime = 0;
        pausedTime = 0;
        isRunning = false;
        elapsedTimeInSeconds = 0;
        stopwatchTime.setText("00:00:00");
    }

    private void resetButtonsVisibility() {
        // Ensure the buttons are visible when a sport is selected
        buttonStart.setVisibility(View.VISIBLE);
        buttonStop.setVisibility(View.VISIBLE);
        buttonComplete.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.GONE);
    }

    public void showStopwatchOverlay() {
        stopwatchOverlay.setVisibility(View.VISIBLE);
        resetButtonsVisibility(); // Ensure buttons are correctly set when showing the stopwatch
    }

    public void hideStopwatchOverlay() {
        stopwatchOverlay.setVisibility(View.GONE);
        resetTimer(); // Clear the timer when hiding the overlay
    }



    private void calculateCaloriesBurned() {
        if (caloriesPerHour > 0) {
            double caloriesBurned = (caloriesPerHour * elapsedTimeInSeconds) / 3600.0;
            String message = String.format("Calories Burned When %s: %.2f kcal", selectedSport,caloriesBurned);
            Log.d("CaloriesBurned", message);

            // Insert into the database
            storeActivityInDatabase(caloriesBurned);

            // Update the stopwatch with the calories burned message
            stopwatchTime.setText(message);
        }
    }

//    private void storeActivityInDatabase(double caloriesBurned) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // Using your custom ConnectionClass
//                    ConnectionClass connectionClass = new ConnectionClass();
//                    Connection connection = connectionClass.CONN();
//                    if (connection != null) {
//                        String insertQuery = "INSERT INTO sport_tracker (User_ID, sport, duration_seconds, calories_burned) VALUES (?, ?, ?, ?)";
//                        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
//                            preparedStatement.setInt(1, userId); // Set the user ID
//                            preparedStatement.setString(2, selectedSport); // Set the sport name
//                            preparedStatement.setInt(3, elapsedTimeInSeconds); // Set the duration in seconds
//                            preparedStatement.setDouble(4, caloriesBurned); // Set the calories burned
//                            preparedStatement.executeUpdate(); // Execute the insert query
//                        }
//                        Toast.makeText(getContext(), "saved"+userId, Toast.LENGTH_SHORT).show();
//
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace(); // Log the SQL exception
//                }
//            }
//        }).start();
//    }

    public void storeActivityInDatabase(double caloriesBurned) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection connection = null;

            try {
                // Establish the connection
                connection = connectionClass.CONN();
                if (connection != null) {
                    // Use a parameterized query to prevent SQL injection
                    String query = "INSERT INTO sport_tracker (User_ID, sport, duration_seconds, calories_burned) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setInt(1, userId); // Set the user ID
                        statement.setString(2, selectedSport); // Set the sport name
                        statement.setInt(3, elapsedTimeInSeconds); // Set the duration in seconds
                        statement.setDouble(4, caloriesBurned); // Set the calories burned

                        // Execute the query (use executeUpdate for INSERT)
                        int rowsAffected = statement.executeUpdate();

                        // If the insert was successful
                        if (rowsAffected > 0) {
                            // Run on the UI thread to show a success message
                            requireActivity().runOnUiThread(() -> {
                                //Toast.makeText(getContext(), "Saved successfully for User ID: " + userId, Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                } else {
                    // Connection failed, run on UI thread to show error
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (SQLException e) {
                // Catch SQL exception and print the error
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                // Ensure the connection is closed
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
