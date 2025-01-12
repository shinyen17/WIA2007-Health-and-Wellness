package com.example.navigation;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoutineFragment extends Fragment {

    private TextView username;
    private TextView TVValue;
    private Button btnEdit;
    private Button btnBMICalculator;
    private Button btnHistory;
    private ConnectionClass connectionClass;
    private Connection con;
    private int userId;
    private String str;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass = new ConnectionClass();
        // Retrieve userId from MainActivity
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        // Initialize views

        username = view.findViewById(R.id.username);
        TVValue = view.findViewById(R.id.TVValue);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnBMICalculator = view.findViewById(R.id.btnBMICalculator);
        btnHistory = view.findViewById(R.id.btnHistory);

        // Add back button click listener
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Go back to previous fragment
            requireActivity().onBackPressed();
        });

        // Add refresh button click listener
        ImageButton btnRefresh = view.findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            // Show a loading indicator (optional)
            Toast.makeText(getContext(), "Refreshing...", Toast.LENGTH_SHORT).show();
            // Update the record
            updateRecordFromDatabase();
        });

        //connect();
        getUsername();
        updateRecordFromDatabase();

        // Button click listeners
        btnEdit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", userId);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.MainFragment, new editRecordFragment(), "EditRecordFragment")
                    .addToBackStack(null)
                    .commit();
        });

        btnBMICalculator.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.MainFragment, new bmiCalculatorFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnHistory.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", userId);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.MainFragment, new historyFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    public void connect() {
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

            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show());
        });
    }

    private void getUsername() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String USERNAME = "";
            try (Connection con = connectionClass.CONN();
                 PreparedStatement stmt = con.prepareStatement("SELECT USERNAME FROM user WHERE User_ID = ?")) {

                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        USERNAME = "Hi, " + rs.getString("USERNAME");
                    } else {
                        USERNAME = "No user found";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                USERNAME = "Database error";
            }

            String finalUsername = USERNAME;
            requireActivity().runOnUiThread(() -> username.setText(finalUsername));
        });
    }

    public void updateRecordFromDatabase() {
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
                        int steps = rs.getInt("walking_steps");
                        float sleepHours = rs.getFloat("sleep_hour");
                        float waterIntake = rs.getFloat("water_intake");
                        float bmi = rs.getFloat("bmi");

                        requireActivity().runOnUiThread(() ->
                                setRecordValues(steps, sleepHours, waterIntake, bmi));
                    } else {
                        requireActivity().runOnUiThread(() ->
                                setRecordValues(0, 0.0f, 0.0f, 0.0f));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        setRecordValues(0, 0.0f, 0.0f, 0.0f));
            }
        });
    }

    private void setRecordValues(int steps, float sleepHours, float waterIntake, float bmi) {
        String recordText = String.format("\n%d\n\n%.1f\n\n%.1f\n\n%.1f",
                steps, sleepHours, waterIntake, bmi);
        TVValue.setText(recordText);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecordFromDatabase();
    }
}