package com.example.navigation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseSlotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseSlotFragment extends Fragment {

    ConnectionClass connectionClass;
    Connection con;
    String str, doctorName;
    TextView slotButton1, slotButton2, slotButton3, slotButton4, slotButton5, bookButton, noSlotTextView, dateButton;
    Calendar calendar;
    private boolean isDateSelected = false;
    private boolean isTimeSelected = false;
    private TextView doctorNameTextView, specializationTextView, descriptionTextView;
    private ImageView doctorImageView;
    private TextView selectedSlotButton = null;  // To track the previously selected slot
    private String selectedTime = ""; // This will hold the selected time

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChooseSlotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseSlotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseSlotFragment newInstance(String param1, String param2) {
        ChooseSlotFragment fragment = new ChooseSlotFragment();
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
        connectionClass = new ConnectionClass();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_choose_slot, container, false);

        doctorNameTextView = rootView.findViewById(R.id.DrName);
        specializationTextView = rootView.findViewById(R.id.specialisation);
        descriptionTextView = rootView.findViewById(R.id.Description);
        doctorImageView = rootView.findViewById(R.id.imageView);

        dateButton = rootView.findViewById(R.id.DateB);
        calendar = Calendar.getInstance();
        slotButton1 = rootView.findViewById(R.id.Time1);
        slotButton2 = rootView.findViewById(R.id.Time2);
        slotButton3 = rootView.findViewById(R.id.Time3);
        slotButton4 = rootView.findViewById(R.id.Time4);
        slotButton5 = rootView.findViewById(R.id.Time5);
        noSlotTextView = rootView.findViewById(R.id.noSlotTextView);

        // Initially set the time buttons to GONE
        slotButton1.setVisibility(View.GONE);
        slotButton2.setVisibility(View.GONE);
        slotButton3.setVisibility(View.GONE);
        slotButton4.setVisibility(View.GONE);
        slotButton5.setVisibility(View.GONE);
        noSlotTextView.setVisibility(View.GONE);

        TextView back = rootView.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            // Get the FragmentManager and begin a transaction
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            // Remove the current fragment
            transaction.remove(ChooseSlotFragment.this);
            transaction.commit();
        });

        // Retrieve the data passed through the arguments
        Bundle args = getArguments();
        if (args != null) {
            doctorName = args.getString("doctor_name");
            int doctorImageResId = args.getInt("doctor_image", -1);

            // Set the data to the views
            doctorNameTextView.setText(doctorName);
            if (doctorImageResId != -1) {
                doctorImageView.setImageResource(doctorImageResId);
            }

            // Fetch doctor data based on the doctor's name
            fetchDoctorData(doctorName);
        }

        // Implement DatePickerDialog on the dateButton
        dateButton.setOnClickListener(v -> {
            // Always set the calendar to the current date
            Calendar todayCalendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // Create a Calendar instance and set the chosen date
                    calendar.set(year, monthOfYear, dayOfMonth);

                    // Format the date into a readable format
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = sdf.format(calendar.getTime());

                    // Set the formatted date on the button
                    dateButton.setText(formattedDate);
                    isDateSelected = true;

                    // When the user selects a date, make the time buttons visible
                    slotButton1.setVisibility(View.VISIBLE);
                    slotButton2.setVisibility(View.VISIBLE);
                    slotButton3.setVisibility(View.VISIBLE);
                    slotButton4.setVisibility(View.VISIBLE);
                    slotButton5.setVisibility(View.VISIBLE);
                    noSlotTextView.setVisibility(View.GONE);

                    // Check if the selected date is today
                    String todayDate = sdf.format(todayCalendar.getTime());
                    if (formattedDate.equals(todayDate)) {
                        // Get the current time
                        Calendar currentTime = Calendar.getInstance();
                        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);

                        // Hide buttons based on the current time
                        if (currentHour >= 8) slotButton1.setVisibility(View.GONE);
                        if (currentHour >= 10) slotButton2.setVisibility(View.GONE);
                        if (currentHour >= 13) slotButton3.setVisibility(View.GONE);
                        if (currentHour >= 15) slotButton4.setVisibility(View.GONE);
                        if (currentHour >= 17) slotButton5.setVisibility(View.GONE);
                    }

                    // Now fetch the available slots for the selected doctor and date
                    fetchAvailableSlots(doctorName, formattedDate);
                }
            }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Ensure the minimum selectable date is always today
            datePickerDialog.getDatePicker().setMinDate(todayCalendar.getTimeInMillis());
            datePickerDialog.show();
        });

        // Set listeners for time slots
        setSlotButtonListener(slotButton1, "8");
        setSlotButtonListener(slotButton2, "10");
        setSlotButtonListener(slotButton3, "13");
        setSlotButtonListener(slotButton4, "15");
        setSlotButtonListener(slotButton5, "17");

        bookButton = rootView.findViewById(R.id.Book);
        bookButton.setOnClickListener(v -> {
            if (!isDateSelected || !isTimeSelected) {
                Toast.makeText(getActivity(), "Please select both a date and a time slot before booking.", Toast.LENGTH_SHORT).show();
            } else {
                // Retrieve the selected date and time
                String selectedDate = dateButton.getText().toString();
                String selectedTime = this.selectedTime; // Get the selected time
                String formattedTime = "";

                // Format the time based on the selected time
                if (selectedTime.equals("8") || selectedTime.equals("10")) {
                    formattedTime = selectedTime + ":00 AM";  // 8:00 AM or 10:00 AM
                } else {
                    formattedTime = selectedTime + ":00 PM";
                }

                // Create the confirmation message
                String message = "You have selected the following appointment:\n\n" +
                        "Doctor: " + doctorName + " (" + specializationTextView.getText().toString() + " )\n" +
                        "Date: " + selectedDate + "\n" +
                        "Time: " + formattedTime + "\n\n" +
                        "Would you like to confirm your appointment?";

                // Show the confirmation dialog
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm Appointment")
                        .setMessage(message)
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            // If user confirms, proceed with booking the appointment
                            bookAppointment(selectedDate, Integer.parseInt(selectedTime));

                            // Navigate to ScheduleFragment
                            ScheduleFragment scheduleFragment = new ScheduleFragment();  // Create an instance of ScheduleFragment
                            getFragmentManager().beginTransaction()  // Start the fragment transaction
                                    .replace(R.id.Appointment, scheduleFragment)  // Replace the current fragment with ScheduleFragment
                                    .addToBackStack(null)  // Add the transaction to the back stack to allow the user to navigate back
                                    .commit();  // Commit the transaction


                        })
                        .setNegativeButton("Cancel", null)  // If user cancels, dismiss the dialog
                        .show();
            }
        });

        return rootView;
    }

    public void fetchDoctorData(String doctorName) {
        // Clean the doctorName to remove "Dr." before using it
        final String cleanedDoctorName = doctorName.startsWith("Dr. ") ? doctorName.substring(4) : doctorName;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection connection = null;
            String[] connectionStatus = new String[1]; // For thread safety

            try {
                // Establish the connection
                connection = connectionClass.CONN();
                if (connection != null) {
                    // Use a parameterized query to prevent SQL injection
                    String query = "SELECT Doctor_Name, Specialization, Description FROM Doctor WHERE Doctor_Name = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, cleanedDoctorName);

                    // Execute the query
                    ResultSet resultSet = statement.executeQuery();

                    // Check if data exists
                    if (resultSet.next()) {
                        // Retrieve the values for Doctor_Name, Specialization, and Description
                        String fetchedDoctorName = resultSet.getString("Doctor_Name");
                        String specialization = resultSet.getString("Specialization");
                        String description = resultSet.getString("Description");

                        // Update the UI with the fetched data
                        requireActivity().runOnUiThread(() -> {
                            doctorNameTextView.setText("Dr. " + fetchedDoctorName);
                            specializationTextView.setText(specialization);
                            descriptionTextView.setText(description);
                        });

                        connectionStatus[0] = "Query executed successfully";
                    } else {
                        connectionStatus[0] = "No doctor found with the name " + doctorName;
                    }

                    // Close resources
                    resultSet.close();
                    statement.close();
                } else {
                    connectionStatus[0] = "Connection failed!";
                }
            } catch (SQLException e) {
                connectionStatus[0] = "Error: " + e.getMessage();
                e.printStackTrace();
            } finally {
                // Close the connection
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Update UI with the connection status or result
            requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), connectionStatus[0], Toast.LENGTH_SHORT).show());
        });
    }

    // Helper method to fetch Doctor ID from the Doctor's name
    public int getDoctorId(String doctorName) {
        final String cleanedDoctorName = doctorName.startsWith("Dr. ") ? doctorName.substring(4) : doctorName;
        // This is a simple way of getting the doctor ID based on the name
        // Assuming you have a mapping between Doctor names and IDs in your database
        int doctorId = -1;
        try {
            Connection connection = connectionClass.CONN();
            if (connection != null) {
                String query = "SELECT Doctor_ID FROM Doctor WHERE Doctor_Name = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, cleanedDoctorName);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    doctorId = resultSet.getInt("Doctor_ID");
                }
                resultSet.close();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctorId;
    }

    // Method to fetch available slots for the doctor and date
    public void fetchAvailableSlots(final String doctorName, final String selectedDate) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection connection = null;

            try {
                connection = connectionClass.CONN();
                if (connection != null) {
                    // Get the Doctor ID
                    int doctorId = getDoctorId(doctorName);

                    if (doctorId == -1) {
                        // Handle doctor not found
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(), "Doctor not found", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // Correct table name 'Appointment' for query
                    String query = "SELECT Appointment_Time FROM Appointment WHERE Appointment_Date = ? AND Doctor_ID = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, selectedDate);  // Set the selected date
                    statement.setInt(2, doctorId);  // Use the correct Doctor_ID

                    ResultSet resultSet = statement.executeQuery();
                    Set<String> bookedSlots = new HashSet<>();
                    while (resultSet.next()) {
                        String bookedTime = resultSet.getString("Appointment_Time");
                        bookedSlots.add(bookedTime); // Add the booked time to the set
                    }

                    requireActivity().runOnUiThread(() -> {
                        // Hide buttons for the booked slots
                        if (bookedSlots.contains("8")) {
                            slotButton1.setVisibility(View.GONE); // Disable the 8am slot button
                        }
                        if (bookedSlots.contains("10")) {
                            slotButton2.setVisibility(View.GONE); // Disable the 10am slot button
                        }
                        if (bookedSlots.contains("13")) {
                            slotButton3.setVisibility(View.GONE); // Disable the 1pm slot button
                        }
                        if (bookedSlots.contains("15")) {
                            slotButton4.setVisibility(View.GONE); // Disable the 3pm slot button
                        }
                        if (bookedSlots.contains("17")) {
                            slotButton5.setVisibility(View.GONE); // Disable the 5pm slot button
                        }

                        // Check if there are no available slots and show the noSlotTextView message
                        updateNoSlotMessage();
                        Log.d("VisibilityCheck", "Button1: " + slotButton1.getVisibility());
                        Log.d("VisibilityCheck", "Button2: " + slotButton2.getVisibility());
                        Log.d("VisibilityCheck", "Button3: " + slotButton3.getVisibility());
                        Log.d("VisibilityCheck", "Button4: " + slotButton4.getVisibility());
                        Log.d("VisibilityCheck", "Button5: " + slotButton5.getVisibility());
                    });

                    // Clean up database resources
                    resultSet.close();
                    statement.close();
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Connection failed!", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            } finally {
                // Close connection in the finally block
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

    // Method to set a click listener for each time slot button
    private void setSlotButtonListener(TextView slotButton, String time) {
        slotButton.setOnClickListener(v -> {
            // Change the color of the previously selected slot back to its original state
            if (selectedSlotButton != null) {
                selectedSlotButton.setBackgroundResource(R.drawable.bookb);
            }

            // Change the color of the currently selected slot to indicate selection
            slotButton.setBackgroundResource(R.drawable.slot_choosen);  // Change to selected color
            isTimeSelected = true;
            // Store the newly selected button for future reference
            selectedSlotButton = slotButton;
            selectedTime = time;
        });
    }

    private void updateNoSlotMessage() {
        if (slotButton1.getVisibility() == View.GONE &&
                slotButton2.getVisibility() == View.GONE &&
                slotButton3.getVisibility() == View.GONE &&
                slotButton4.getVisibility() == View.GONE &&
                slotButton5.getVisibility() == View.GONE) {
            noSlotTextView.setVisibility(View.VISIBLE);
        } else {
            noSlotTextView.setVisibility(View.GONE); // Hide if at least one slot is available
        }
    }

    private void bookAppointment(String selectedDate, int selectedTime) {
        Bundle args = getArguments();
        if (args != null) {
            doctorName = args.getString("doctor_name");

            // Now, insert the appointment into the database
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                Connection connection = null;
                try {
                    // Establish the database connection
                    connection = connectionClass.CONN();
                    if (connection != null) {
                        int doctorId = getDoctorId(doctorName); // Get the Doctor ID

                        if (doctorId == -1) {
                            Toast.makeText(getActivity(), "Doctor not found:" + doctorName + doctorId, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Prepare the SQL insert query
                        String insertQuery = "INSERT INTO Appointment (User_ID, Doctor_ID, Appointment_Date, Appointment_Time) VALUES (?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(insertQuery);
                        statement.setInt(1, 2);
                        statement.setInt(2, doctorId); // Set Doctor_ID
                        statement.setString(3, selectedDate); // Set Appointment_Date
                        statement.setInt(4, selectedTime); // Set Appointment_Time

                        // Execute the insert query
                        int rowsAffected = statement.executeUpdate();
                        if (rowsAffected > 0) {
                            // Appointment successfully booked
                            requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Appointment booked successfully!", Toast.LENGTH_SHORT).show());
                        } else {
                            // Error while booking
                            requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error booking appointment", Toast.LENGTH_SHORT).show());
                        }

                        statement.close();
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Connection failed!", Toast.LENGTH_SHORT).show());
                    }
                } catch (SQLException e) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                } finally {
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

}