package com.example.navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.chip.Chip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DailyFragment extends Fragment {
    private ConnectionClass connectionClass;
    private int userId;
    private ImageButton IBBack;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the userId from the parent activity (MainActivity)
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        connectionClass = new ConnectionClass();

//        Toast.makeText(getContext(), "User_ID: " + userId, Toast.LENGTH_SHORT).show();

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.DailyFragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize checkboxes and chips
        Chip chipExercise = view.findViewById(R.id.chipExercise);
        CheckBox cbExercise = view.findViewById(R.id.CBExercise);
        setChipClickListener(chipExercise, cbExercise, "EXERCISE_DAY", "LAST_EXERCISE_CHECK");

        Chip chipCalories = view.findViewById(R.id.chipCalories);
        CheckBox cbCalories = view.findViewById(R.id.CBCalories);
        setChipClickListener(chipCalories, cbCalories, "CALORIES_DAY", "LAST_CALORIES_CHECK");

        Chip chipDrink = view.findViewById(R.id.chipDrink);
        CheckBox cbDrink = view.findViewById(R.id.CBDrink);
        setChipClickListener(chipDrink, cbDrink, "DRINK_DAY", "LAST_DRINK_CHECK");

        Chip chipWake = view.findViewById(R.id.chipWake);
        CheckBox cbWake = view.findViewById(R.id.CBWake);
        setChipClickListener(chipWake, cbWake, "WAKE_DAY", "LAST_WAKE_CHECK");

        Chip chipSleep = view.findViewById(R.id.chipSleep);
        CheckBox cbSleep = view.findViewById(R.id.CBSleep);
        setChipClickListener(chipSleep, cbSleep, "SLEEP_DAY", "LAST_SLEEP_CHECK");

        // Set the checkbox state based on the database values
        setCheckboxState(cbExercise, "EXERCISE_DAY", "LAST_EXERCISE_CHECK");
        setCheckboxState(cbCalories, "CALORIES_DAY", "LAST_CALORIES_CHECK");
        setCheckboxState(cbDrink, "DRINK_DAY", "LAST_DRINK_CHECK");
        setCheckboxState(cbWake, "WAKE_DAY", "LAST_WAKE_CHECK");
        setCheckboxState(cbSleep, "SLEEP_DAY", "LAST_SLEEP_CHECK");

        IBBack = view.findViewById(R.id.IBBackDaily);
        IBBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment ProfileFragment = new ProfileFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Get the FragmentManager for the parent activity
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Start a FragmentTransaction
                fragmentTransaction.replace(R.id.frame_layout, ProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
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
                    String query = "SELECT DATE(" + lastCheckColumn + ") = CURDATE() AS isCheckedToday FROM progress_dashboard WHERE User_ID = ?";
                    stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId);

                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        boolean isCheckedToday = resultSet.getBoolean("isCheckedToday");
                        // Set checkbox state based on today's check-in status
                        requireActivity().runOnUiThread(() -> checkBox.setChecked(isCheckedToday));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error retrieving check-in status.", Toast.LENGTH_SHORT).show());
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
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Database connection failed", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Log for successful connection
                Log.d("Database", "Connection successful");

                // Check if the user has already checked in for the activity today
                String checkQuery = "SELECT DATE(" + lastCheckColumn + ") = CURDATE() AS isCheckedToday FROM progress_dashboard WHERE User_ID = ?";
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
                    String updateQuery = "UPDATE progress_dashboard SET " + columnName + " = " + columnName + " + 1, " + lastCheckColumn + " = CURDATE() WHERE User_ID = ?";
                    Log.d("SQL Update", "Executing query: " + updateQuery);

                    updateStmt = con.prepareStatement(updateQuery);
                    updateStmt.setInt(1, userId);

                    int rowsUpdated = updateStmt.executeUpdate();
                    Log.d("Rows Updated", "Rows updated: " + rowsUpdated);

                    if (rowsUpdated > 0) {
                        requireActivity().runOnUiThread(() -> {
                            // Update UI
                            checkBox.setChecked(true);
                            chip.setEnabled(false);
                            Toast.makeText(getContext(), "Check-in successful!", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "No rows updated. Check your query.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "You have already checked in today!", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
