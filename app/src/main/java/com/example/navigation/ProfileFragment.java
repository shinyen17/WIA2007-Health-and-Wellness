package com.example.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    ConnectionClass connectionClass;
    Connection con;
    ResultSet rs;
    String name, str;
    private CardView card2, card3, card4;
    private TextView username, TVHit, TVTotal, TVDayCount, TVCal;
    private int userId;
    private String USERNAME = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionClass = new ConnectionClass();
        // Retrieve userId from arguments
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        username = rootView.findViewById(R.id.username);
        card2 = rootView.findViewById(R.id.c2);
        card3 = rootView.findViewById(R.id.c3);
        card4 = rootView.findViewById(R.id.c4);
        TVHit = rootView.findViewById(R.id.TVHit);
        TVTotal = rootView.findViewById(R.id.TVTotal);
        TVDayCount = rootView.findViewById(R.id.TVDayCount);

        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);

        TVCal = rootView.findViewById(R.id.TVCal);
        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.MainFragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connect();
        getUsername();
        fetchGoalCounts();
        fetchDailyCounts();
        fetchCal();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.c2) {
            replaceFragment(new GoalFragment());
        } else if (v.getId() == R.id.c3) {
            replaceFragment(new DailyFragment());
        } else if (v.getId() == R.id.c4) {
            replaceFragment(new BadgeFragment());
        }
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

            //requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show());
        });
    }

    public void getUsername() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection con = connectionClass.CONN();
                 PreparedStatement stmt = con.prepareStatement("SELECT USERNAME FROM user WHERE User_ID = ?")) {

                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        USERNAME = "Welcome " + rs.getString("USERNAME");
                    } else {
                        USERNAME = "No user found";
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                USERNAME = "Database error";
            }

            requireActivity().runOnUiThread(() -> username.setText(USERNAME));
        });
    }

    public void fetchGoalCounts() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int totalGoals = 0;
            int hitGoals = 0;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    try (PreparedStatement stmtHit = con.prepareStatement(
                            "SELECT COUNT(GOAL_CREATED) AS HitCount FROM goal WHERE User_ID = ? AND GOAL_STATUS = true")) {
                        stmtHit.setInt(1, userId);
                        try (ResultSet rsHit = stmtHit.executeQuery()) {
                            if (rsHit.next()) {
                                hitGoals = rsHit.getInt("HitCount");
                            }
                        }
                    }

                    try (PreparedStatement stmtTotal = con.prepareStatement(
                            "SELECT COUNT(GOAL_CREATED) AS TotalCount FROM goal WHERE User_ID = ?")) {
                        stmtTotal.setInt(1, userId);
                        try (ResultSet rsTotal = stmtTotal.executeQuery()) {
                            if (rsTotal.next()) {
                                totalGoals = rsTotal.getInt("TotalCount");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            int finalHitGoals = hitGoals;
            int finalTotalGoals = totalGoals;

            requireActivity().runOnUiThread(() -> {
                TVHit.setText(String.valueOf(finalHitGoals));
                TVTotal.setText(String.valueOf(finalTotalGoals));
            });
        });
    }

    public void fetchDailyCounts() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int caloriesDay = 0, exerciseDay = 0, drinkDay = 0, wakeDay = 0, sleepDay = 0;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    try (PreparedStatement stmtDay = con.prepareStatement(
                            "SELECT CALORIES_DAY, EXERCISE_DAY, DRINK_DAY, SLEEP_DAY, WAKE_DAY FROM progress_dashboard WHERE User_ID = ?")) {
                        stmtDay.setInt(1, userId);
                        try (ResultSet rsDay = stmtDay.executeQuery()) {
                            if (rsDay.next()) {
                                caloriesDay = rsDay.getInt("CALORIES_DAY");
                                exerciseDay = rsDay.getInt("EXERCISE_DAY");
                                drinkDay = rsDay.getInt("DRINK_DAY");
                                sleepDay = rsDay.getInt("SLEEP_DAY");
                                wakeDay = rsDay.getInt("WAKE_DAY");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            int finalCaloriesDay = caloriesDay;
            int finalExerciseDay = exerciseDay;
            int finalDrinkDay = drinkDay;
            int finalWakeDay = wakeDay;
            int finalSleepDay = sleepDay;

            requireActivity().runOnUiThread(() -> {
                TVDayCount.setText(finalCaloriesDay + "\n\n" + finalExerciseDay + "\n\n" +
                        finalDrinkDay + "\n\n" + finalWakeDay + "\n\n" + finalSleepDay);
            });
        });
    }

    private void fetchCal(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int totalCalories = 0;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    try (PreparedStatement stmtCal = con.prepareStatement(
                            "SELECT Total_Calories FROM total_calories WHERE User_ID = ?")) {
                        stmtCal.setInt(1, userId);
                        try (ResultSet rsCal = stmtCal.executeQuery()) {
                            if (rsCal.next()) {
                                totalCalories = rsCal.getInt("Total_Calories");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String finalCaloriesDay = String.valueOf(totalCalories);

            requireActivity().runOnUiThread(() -> {
                TVCal.setText(finalCaloriesDay);
            });
        });
    }

    private void replaceFragment(Fragment fragment) {
        // Get the parent activity's FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Begin the fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the fragment container with the new fragment
        fragmentTransaction.replace(R.id.MainFragment, fragment);

        // Optionally, add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }


}
