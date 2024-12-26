package com.example.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpComingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpComingFragment extends Fragment {

    ConnectionClass connectionClass;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpComingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpComingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpComingFragment newInstance(String param1, String param2) {
        UpComingFragment fragment = new UpComingFragment();
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
        connectionClass = new ConnectionClass();

        View view = inflater.inflate(R.layout.fragment_up_coming, container, false);

        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        appointmentsRecyclerView.setLayoutManager(layoutManager);

        fetchUpcomingAppointments(2);

        return view;
    }

    // fetch UPCOMING appointment time
    public void fetchUpcomingAppointments(int userId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Connection connection = connectionClass.CONN();
                if (connection != null) {
                    // Get the current date in SQL format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = dateFormat.format(new Date());

                    Calendar calendar = Calendar.getInstance();
                    int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // e.g., 13 for 1 PM

                    Log.d("DEBUG_TAG", "Current Time: " + currentHour);

//                    int currentTimeEnum = mapCurrentTimeToEnum();

                    // Updated query to filter by date and time
                    String query = "SELECT a.Appointment_ID, a.Doctor_ID, a.Appointment_Date, a.Appointment_Time, \n" +
                            "       d.Doctor_Name, d.Specialization " +
                            "FROM Appointment a " +
                            "JOIN Doctor d ON a.Doctor_ID = d.Doctor_ID " +
                            "WHERE a.User_ID = ? " +
                            "  AND (a.Appointment_Date > ? " +
                            "       OR (a.Appointment_Date = ? AND a.Appointment_Time >= ?))" +
                            "ORDER BY a.Appointment_Date, a.Appointment_Time";

                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, userId);
                    statement.setString(2, currentDate);
                    statement.setString(3, currentDate);
                    statement.setInt(4, currentHour);
                    ResultSet resultSet = statement.executeQuery();

                    List<Appointment> appointments = new ArrayList<>();

                    while (resultSet.next()) {
                        int id = resultSet.getInt("Appointment_ID");
                        int doctorid = resultSet.getInt("Doctor_ID");
                        String date = resultSet.getString("Appointment_Date");
                        String time = mapEnumToString(resultSet.getInt("Appointment_Time"));
                        String doctorName = resultSet.getString("Doctor_Name");
                        String specialization = resultSet.getString("Specialization");

                        // Add all details into the Appointment object
                        appointments.add(new Appointment(id, doctorid, date, time, doctorName, specialization));
                    }


                    resultSet.close();
                    statement.close();

                    Log.d("Appointments Size", "Fetched appointments: " + appointments.size());

                    // Update UI on the main thread
                    getActivity().runOnUiThread(() -> {
                        appointmentList.clear(); // Clear current list
                        appointmentList.addAll(appointments); // Add new data
                        if (appointmentAdapter == null) {
                            appointmentAdapter = new AppointmentAdapter(getActivity(), appointmentList, false, this);
                            appointmentsRecyclerView.setAdapter(appointmentAdapter);
                        } else {
                            appointmentAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Connection failed!", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "SQL Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private String mapEnumToString(int value) {
        switch (value) {
            case 8:
                return "8:00 - 9:30 AM";
            case 10:
                return "10:00 - 11:30 AM";
            case 13:
                return "1:00 - 2:30 PM";
            case 15:
                return "3:00 - 4:30 PM";
            case 17:
                return "5:00 - 6:30 PM";
            default:
                return "Unknown";
        }
    }
}