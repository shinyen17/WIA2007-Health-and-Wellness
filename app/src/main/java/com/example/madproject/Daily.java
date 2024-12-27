package com.example.madproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Daily extends AppCompatActivity {
    ConnectionClass connectionClass;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_daily);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.DailyFragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        connectionClass = new ConnectionClass();

        // Retrieve USER_ID from the intent
        userId = getIntent().getIntExtra("USER_ID", -1); // -1 is default if USER_ID not found
        Toast.makeText(this, "USER_ID: " + userId, Toast.LENGTH_SHORT).show();

        // Initialize checkboxes and chips
        Chip chipExercise = findViewById(R.id.chipExercise);
        CheckBox cbExercise = findViewById(R.id.CBExercise);
        setChipClickListener(chipExercise, cbExercise, "EXERCISE_DAY", "LAST_EXERCISE_CHECK");

        Chip chipCalories = findViewById(R.id.chipCalories);
        CheckBox cbCalories = findViewById(R.id.CBCalories);
        setChipClickListener(chipCalories, cbCalories, "CALORIES_DAY", "LAST_CALORIES_CHECK");

        Chip chipDrink = findViewById(R.id.chipDrink);
        CheckBox cbDrink = findViewById(R.id.CBDrink);
        setChipClickListener(chipDrink, cbDrink, "DRINK_DAY", "LAST_DRINK_CHECK");

        Chip chipWake = findViewById(R.id.chipWake);
        CheckBox cbWake = findViewById(R.id.CBWake);
        setChipClickListener(chipWake, cbWake, "WAKE_DAY", "LAST_WAKE_CHECK");

        Chip chipSleep = findViewById(R.id.chipSleep);
        CheckBox cbSleep = findViewById(R.id.CBSleep);
        setChipClickListener(chipSleep, cbSleep, "SLEEP_DAY", "LAST_SLEEP_CHECK");

        // Set the checkbox state based on the database values
        setCheckboxState(cbExercise, "EXERCISE_DAY", "LAST_EXERCISE_CHECK");
        setCheckboxState(cbCalories, "CALORIES_DAY", "LAST_CALORIES_CHECK");
        setCheckboxState(cbDrink, "DRINK_DAY", "LAST_DRINK_CHECK");
        setCheckboxState(cbWake, "WAKE_DAY", "LAST_WAKE_CHECK");
        setCheckboxState(cbSleep, "SLEEP_DAY", "LAST_SLEEP_CHECK");
    }

    private void setCheckboxState(CheckBox checkBox, String columnName, String lastCheckColumn) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            PreparedStatement stmt = null;
            try {
                con = connectionClass.CONN();
                if (con != null) {
                    // Query to check if the activity was checked today
                    String query = "SELECT DATE(" + lastCheckColumn + ") = CURDATE() AS isCheckedToday FROM progress_dashboard WHERE USER_ID = ?";
                    stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId);

                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        boolean isCheckedToday = resultSet.getBoolean("isCheckedToday");
                        // Set checkbox state based on today's check-in status
                        runOnUiThread(() -> checkBox.setChecked(isCheckedToday));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Daily.this, "Error retrieving check-in status.", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setChipClickListener(Chip chip, CheckBox checkBox, String columnName, String lastCheckColumn) {
        chip.setOnClickListener(v -> handleChipClick(columnName, lastCheckColumn, chip, checkBox));
    }

    public void handleChipClick(String columnName, String lastCheckColumn, Chip chip, CheckBox checkBox) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            PreparedStatement checkStmt = null;
            PreparedStatement updateStmt = null;

            try {
                con = connectionClass.CONN();
                if (con == null) {
                    runOnUiThread(() -> Toast.makeText(Daily.this, "Database connection failed", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Log for successful connection
                Log.d("Database", "Connection successful");

                // Check if the user has already checked in for the activity today
                String checkQuery = "SELECT DATE(" + lastCheckColumn + ") = CURDATE() AS isCheckedToday FROM progress_dashboard WHERE USER_ID = ?";
                checkStmt = con.prepareStatement(checkQuery);
                checkStmt.setInt(1, userId);

                ResultSet resultSet = checkStmt.executeQuery();
                boolean isCheckedToday = false;
                if (resultSet.next()) {
                    isCheckedToday = resultSet.getBoolean("isCheckedToday");
                    Log.d("Check Today", "isCheckedToday: " + isCheckedToday);
                }

                if (!isCheckedToday) {
                    // Log the update query to ensure it's correct
                    String updateQuery = "UPDATE progress_dashboard SET " + columnName + " = " + columnName + " + 1, " + lastCheckColumn + " = CURDATE() WHERE USER_ID = ?";
                    Log.d("SQL Update", "Executing query: " + updateQuery);

                    updateStmt = con.prepareStatement(updateQuery);
                    updateStmt.setInt(1, userId);

                    int rowsUpdated = updateStmt.executeUpdate();
                    Log.d("Rows Updated", "Rows updated: " + rowsUpdated);

                    if (rowsUpdated > 0) {
                        runOnUiThread(() -> {
                            // Update UI
                            checkBox.setChecked(true);
                            chip.setEnabled(false);
                            Toast.makeText(Daily.this, "Check-in successful!", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(Daily.this, "No rows updated. Check your query.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Daily.this, "You have already checked in today!", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Daily.this, "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (checkStmt != null) checkStmt.close();
                    if (updateStmt != null) updateStmt.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
