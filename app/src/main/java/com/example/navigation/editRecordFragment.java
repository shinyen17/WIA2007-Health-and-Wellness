package com.example.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class editRecordFragment extends Fragment {

    private TextInputEditText stepsInput;
    private TextInputEditText sleepInput;
    private TextInputEditText waterInput;
    private TextInputEditText bmiInput;
    private Button btnSave;
    private ConnectionClass connectionClass;
    private Connection con;
    private String str;
    private int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass = new ConnectionClass();
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_record, container, false);

        // Initialize views
        stepsInput = view.findViewById(R.id.stepsInput);
        sleepInput = view.findViewById(R.id.sleepInput);
        waterInput = view.findViewById(R.id.waterInput);
        bmiInput = view.findViewById(R.id.bmiInput);
        btnSave = view.findViewById(R.id.btnSave);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        //connect();
        loadTodayRecord();

        btnSave.setOnClickListener(v -> saveRecord());

        return view;
    }

    private void connect() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionClass.CONN();
                if (con == null) {
                    str = "Error in connection with MySQL server";
                } else {
                    str = "Connected with MySQL server";
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show());
        });
    }

    private void loadTodayRecord() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            try (Connection con = connectionClass.CONN();
                 PreparedStatement stmt = con.prepareStatement(
                         "SELECT walking_steps, sleep_hour, water_intake, bmi " +
                                 "FROM routine WHERE user_id = ? AND date_created = ? " +
                                 "ORDER BY routine_id DESC LIMIT 1")) {

                stmt.setInt(1, userId);
                stmt.setString(2, today);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String steps = String.valueOf(rs.getInt("walking_steps"));
                        String sleep = String.format("%.1f", rs.getFloat("sleep_hour"));
                        String water = String.format("%.1f", rs.getFloat("water_intake"));
                        String bmi = String.format("%.1f", rs.getFloat("bmi"));

                        requireActivity().runOnUiThread(() -> {
                            stepsInput.setText(steps);
                            sleepInput.setText(sleep);
                            waterInput.setText(water);
                            bmiInput.setText(bmi);
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void saveRecord() {
        String steps = stepsInput.getText().toString();
        String sleep = sleepInput.getText().toString();
        String water = waterInput.getText().toString();
        String bmi = bmiInput.getText().toString();

        if (steps.isEmpty() || sleep.isEmpty() || water.isEmpty() || bmi.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            try (Connection con = connectionClass.CONN();
                 PreparedStatement stmt = con.prepareStatement(
                         "UPDATE routine SET walking_steps = ?, sleep_hour = ?, water_intake = ?, bmi = ? " +
                                 "WHERE user_id = ? AND date_created = ?")) {

                stmt.setInt(1, Integer.parseInt(steps));
                stmt.setFloat(2, Float.parseFloat(sleep));
                stmt.setFloat(3, Float.parseFloat(water));
                stmt.setFloat(4, Float.parseFloat(bmi));
                stmt.setInt(5, userId);
                stmt.setString(6, today);

                int result = stmt.executeUpdate();

                if (result > 0) {
                    // Wait briefly for database to update
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Update parent fragment first
                    Fragment parentFragment = getParentFragmentManager().findFragmentByTag("RoutineFragment");
                    if (parentFragment instanceof RoutineFragment) {
                        ((RoutineFragment) parentFragment).updateRecordFromDatabase();

                        // Wait for update to complete
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Then show success and navigate back
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Record updated successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    });
                } else {
                    // If no record exists for today, create a new one
                    try (PreparedStatement insertStmt = con.prepareStatement(
                            "INSERT INTO routine (user_id, date_created, walking_steps, sleep_hour, water_intake, bmi) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)")) {

                        insertStmt.setInt(1, userId);
                        insertStmt.setString(2, today);
                        insertStmt.setInt(3, Integer.parseInt(steps));
                        insertStmt.setFloat(4, Float.parseFloat(sleep));
                        insertStmt.setFloat(5, Float.parseFloat(water));
                        insertStmt.setFloat(6, Float.parseFloat(bmi));

                        insertStmt.executeUpdate();

                        // Update parent fragment and navigate back
                        Fragment parentFragment = getParentFragmentManager().findFragmentByTag("RoutineFragment");
                        if (parentFragment instanceof RoutineFragment) {
                            ((RoutineFragment) parentFragment).updateRecordFromDatabase();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "New record created successfully", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        });
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error saving record", Toast.LENGTH_SHORT).show());
            } catch (NumberFormatException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show());
            }
        });
    }
}