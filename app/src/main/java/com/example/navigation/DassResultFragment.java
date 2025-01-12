package com.example.navigation;

import android.os.Bundle;

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
 * Use the {@link DassResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DassResultFragment extends Fragment {

    ConnectionClass connectionClass;
    Connection con;
    String str;
    String depressionResult, anxietyResult, stressResult;
    private int userId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DassResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DassResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DassResultFragment newInstance(String param1, String param2) {
        DassResultFragment fragment = new DassResultFragment();
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
        View view = inflater.inflate(R.layout.fragment_dass_result, container, false);

        connectionClass = new ConnectionClass();

        // Get the score passed as an argument
        int totalScore = getArguments() != null ? getArguments().getInt("stotalScore", 0) : 0;

        TextView depression = view.findViewById(R.id.depressionlevel);
        TextView anxiety = view.findViewById(R.id.anxietylevel);
        TextView stress = view.findViewById(R.id.stresslevel);
        TextView btnBook = view.findViewById(R.id.btnBook);

        depressionResult = "";
        anxietyResult = "";
        stressResult = "";

        if(totalScore<5){
            depressionResult = "Normal";
        } else if (totalScore==5 || totalScore==6) {
            depressionResult = "Mild";
        } else if (totalScore>=7 && totalScore<=10) {
            depressionResult = "Moderate";
        } else if (totalScore>=11 || totalScore<=13) {
            depressionResult = "Severe";
        } else if (totalScore>=14) {
            depressionResult = "Extremely Severe";
        }

        depression.setText(depressionResult);

        if(totalScore<4){
            anxietyResult = "Normal";
        } else if (totalScore==4 || totalScore==5) {
            anxietyResult = "Mild";
        } else if (totalScore==6 && totalScore==7) {
            anxietyResult = "Moderate";
        } else if (totalScore==8 || totalScore==9) {
            anxietyResult = "Severe";
        } else if (totalScore>=10) {
            anxietyResult = "Extremely Severe";
        }
        anxiety.setText(anxietyResult);

        if(totalScore<8){
            stressResult = "Normal";
        } else if (totalScore==8 || totalScore==9) {
            stressResult = "Mild";
        } else if (totalScore>=10 && totalScore<=12) {
            stressResult = "Moderate";
        } else if (totalScore>=13 || totalScore<=16) {
            stressResult = "Severe";
        } else if (totalScore>=17) {
            stressResult = "Extremely Severe";
        }
        stress.setText(stressResult);

        // Save the result to the database
        saveTestResult(userId, depressionResult, anxietyResult, stressResult, "DASS21");

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

    private void saveTestResult(int userId, String depression, String anxiety, String stress, String testType) {

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
                    stmt.setString(4, "-");
                    stmt.setString(5, depression);
                    stmt.setString(6, anxiety);
                    stmt.setString(7, stress);

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