package com.example.navigation;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalFragment extends Fragment {
    ConnectionClass connectionClass;
    Button createGoalBut;
    ImageButton backGoalBut, addGoalBut, infoBut,IBBack;
    EditText goalET;
    Dialog dialogCreate, dialogEdit2;
    int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the userId from the parent activity (MainActivity)
        // Retrieve userId from arguments
        if (getActivity() instanceof MainActivity) {
            userId = ((MainActivity) getActivity()).getUserId();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectionClass = new ConnectionClass();
        displayGoal();
//        // Retrieve USER_ID from arguments
//        if (getArguments() != null) {
//            userId = getArguments().getInt("USER_ID", -1); // -1 is default if USER_ID not found
//        } else {
//            userId = -1;
//        }

//        Toast.makeText(requireContext(), "USER_ID: " + userId, Toast.LENGTH_SHORT).show();

        // Initialize UI elements
        createGoalBut = view.findViewById(R.id.createBut);
        infoBut = view.findViewById(R.id.infoBut);
        IBBack = view.findViewById(R.id.IBBackGoal);
        // Create goal dialog
        dialogCreate = new Dialog(requireContext());
        dialogCreate.setContentView(R.layout.goal_create_box);
        dialogCreate.setCancelable(false);
        goalET = dialogCreate.findViewById(R.id.editText);
        backGoalBut = dialogCreate.findViewById(R.id.backButton);
        addGoalBut = dialogCreate.findViewById(R.id.addButton);

        // Edit goal dialog
        dialogEdit2 = new Dialog(requireContext());
        dialogEdit2.setContentView(R.layout.edit_goal_box2);
        dialogEdit2.setCancelable(true);

        // Info button click listener
        infoBut.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Long press on the goal to edit", Toast.LENGTH_SHORT).show()
        );

        // Back button in goal_create_box
        backGoalBut.setOnClickListener(v -> {
            goalET.setHint("Type your goal here...");
            dialogCreate.dismiss();
        });

        // Create goal button in fragment_goal
        createGoalBut.setOnClickListener(v -> dialogCreate.show());

        // Add goal button in goal_create_box
        addGoalBut.setOnClickListener(v -> {
            String message;
            String goalUserSet = goalET.getText().toString();
            if (!goalUserSet.isEmpty()) {
                saveGoalToDatabase(goalUserSet);
                message = "Goal \"" + goalUserSet + "\" added successfully";
            } else {
                message = "The goal is empty";
            }
            goalET.setText(""); // Clear input field
            dialogCreate.dismiss();
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });

        IBBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment ProfileFragment = new ProfileFragment();
                FragmentManager fragmentManager = getParentFragmentManager(); // Get the FragmentManager for the parent activity
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // Start a FragmentTransaction
                fragmentTransaction.replace(R.id.frame_layout, ProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        // Adjust insets for fragment layout
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.GoalFragment), (v, insets) -> {
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
                            requireActivity().runOnUiThread(() -> {
                                displayGoal();
                                Toast.makeText(requireContext(), "Goal added successfully", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Failed to add goal", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Database connection error", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Database error", Toast.LENGTH_SHORT).show()
                );
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

                    // Clear existing views in the container
                    requireActivity().runOnUiThread(() -> {
                        View view = getView();
                        if (view != null) {
                            LinearLayout goalContainer = view.findViewById(R.id.goalContainer);
                            goalContainer.removeAllViews(); // Clear existing views
                        }
                    });

                    while (resultSet.next()) {
                        String cbId = resultSet.getString("CB_ID");
                        String goalCreated = resultSet.getString("GOAL_CREATED");
                        boolean goalStatus = resultSet.getBoolean("GOAL_STATUS");

                        // Add CheckBox to the layout based on the retrieved data
                        requireActivity().runOnUiThread(() -> {
                            View view = getView();
                            if (view != null) {
                                // Create CheckBox
                                CheckBox goalCheckBox = new CheckBox(requireContext());
                                goalCheckBox.setId(View.generateViewId()); // Generate unique view ID
                                goalCheckBox.setTag(cbId); // Set CB_ID as tag to identify the checkbox
                                goalCheckBox.setText(goalCreated);
                                goalCheckBox.setChecked(goalStatus); // Check or uncheck based on GOAL_STATUS

                                // Set layout parameters
                                goalCheckBox.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));

                                // Handle checkbox click to update GOAL_STATUS in the database
                                goalCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    updateGoalStatusInDatabase(cbId, isChecked);
                                });

                                // Handle long click to show edit goal dialog
                                goalCheckBox.setOnLongClickListener(v -> {
                                    showEditGoalDialog(cbId, goalCreated);
                                    return true;
                                });

                                // Add CheckBox to the container
                                LinearLayout goalContainer = view.findViewById(R.id.goalContainer);
                                goalContainer.addView(goalCheckBox);
                            }
                        });
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Database connection error", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error fetching goals", Toast.LENGTH_SHORT).show()
                );
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
        // Ensure dialogEdit2 is initialized and set for fragment context
        dialogEdit2.setCancelable(true);

        EditText editGoalET = dialogEdit2.findViewById(R.id.editGoalText2);
        Button saveButton = dialogEdit2.findViewById(R.id.saveButton);
        Button deleteButton = dialogEdit2.findViewById(R.id.deleteButton);

        // Pre-fill the EditText with the current goal text
        editGoalET.setText(currentGoalText);

        // Show the dialog
        dialogEdit2.show();

        // Handle delete button click
        deleteButton.setOnClickListener(v -> {
            deleteGoalFromDatabase(cbId);
            dialogEdit2.dismiss();
            Toast.makeText(requireContext(), "Goal deleted successfully", Toast.LENGTH_SHORT).show();
        });

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String updatedGoalText = editGoalET.getText().toString();

            if (!updatedGoalText.isEmpty()) {
                updateGoalTextInDatabase(cbId, updatedGoalText);
                dialogEdit2.dismiss();
                Toast.makeText(requireContext(), "Goal updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Goal cannot be empty", Toast.LENGTH_SHORT).show();
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
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Goal updated successfully", Toast.LENGTH_SHORT).show();
                            // Refresh UI to reflect the updated goal
                            displayGoal();
                        });
                    }
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Database connection error", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error updating goal", Toast.LENGTH_SHORT).show());
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
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Goal updated", Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error updating goal", Toast.LENGTH_SHORT).show());
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
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Goal deleted successfully", Toast.LENGTH_SHORT).show();
                            displayGoal(); // Refresh the UI
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Failed to delete goal", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Database connection error", Toast.LENGTH_SHORT).show());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error deleting goal", Toast.LENGTH_SHORT).show());
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