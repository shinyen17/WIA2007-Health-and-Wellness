package com.example.navigation;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BadgeFragment extends Fragment {
    private ConnectionClass connectionClass;
    private ImageButton IBBack;
    private ImageView IVCal, IVExercise, IVLove, CalLock, ExerciseLock, LoveLock, IVLuv, IVSavvy, IVCalOnMe;
    private CardView CVCal, CVExercise, CVLove, CVWelcome;
    private boolean CalUnlocked = false, ExerciseUnlocked = false, LoveUnlocked = false;
    private int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the userId from the parent activity (MainActivity)
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_badge, container, false);

        // Initialize database connection class
        connectionClass = new ConnectionClass();

//        Toast.makeText(getContext(), "User_ID: " + userId, Toast.LENGTH_SHORT).show();

        // Initialize views

        IVCal = view.findViewById(R.id.IVCal);
        IVExercise = view.findViewById(R.id.IVExercise);
        IVLove = view.findViewById(R.id.IVLove);
        CVCal = view.findViewById(R.id.CVCal);
        CVExercise = view.findViewById(R.id.CVExercise);
        CVLove = view.findViewById(R.id.CVLove);
        CalLock = view.findViewById(R.id.CalLock);
        ExerciseLock = view.findViewById(R.id.ExerciseLock);
        LoveLock = view.findViewById(R.id.LoveLock);

        CVWelcome = view.findViewById(R.id.CVWelcome);
        IVCalOnMe = view.findViewById(R.id.IVCalOnMe);
        IVSavvy = view.findViewById(R.id.IVSavvy);
        IVLuv = view.findViewById(R.id.IVLuv);

        // Hide badges initially
        IVCalOnMe.setVisibility(View.INVISIBLE);
        IVSavvy.setVisibility(View.INVISIBLE);
        IVLuv.setVisibility(View.INVISIBLE);

        IBBack = view.findViewById(R.id.IBBackBadge);

        CheckConditionCalOnMe();
        CheckConditionExercise();
        CheckConditionLove();

        // avoid delay
        BadgeFragment badgeFragment = this;
        new Handler().postDelayed(() -> {
            // Fetch upcoming appointments from UpComingFragment
            badgeFragment.fetchUnlockedStatusFromDB();
        }, 100); // Delay of 100ms

//        // Fetch unlocked status from the database
//        fetchUnlockedStatusFromDB();

        CVWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Welcome To BlueFit!", Toast.LENGTH_SHORT).show();
            }
        });
        IBBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment ProfileFragment = new ProfileFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Get the FragmentManager for the parent activity
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Start a FragmentTransaction
                fragmentTransaction.replace(R.id.frame_layout, ProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void makeUnlocked(ImageView imageView, ImageView lock, ImageView badge) {
        if (imageView != null) {
            imageView.setVisibility(View.INVISIBLE);
            lock.setVisibility(View.INVISIBLE);
            badge.setVisibility(View.VISIBLE);
        }
    }

    private void makeClickable(CardView cardView) {
        if (cardView != null) {
            cardView.setClickable(true);
            cardView.setFocusable(true);
        }
    }

    private void fetchUnlockedStatusFromDB() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            try {
                con = connectionClass.CONN(); // Ensure ConnectionClass handles database connections
                if (con != null) {
                    String query = "SELECT CAL_STATUS, EXERCISE_STATUS, LUV_STATUS FROM badge WHERE User_ID = ?";
                    PreparedStatement stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        CalUnlocked = rs.getBoolean("CAL_STATUS");
                        ExerciseUnlocked = rs.getBoolean("EXERCISE_STATUS");
                        LoveUnlocked = rs.getBoolean("LUV_STATUS");
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(this::updateUI);
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "No badge record found for user", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }

                    // Close resources
                    rs.close();
                    stmt.close();
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Connection failed", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error fetching badge data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
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
            makeUnlocked(IVCal, CalLock, IVCalOnMe);
            makeClickable(CVCal);
            CVCal.setOnClickListener(view ->
                    Toast.makeText(getContext(), "Cal On Me Badge Gained!", Toast.LENGTH_SHORT).show());
        } else {
            CalLock.setOnClickListener(view ->
                    Toast.makeText(getContext(), "You haven't unlocked yet! Try to record more calories.", Toast.LENGTH_SHORT).show());
        }

        if (ExerciseUnlocked) {
            makeUnlocked(IVExercise, ExerciseLock, IVSavvy);
            makeClickable(CVExercise);
            CVExercise.setOnClickListener(view ->
                    Toast.makeText(getContext(), "Exercise Badge Gained!", Toast.LENGTH_SHORT).show());
        } else {
            ExerciseLock.setOnClickListener(view ->
                    Toast.makeText(getContext(), "You haven't unlocked yet! Try to do more exercise.", Toast.LENGTH_SHORT).show());
        }

        if (LoveUnlocked) {
            makeUnlocked(IVLove, LoveLock, IVLuv);
            makeClickable(CVLove);
            CVLove.setOnClickListener(view ->
                    Toast.makeText(getContext(), "Love Badge Gained!", Toast.LENGTH_SHORT).show());
        } else {
            LoveLock.setOnClickListener(view ->
                    Toast.makeText(getContext(), "You haven't unlocked yet! Try the Mental Health Check.", Toast.LENGTH_SHORT).show());
        }
    }
    //condition for CalOnMe
    public void CheckConditionCalOnMe() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int caloriesDay= 0;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    try (PreparedStatement stmtDay = con.prepareStatement(
                            "SELECT CALORIES_DAY FROM progress_dashboard WHERE User_ID = ?")) {
                        stmtDay.setInt(1, userId);
                        try (ResultSet rsDay = stmtDay.executeQuery()) {
                            if (rsDay.next()) {
                                caloriesDay = rsDay.getInt("CALORIES_DAY");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            boolean calUnlocked = caloriesDay >= 10;
            updateBadgeStatusInDB("CAL_STATUS", calUnlocked);
        });
    }
    public void CheckConditionExercise() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            int exerciseDay= 0;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    try (PreparedStatement stmtDay = con.prepareStatement(
                            "SELECT EXERCISE_DAY FROM progress_dashboard WHERE User_ID = ?")) {
                        stmtDay.setInt(1, userId);
                        try (ResultSet rsDay = stmtDay.executeQuery()) {
                            if (rsDay.next()) {
                                exerciseDay = rsDay.getInt("EXERCISE_DAY");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ExerciseUnlocked = exerciseDay >= 10;
            updateBadgeStatusInDB("EXERCISE_STATUS", ExerciseUnlocked);
        });
    }
    public void CheckConditionLove() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            boolean loveUnlocked = false;

            try (Connection con = connectionClass.CONN()) {
                if (con != null) {
                    try (PreparedStatement stmtResult = con.prepareStatement(
                            "SELECT result_PHQ9 FROM test_result WHERE User_ID = ?")) {
                        stmtResult.setInt(1, userId);
                        try (ResultSet rsResult = stmtResult.executeQuery()) {
                            if (rsResult.next()) {
                                if (rsResult.getObject("result_PHQ9") != null) {
                                    loveUnlocked = true;
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            updateBadgeStatusInDB("LUV_STATUS", loveUnlocked);
        });
    }
    private void updateBadgeStatusInDB(String columnName, boolean status) {
        try (Connection con = connectionClass.CONN()) {
            if (con != null) {
                String query = "UPDATE badge SET " + columnName + " = ? WHERE User_ID = ?";
                try (PreparedStatement stmt = con.prepareStatement(query)) {
                    stmt.setBoolean(1, status);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
