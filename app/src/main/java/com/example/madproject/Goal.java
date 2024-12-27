package com.example.madproject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Goal extends AppCompatActivity {
    ConnectionClass connectionClass;
    Button createGoalBut;
    ImageButton backGoalBut, addGoalBut, infoBut;
    EditText goalET;
    Dialog dialogCreate,dialogEdit2;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        connectionClass = new ConnectionClass();
        // Retrieve USER_ID from the intent
        userId = getIntent().getIntExtra("USER_ID", -1); // -1 is default if USER_ID not found
        Toast.makeText(this, "USER_ID: " + userId, Toast.LENGTH_SHORT).show();
        displayGoal();
        setContentView(R.layout.fragment_goal);
        createGoalBut = findViewById(R.id.createBut);;

        //create goal dialog
        LinearLayout goalContainer = findViewById(R.id.goalContainer);
        dialogCreate = new Dialog(Goal.this);
        dialogCreate.setContentView(R.layout.goal_create_box);
        dialogCreate.setCancelable(false);
        goalET = dialogCreate.findViewById(R.id.editText);
        backGoalBut = dialogCreate.findViewById(R.id.backButton);
        addGoalBut = dialogCreate.findViewById(R.id.addButton);
        infoBut = findViewById(R.id.infoBut);
        //edit goal 2 dialog
        dialogEdit2 = new Dialog(Goal.this);
        dialogEdit2.setContentView(R.layout.edit_goal_box2);
        dialogEdit2.setCancelable(true);

        infoBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Goal.this, "Long press on the goal to edit", Toast.LENGTH_SHORT).show();
            }
        });
        //when click back button in goal_create_box
        backGoalBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalET.setHint("Type your goal here...");
                dialogCreate.dismiss();
            }
        });
        //when click create goal in fragment_goal
        createGoalBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreate.show();
            }
        });
        addGoalBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                String goalUserSet = goalET.getText().toString();
                if (!goalUserSet.isEmpty()) {
                    saveGoalToDatabase(goalUserSet);
                    message = "Goal \"" + goalUserSet + "\" added successfully";
                }
                else message = "The goal is empty";
                goalET.setText(""); //clear input field
                dialogCreate.dismiss();
                Toast.makeText(Goal.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.GoalFragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void saveGoalToDatabase(String goalCreated) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            PreparedStatement insertStmt = null;
            PreparedStatement updateStmt = null;
            ResultSet generatedKeys = null;

            try {
                // Establish connection
                con = connectionClass.CONN();
                if (con != null) {
                    // Step 1: Insert the goal into the table
                    String insertQuery = "INSERT INTO goal (GOAL_CREATED, GOAL_STATUS, USER_ID) VALUES (?, ?, ?)";
                    insertStmt = con.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    insertStmt.setString(1, goalCreated); // GOAL_CREATED
                    insertStmt.setBoolean(2, false);     // GOAL_STATUS (unchecked)
                    insertStmt.setInt(3, userId);        // USER_ID

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        // Step 2: Retrieve the generated GOAL_ID
                        generatedKeys = insertStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int goalId = generatedKeys.getInt(1);
                            String cbId = "checkbox" + goalId; // Generate CB_ID

                            // Step 3: Update the goal with CB_ID
                            String updateQuery = "UPDATE goal SET CB_ID = ? WHERE GOAL_ID = ?";
                            updateStmt = con.prepareStatement(updateQuery);
                            updateStmt.setString(1, cbId); // CB_ID = "checkbox" + goalId
                            updateStmt.setInt(2, goalId);
                            updateStmt.executeUpdate();

                            // Step 4: Update the UI with the new goal
                            runOnUiThread(() -> {
                                displayGoal();
                                Toast.makeText(Goal.this, "Goal added successfully", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(Goal.this, "Failed to add goal", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Goal.this, "Database connection error", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Goal.this, "Database error", Toast.LENGTH_SHORT).show());
            } finally {
                // Close resources
                try {
                    if (generatedKeys != null) generatedKeys.close();
                    if (insertStmt != null) insertStmt.close();
                    if (updateStmt != null) updateStmt.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void displayGoal() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            PreparedStatement stmt = null;
            ResultSet resultSet = null;

            try {
                con = connectionClass.CONN();
                if (con != null) {
                    // SQL query to fetch goals for the current user
                    String query = "SELECT CB_ID, GOAL_CREATED, GOAL_STATUS FROM goal WHERE USER_ID = ?";
                    stmt = con.prepareStatement(query);
                    stmt.setInt(1, userId); // Set the current user's ID

                    resultSet = stmt.executeQuery();

                    // Fetch goals and add them as CheckBoxes
                    runOnUiThread(() -> {
                        LinearLayout goalContainer = findViewById(R.id.goalContainer);
                        goalContainer.removeAllViews();  // Clear existing views before adding new ones
                    });

                    while (resultSet.next()) {
                        String cbId = resultSet.getString("CB_ID");
                        String goalCreated = resultSet.getString("GOAL_CREATED");
                        boolean goalStatus = resultSet.getBoolean("GOAL_STATUS");

                        // Add CheckBox to the layout based on the retrieved data
                        runOnUiThread(() -> {
                            // Create CheckBox
                            CheckBox goalCheckBox = new CheckBox(Goal.this);
                            goalCheckBox.setId(View.generateViewId()); // Generate unique view ID
                            goalCheckBox.setTag(cbId); // Set CB_ID as tag to identify the checkbox
                            goalCheckBox.setText(goalCreated);
                            goalCheckBox.setChecked(goalStatus); // Check or uncheck based on GOAL_STATUS

                            // Set layout parameters
                            goalCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));

                            //Handle checkbox click to update GOAL_STATUS in the database
                            goalCheckBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                                updateGoalStatusInDatabase(cbId, isChecked1);
                            });
                            goalCheckBox.setOnLongClickListener(v -> {
                                showEditGoalDialog(cbId, goalCreated);
                                return true;
                            });
                            // Add CheckBox to the container
                            LinearLayout goalContainer = findViewById(R.id.goalContainer);
//                            goalCheckBox.setBackgroundResource(R.drawable.round_corner);
                            goalContainer.addView(goalCheckBox);
                        });
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Goal.this, "Database connection error", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Goal.this, "Error fetching goals", Toast.LENGTH_SHORT).show());
            } finally {
                // Close resources
                try {
                    if (resultSet != null) resultSet.close();
                    if (stmt != null) stmt.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void showEditGoalDialog(String cbId, String currentGoalText) {
        // Initialize dialogEdit2 for editing
        dialogEdit2.setCancelable(true);
        EditText editGoalET = dialogEdit2.findViewById(R.id.editGoalText2);
        Button saveButton = dialogEdit2.findViewById(R.id.saveButton);

        // Pre-fill the EditText with the current goal text
        editGoalET.setText(currentGoalText);

        // Show the dialog
        dialogEdit2.show();
        Button deleteButton = dialogEdit2.findViewById(R.id.deleteButton);
        //Handle delete button
        deleteButton.setOnClickListener(v ->{
            deleteGoalFromDatabase(cbId);
            dialogEdit2.dismiss();
        });
        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String updatedGoalText = editGoalET.getText().toString();

            if (!updatedGoalText.isEmpty()) {
                updateGoalTextInDatabase(cbId, updatedGoalText);
                dialogEdit2.dismiss();
            } else {
                Toast.makeText(Goal.this, "Goal cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGoalTextInDatabase(String cbId, String updatedGoalText) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = connectionClass.CONN();
                if (con != null) {
                    // SQL query to update GOAL_CREATED based on CB_ID
                    String query = "UPDATE goal SET GOAL_CREATED = ? WHERE CB_ID = ?";
                    stmt = con.prepareStatement(query);
                    stmt.setString(1, updatedGoalText);
                    stmt.setString(2, cbId);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        runOnUiThread(() -> {
                            Toast.makeText(Goal.this, "Goal updated successfully", Toast.LENGTH_SHORT).show();
                            // Refresh UI to reflect the updated goal
                            displayGoal();
                        });
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Goal.this, "Database connection error", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Goal.this, "Error updating goal", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateGoalStatusInDatabase(String cbId, boolean newStatus) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = connectionClass.CONN();
                if (con != null) {
                    String query = "UPDATE goal SET GOAL_STATUS = ? WHERE CB_ID = ?";
                    stmt = con.prepareStatement(query);
                    stmt.setBoolean(1, newStatus);
                    stmt.setString(2, cbId);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        runOnUiThread(() -> Toast.makeText(Goal.this, "Goal updated", Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Goal.this, "Error updating goal", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void deleteGoalFromDatabase(String cbId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = connectionClass.CONN();
                if (con != null) {
                    // SQL query to delete the goal by CB_ID
                    String query = "DELETE FROM goal WHERE CB_ID = ?";
                    stmt = con.prepareStatement(query);
                    stmt.setString(1, cbId);

                    int rowsDeleted = stmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        runOnUiThread(() -> {
                            Toast.makeText(Goal.this, "Goal deleted successfully", Toast.LENGTH_SHORT).show();
                            displayGoal(); // Refresh the UI
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(Goal.this, "Failed to delete goal", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(Goal.this, "Database connection error", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Goal.this, "Error deleting goal", Toast.LENGTH_SHORT).show());
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}