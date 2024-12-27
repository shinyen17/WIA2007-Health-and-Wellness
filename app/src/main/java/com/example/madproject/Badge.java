package com.example.madproject;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Badge extends AppCompatActivity {
    ConnectionClass connectionClass;
    private ImageView IVCal, IVExercise, IVLove, CalLock, ExerciseLock, LoveLock, IVLuv, IVSavvy, IVCalOnMe;
    private CardView CVCal, CVExercise, CVLove;
    private boolean CalUnlocked = false, ExerciseUnlocked = false, LoveUnlocked = false;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_badge);
        connectionClass = new ConnectionClass();
        userId = getIntent().getIntExtra("USER_ID", -1); // -1 is default if USER_ID not found
        Toast.makeText(this, "USER_ID: " + userId, Toast.LENGTH_SHORT).show();
        IVCal = findViewById(R.id.IVCal);
        IVExercise = findViewById(R.id.IVExercise);
        IVLove = findViewById(R.id.IVLove);
        CVCal = findViewById(R.id.CVCal);
        CVExercise = findViewById(R.id.CVExercise);
        CVLove = findViewById(R.id.CVLove);
        CalLock = findViewById(R.id.CalLock);
        ExerciseLock = findViewById(R.id.ExerciseLock);
        LoveLock = findViewById(R.id.LoveLock);

        IVCalOnMe = findViewById(R.id.IVCalOnMe);
        IVSavvy = findViewById(R.id.IVSavvy);
        IVLuv = findViewById(R.id.IVLuv);

        IVCalOnMe.setVisibility(View.INVISIBLE);
        IVSavvy.setVisibility(View.INVISIBLE);
        IVLuv.setVisibility(View.INVISIBLE);
        fetchUnlockedStatusFromDB();
    }
    private void makeUnlocked(ImageView imageView, ImageView lock, ImageView badge) {
        if (imageView != null) {
            imageView.setVisibility(View.INVISIBLE);
            lock.setVisibility(View.INVISIBLE);
            badge.setVisibility(View.VISIBLE);
        }
    }
    private void makeClickable(CardView cardView){
            if (cardView != null) {
                cardView.setClickable(true); // Enable or disable clicking
                cardView.setFocusable(true); // Enable focus if clickable
            }
    }
    private void fetchUnlockedStatusFromDB() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            try {
                con = connectionClass.CONN(); // Ensure ConnectionClass handles database connections
                if (con != null) {
                    String query = "SELECT CAL_STATUS, EXERCISE_STATUS, LUV_STATUS FROM badge WHERE USER_ID = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        CalUnlocked = rs.getBoolean("CAL_STATUS");
                        ExerciseUnlocked = rs.getBoolean("EXERCISE_STATUS");
                        LoveUnlocked = rs.getBoolean("LUV_STATUS");
                        runOnUiThread(() -> updateUI());
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(Badge.this, "No badge record found for user", Toast.LENGTH_SHORT).show()
                        );
                    }

                    // Close resources
                    rs.close();
                    stmt.close();
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(Badge.this, "Connection failed", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(Badge.this, "Error fetching badge data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } finally {
                try {
                    if (con != null) con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void updateUI() {
        if (CalUnlocked) {
            makeUnlocked(IVCal, CalLock,IVCalOnMe);
            makeClickable(CVCal);
            CVCal.setOnClickListener(view ->
                    Toast.makeText(this, "Cal On Me Badge Gained!", Toast.LENGTH_SHORT).show());
        }
        else {
            CalLock.setOnClickListener(view ->
                    Toast.makeText(this,"You haven't unlocked yet!",Toast.LENGTH_SHORT).show());
        }

        if (ExerciseUnlocked) {
            makeUnlocked(IVExercise, ExerciseLock,IVSavvy);
            makeClickable(CVExercise);
            CVExercise.setOnClickListener(view ->
                    Toast.makeText(this, "Exercise Badge Gained!", Toast.LENGTH_SHORT).show());
        }

        if (LoveUnlocked) {
            makeUnlocked(IVLove, LoveLock,IVLuv);
            makeClickable(CVLove);
            CVLove.setOnClickListener(view ->
                    Toast.makeText(this, "Love Badge Gained!", Toast.LENGTH_SHORT).show());
        }
    }

}