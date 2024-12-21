package com.example.combine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class dassResult extends AppCompatActivity {

    ConnectionClass connectionClass;
    Connection con;
    String str;

    String depressionResult, anxietyResult, stressResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dassresult);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        connectionClass = new ConnectionClass();
        //connect();

        // Retrieve the total score passed from MainActivity2
        int totalScore = getIntent().getIntExtra("totalScore", 0);

        TextView depression = findViewById(R.id.depressionlevel);
        TextView anxiety = findViewById(R.id.anxietylevel);
        TextView stress = findViewById(R.id.stresslevel);

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

        int userId = 1;
        // Save the result to the database
        saveTestResult(userId, depressionResult, anxietyResult, stressResult, "DASS21");

        Button BookASession = findViewById(R.id.BookASession);
        BookASession.setOnClickListener(v -> {
            Intent intent = new Intent(dassResult.this, MainActivity2.class);
            startActivity(intent);
        });
    }

//    public void connect() {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                // Attempt to establish a connection
//                con = connectionClass.CONN();
//                if (con == null) {
//                    str = "Error: Unable to connect to the MySQL server.";
//                } else {
//                    str = "Successfully connected to the MySQL server.";
//                }
//            } catch (Exception e) {
//                str = "Connection failed: " + e.getMessage(); // Log the error message
//                e.printStackTrace(); // Print stack trace for debugging
//            }
//
//            // Update UI on the main thread
//            runOnUiThread(() -> {
//                Toast.makeText(dassResult.this, str, Toast.LENGTH_SHORT).show();
//            });
//        });
//    }

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
                    String query = "INSERT INTO Test_Result (User_ID, date_tested, Test_Type, depression_result, anxiety_result, stress_result) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId);
                    stmt.setString(2, currentDate);
                    stmt.setString(3, testType);
                    stmt.setString(4, depression);
                    stmt.setString(5, anxiety);
                    stmt.setString(6, stress);

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

            // Update UI on the main thread
            runOnUiThread(() -> {
                Toast.makeText(dassResult.this, str, Toast.LENGTH_SHORT).show();
            });
        });
    }

}