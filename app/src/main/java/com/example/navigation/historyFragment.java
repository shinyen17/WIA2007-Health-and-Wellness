package com.example.navigation;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class historyFragment extends Fragment {

    private TextView TVValue;
    private TextView dateTitle;
    private Button btnSelectDate;
    private ConnectionClass connectionClass;
    private Connection con;
    private String str;
    private int userId;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass = new ConnectionClass();
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize views
        TVValue = view.findViewById(R.id.TVValue);
        dateTitle = view.findViewById(R.id.dateTitle);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        //connect();

        btnSelectDate.setOnClickListener(v -> showDatePicker());

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

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    loadRecord();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadRecord() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(calendar.getTime());

            try (Connection con = connectionClass.CONN();
                 PreparedStatement stmt = con.prepareStatement(
                         "SELECT walking_steps, sleep_hour, water_intake, bmi " +
                                 "FROM routine WHERE user_id = ? AND date_created = ? " +
                                 "ORDER BY routine_id DESC LIMIT 1")) {

                stmt.setInt(1, userId);
                stmt.setString(2, selectedDate);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int steps = rs.getInt("walking_steps");
                        int sleepHours = rs.getInt("sleep_hour");
                        float waterIntake = rs.getFloat("water_intake");
                        float bmi = rs.getFloat("bmi");

                        requireActivity().runOnUiThread(() -> {
                            String formattedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                                    .format(calendar.getTime());
                            dateTitle.setText("\nRECORD FOR " + formattedDate + "\n");
                            setRecordValues(steps, sleepHours, waterIntake, bmi);
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            String formattedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                                    .format(calendar.getTime());
                            dateTitle.setText("\nNO RECORD FOR " + formattedDate + "\n");
                            setRecordValues(0, 0, 0.0f, 0.0f);
                        });
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error loading record", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setRecordValues(int steps, int sleepHours, float waterIntake, float bmi) {
        String recordText = String.format("\n%d\n\n%d\n\n%.1f\n\n%.1f",
                steps, sleepHours, waterIntake, bmi);
        TVValue.setText(recordText);
    }
}