package com.example.madproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ConnectionClass connectionClass;
    Connection con;
    ResultSet rs;
    String name, str;
    public CardView card1, card2, card3, card4;
    TextView username, TVHit, TVTotal,TVDayCount;
    int userID = 1; //hard coded user id
    String USERNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_main);
        connectionClass = new ConnectionClass();
        connect();
        username = findViewById(R.id.username);
        getUsername();
        card2 = (CardView) findViewById(R.id.c2);
        card3 = (CardView) findViewById(R.id.c3);
        card4 = (CardView) findViewById(R.id.c4);

        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);
        TVHit = findViewById(R.id.TVHit);
        TVTotal = findViewById(R.id.TVTotal);
        TVDayCount = findViewById(R.id.TVDayCount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MainFragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchGoalCounts();
        fetchDailyCounts();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        if (v.getId() == R.id.c2) {
            i = new Intent(this, Goal.class);
            i.putExtra("USER_ID", userID); // Pass USER_ID here
            startActivity(i);
        } else if (v.getId() == R.id.c3) {
            i = new Intent(this, Daily.class);
            i.putExtra("USER_ID", userID); // Pass USER_ID here
            startActivity(i);
        } else if (v.getId() == R.id.c4) {
            i = new Intent(this, Badge.class);
            i.putExtra("USER_ID", userID); // Pass USER_ID here
            startActivity(i);
        }
    }
    public void connect(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{
            try{
                con = connectionClass.CONN();
                if(con ==null){
                    str = "Error in connection with MySQL server";
                } else{
                    str = "Connected with MySQL server";
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }

            runOnUiThread(()->{
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
            });
        });
    }
    public void getUsername(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Connection con = connectionClass.CONN();
                 PreparedStatement stmt = con.prepareStatement("SELECT USERNAME FROM users WHERE USER_ID = ?")) {

                stmt.setInt(1, userID); // Dynamically set userID

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
            runOnUiThread(() -> username.setText(USERNAME));
        });
    }
    public void fetchGoalCounts() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int totalGoals = 0;
            int hitGoals = 0;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    // Query to count goals with STATUS = true
                    try (PreparedStatement stmtHit = con.prepareStatement(
                            "SELECT COUNT(GOAL_CREATED) AS HitCount FROM goal WHERE USER_ID = ? AND GOAL_STATUS = true")) {
                        stmtHit.setInt(1, userID);
                        try (ResultSet rsHit = stmtHit.executeQuery()) {
                            if (rsHit.next()) {
                                hitGoals = rsHit.getInt("HitCount");
                            }
                        }
                    }

                    // Query to count all goals regardless of STATUS
                    try (PreparedStatement stmtTotal = con.prepareStatement(
                            "SELECT COUNT(GOAL_CREATED) AS TotalCount FROM goal WHERE USER_ID = ?")) {
                        stmtTotal.setInt(1, userID);
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

            runOnUiThread(() -> {
                TVHit.setText(String.valueOf(finalHitGoals));
                TVTotal.setText(String.valueOf(finalTotalGoals));
            });
        });
    }

    public void fetchDailyCounts() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int caloriesDay=0,exerciseDay=0,drinkDay=0,wakeDay=0,sleepDay=0;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    // Query to count goals with STATUS = true
                    try (PreparedStatement stmtDay = con.prepareStatement(
                            "SELECT CALORIES_DAY,EXERCISE_DAY,DRINK_DAY,SLEEP_DAY,WAKE_DAY FROM progress_dashboard WHERE USER_ID = ?")) {
                        stmtDay.setInt(1, userID);
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

            runOnUiThread(() -> {
                TVDayCount.setText(String.valueOf(finalCaloriesDay)+"\n\n"+String.valueOf(finalExerciseDay)+"\n\n"+
                        String.valueOf(finalDrinkDay)+"\n\n"+String.valueOf(finalWakeDay)+"\n\n"+String.valueOf(finalSleepDay));
            });
        });
    }
}
