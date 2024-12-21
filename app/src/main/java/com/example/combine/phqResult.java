package com.example.combine;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class phqResult extends AppCompatActivity {

    ConnectionClass connectionClass;
    Connection con;
    String str;

    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phq_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connectionClass = new ConnectionClass();
        //connect();

        // Retrieve the total score passed from MainActivity2
        int totalScore = getIntent().getIntExtra("totalScore", 0);

        TextView score = findViewById(R.id.Score);
        TextView result = findViewById(R.id.result);
        ConstraintLayout resultcolour = findViewById(R.id.resultcolour);

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


        int userId = 1;
        // Save the result to the database
        saveTestResult(userId, resultText, "PHQ9");

        Button bookASession = findViewById(R.id.bookASession);
        bookASession.setOnClickListener(v -> {
            Intent intent = new Intent(phqResult.this, MainActivity2.class);
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
//                Toast.makeText(phqResult.this, str, Toast.LENGTH_SHORT).show();
//            });
//        });
//    }

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
                    String query = "INSERT INTO Test_Result (User_ID, date_tested, Test_Type, result_PHQ9) " +
                            "VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId);
                    stmt.setString(2, currentDate);
                    stmt.setString(3, testType);
                    stmt.setString(4, cleanedResult);

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
                Toast.makeText(phqResult.this, str, Toast.LENGTH_SHORT).show();
            });
        });
    }

}