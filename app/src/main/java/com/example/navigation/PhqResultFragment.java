package com.example.navigation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhqResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhqResultFragment extends Fragment {

    ConnectionClass connectionClass;
    Connection con;
    String str;
    String resultText;
    private int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PhqResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhqResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhqResultFragment newInstance(String param1, String param2) {
        PhqResultFragment fragment = new PhqResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the userId from the parent activity (MainActivity)
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phq_result, container, false);

        connectionClass = new ConnectionClass();

        // Get the score passed as an argument
        int totalScore = getArguments() != null ? getArguments().getInt("stotalScore", 0) : 0;

        TextView score = view.findViewById(R.id.Score);
        TextView result = view.findViewById(R.id.result);
        ConstraintLayout resultcolour = view.findViewById(R.id.resultcolour);
        TextView btnBook = view.findViewById(R.id.btnBook);

        score.setText(String.valueOf(totalScore));

        resultText = "";

        if(totalScore>=0 && totalScore<5){
            resultText = "Minimal Depression";
            resultcolour.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else if (totalScore>=5 && totalScore<=9) {
            resultText = "Mild Depression";
            resultcolour.setBackgroundColor(Color.parseColor("#FFEB3B"));
        } else if (totalScore>=10 && totalScore<=14) {
            resultText = "Moderate Depression";
            resultcolour.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        } else if (totalScore>=15 && totalScore<=19) {
            resultText = "Moderately Severe Depression";
            resultcolour.setBackgroundColor(Color.parseColor("#FF5722"));
        } else if (totalScore>=20) {
            resultText = "Severe Depression";
            resultcolour.setBackgroundColor(Color.parseColor("#BA0C0C"));
        }

        result.setText(resultText);

        // Save the result to the database
        saveTestResult(userId, resultText, "PHQ9");

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppointmentFragment appointmentFragment = new AppointmentFragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, appointmentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();  // Launch the test fragment
            }
        });

        return view;
    }

    private void saveTestResult(int userId, String result, String testType) {
        // Remove the word "Depression" from the resultText
        String cleanedResult = resultText.replace("Depression", "").trim();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                con = connectionClass.CONN();
                if (con == null) {
                    str = "Error: Unable to connect to the MySQL server.";
                } else {
                    // Prepare the current date
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    // SQL query to insert the test result into the database
                    String query = "INSERT INTO Test_Result (User_ID, date_tested, Test_Type, result_PHQ9, depression_result, anxiety_result, stress_result) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId);
                    stmt.setString(2, currentDate);
                    stmt.setString(3, testType);
                    stmt.setString(4, cleanedResult);
                    stmt.setString(5, "-");
                    stmt.setString(6, "-");
                    stmt.setString(7, "-");

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        str = "Test result saved successfully!";
                    } else {
                        str = "Failed to save test result.";
                    }
                }
            } catch (Exception e) {
                str = "Connection failed: " + e.getMessage();
                e.printStackTrace();
            }

            requireActivity().runOnUiThread(() -> {
                //Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
            });
        });
    }
}