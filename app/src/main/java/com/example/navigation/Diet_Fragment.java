package com.example.navigation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Diet_Fragment extends Fragment {
    ConnectionClass connectionClass;
    Connection con;
    ResultSet rs;
    ArrayList<String> foodList = new ArrayList<>();
    ArrayList<String> foodIds = new ArrayList<>();
    ArrayList<Integer> foodCalories = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String[] recipedata = new String[5];

    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            userId = mainActivity.getUserId();  // Get the userId
        }

        connectionClass = new ConnectionClass();

        connect();

        View view = inflater.inflate(R.layout.fragment_diet_1, container, false);

        // Reference to the ListView in your fragment layout
        ListView listView = view.findViewById(R.id.listView_recipe);

        // Create an ArrayAdapter to populate the ListView
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                foodList
        );

        // Set the adapter to the ListView
        listView.setAdapter(adapter);

        // Set up item click listener
        listView.setOnItemClickListener((AdapterView<?> parent, View itemView, int position, long id) -> {
            // Get the selected food item and Food_ID
            String selectedFood = foodList.get(position);
            String selectedFoodId = foodIds.get(position);
            int selectedCalories = foodCalories.get(position);

            // Show a confirmation dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Record")
                    .setMessage("Do you want to record " + selectedFood + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Record the food into the database
                        recordFood(selectedFoodId,selectedCalories);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });


        TextView btnadd = view.findViewById((R.id.addfood));
        btnadd.setOnClickListener(v1 -> {
            // Inflate the layout for the pop-out window
            View addcalories = inflater.inflate(R.layout.calories_popout, container, false);

            // Create and set up the AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            Button record = addcalories.findViewById(R.id.AddButton);
            record.setOnClickListener(v2 -> {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    // Extract input data
                    String mealName = ((EditText) addcalories.findViewById(R.id.NameInput)).getText().toString();
                    int calories = Integer.parseInt(((EditText) addcalories.findViewById(R.id.caloriesInput)).getText().toString());

                    Connection con = null;
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;

                    try {
                        con = connectionClass.CONN(); // Database connection
                        if (con != null) {
                            String caloriesRecordId = UUID.randomUUID().toString().substring(0, 8); // Use first 8 chars of UUID
                            String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                            String insertQuery = "INSERT INTO user_food (Calories_Record_ID, Timestamp, Calories, User_ID) VALUES (?, ?, ?, ?)";
                            PreparedStatement pstmt4 = con.prepareStatement(insertQuery);
                            pstmt4.setString(1, caloriesRecordId); // Unique Calories_Record_ID
                            pstmt4.setString(2, currentTimestamp); // Current Timestamp
                            pstmt4.setInt(3, calories); // Meal Calories
                            pstmt4.setInt(4, userId); // User_ID
                            pstmt4.executeUpdate();


                        } else {
                            Log.e("FAIL", "Database connection is null.");
                        }
                    } catch (Exception e) {
                        Log.e("ERROR", "Error inserting meal data: " + e.getMessage());
                    } finally {
                        try {
                            if (rs != null) rs.close();
                            if (pstmt != null) pstmt.close();
                            if (con != null) con.close();
                        } catch (SQLException e) {
                            Log.e("ERROR", "Error closing database resources: " + e.getMessage());
                        }
                    }

                });
            });
            builder.setView(addcalories)
                    .setPositiveButton("Close", (dialog, which) ->{
                        dialog.dismiss();
                    });
            builder.create().show();
        });





        // Reference to the TextView (Hyperlink)
        TextView hyperlink = view.findViewById(R.id.Hyperlink);
        hyperlink.setPaintFlags(hyperlink.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        hyperlink.setOnClickListener(v -> {
            connectMeal();
            View recipeView = inflater.inflate(R.layout.healthy_recipe, container, false);
            ((ViewGroup) view).removeAllViews();
            ((ViewGroup) view).addView(recipeView);
            ListView listViewRecipe = view.findViewById(R.id.listView_recipe);
            //connectRecipe("m001");
            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, foodList);
            listViewRecipe.setAdapter(adapter);

            listViewRecipe.setOnItemClickListener((AdapterView<?> parent, View itemView, int position, long id) -> {
                // Get the selected Food_ID
                String selectedFoodId = foodIds.get(position);
                String selectedFood = foodList.get(position);

                // Show a popout with meal details
                connectRecipe(selectedFoodId);
                //openMealDetails();
            });

            TextView hyperlink1 = view.findViewById(R.id.Hyperlink);
            hyperlink1.setPaintFlags(hyperlink.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            hyperlink1.setOnClickListener(v1 -> {
                // Inflate the layout for the pop-out window
                View addrecipeView = inflater.inflate(R.layout.addreceipe_popout, container, false);

                // Create and set up the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                Button addrecipe = addrecipeView.findViewById(R.id.AddButton);
                addrecipe.setOnClickListener(v2 -> {
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        // Extract input data
                        String mealName = ((EditText) addrecipeView.findViewById(R.id.NameInput)).getText().toString();
                        String ingredients = ((MultiAutoCompleteTextView) addrecipeView.findViewById(R.id.ingredientInput)).getText().toString();
                        String steps = ((MultiAutoCompleteTextView) addrecipeView.findViewById(R.id.StepsInput)).getText().toString();
                        int calories = Integer.parseInt(((EditText) addrecipeView.findViewById(R.id.caloriesInput)).getText().toString());

                        Connection con = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;

                        try {
                            con = connectionClass.CONN(); // Database connection
                            if (con != null) {
                                // Step 1: Generate Meal_ID/Food_ID
                                String mealFoodId = "m" + UUID.randomUUID().toString().substring(0, 3); // Auto-generate Meal_ID/Food_ID

// Step 2: Insert into diet_meal
                                String mealQuery = "INSERT INTO diet_meal (Meal_ID, User_ID, Meal_Name, Ingredients, Recipe, Meal_Calories) VALUES (?, ?, ?, ?, ?, ?)";
                                PreparedStatement pstmt1 = con.prepareStatement(mealQuery);
                                pstmt1.setString(1, mealFoodId); // Using same Meal_ID as Food_ID
                                pstmt1.setInt(2, userId); // Assuming User_ID = 1
                                pstmt1.setString(3, mealName);
                                pstmt1.setString(4, ingredients);
                                pstmt1.setString(5, steps); // Recipe steps
                                pstmt1.setInt(6, calories);
                                pstmt1.executeUpdate();

// Step 3: Insert into diet_food
                                String foodQuery = "INSERT INTO diet_food (Food_ID, Food_Name, Food_Type, Food_Calories) VALUES (?, ?, 'user', ?)";
                                pstmt1 = con.prepareStatement(foodQuery);
                                pstmt1.setString(1, mealFoodId); // Using same ID
                                pstmt1.setString(2, mealName);
                                pstmt1.setInt(3, calories);
                                pstmt1.executeUpdate();



                            } else {
                                Log.e("FAIL", "Database connection is null.");
                            }
                        } catch (Exception e) {
                            Log.e("ERROR", "Error inserting meal data: " + e.getMessage());
                        } finally {
                            try {
                                if (rs != null) rs.close();
                                if (pstmt != null) pstmt.close();
                                if (con != null) con.close();
                            } catch (SQLException e) {
                                Log.e("ERROR", "Error closing database resources: " + e.getMessage());
                            }
                        }

                    });
                });
                builder.setView(addrecipeView)
                        .setPositiveButton("Close", (dialog, which) ->{
                            connectMeal();
                            /*View recipeView = inflater.inflate(R.layout.healthy_recipe, container, false);
                            ((ViewGroup) view).removeAllViews();
                            ((ViewGroup) view).addView(recipeView);
                            ListView listViewRecipe = view.findViewById(R.id.listView_recipe);
                            //connectRecipe("m001");*/
                            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, foodList);
                            listViewRecipe.setAdapter(adapter);

                            listViewRecipe.setOnItemClickListener((AdapterView<?> parent, View itemView, int position, long id) -> {
                                // Get the selected Food_ID
                                String selectedFoodId = foodIds.get(position);
                                String selectedFood = foodList.get(position);

                                // Show a popout with meal details
                                connectRecipe(selectedFoodId);
                                //openMealDetails();
                            });
                            dialog.dismiss();
                        });
                builder.create().show();
            });





        });

        return view;
    }

    public void connect() {
        Log.e("MASUK", "Inside connect");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Log.e("DEBUG", "Inside ExecutorService task");
            try {
                con = connectionClass.CONN();
                if (con != null) {
                    Log.e("SUCCESS", "Database connection established successfully!");
                    fetchFoodData();
                } else {
                    Log.e("FAIL", "Database connection is null.");
                }
            } catch (Exception e) {
                Log.e("FAIL", "Database connection failed: " + e.getMessage());
            }
        });
    }

    public void connectMeal() {
        Log.e("MASUK", "Inside connect");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Log.e("DEBUG", "Inside ExecutorService task");
            try {
                con = connectionClass.CONN();
                if (con != null) {
                    Log.e("SUCCESS", "Database connection established successfully!");
                    fetchMealData();
                } else {
                    Log.e("FAIL", "Database connection is null.");
                }
            } catch (Exception e) {
                Log.e("FAIL", "Database connection failed: " + e.getMessage());
            }
        });
    }

    public void connectRecipe(String mealId) {
        Log.e("MASUK", "Inside connect");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Log.e("DEBUG", "Inside ExecutorService task");
            try {
                con = connectionClass.CONN();
                if (con != null) {
                    Log.e("SUCCESS", "Database connection established successfully!");
                    fetchRecipeData(mealId);
                    requireActivity().runOnUiThread(() -> openMealDetails());
                } else {
                    Log.e("FAIL", "Database connection is null.");
                }
            } catch (Exception e) {
                Log.e("FAIL", "Database connection failed: " + e.getMessage());
            }
        });
    }

    private void fetchFoodData() {
        try {
            String query = "SELECT Food_ID, Food_Name, Food_Calories FROM diet_food";
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            // Clear the existing food list to avoid duplicates
            foodList.clear();
            foodIds.clear();
            foodCalories.clear();  // Don't forget to clear the foodCalories list

            while (rs.next()) {
                String foodId = rs.getString("Food_ID");
                String foodName = rs.getString("Food_Name");
                int foodCalories = rs.getInt("Food_Calories");
                foodIds.add(foodId);
                foodList.add(foodName + " - " + foodCalories + " kcal");
                this.foodCalories.add(foodCalories);  // Add calories to the list
            }

            // Update the ListView on the main thread
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

        } catch (Exception e) {
            Log.e("FETCH_ERROR", "Error fetching data: " + e.getMessage());
        }
    }

    private void fetchMealData() {
        try {
            String query = "SELECT Meal_ID, Meal_Name, Meal_Calories FROM diet_meal";
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            // Clear existing lists to avoid duplicates
            foodList.clear();
            foodIds.clear();

            while (rs.next()) {
                String mealId = rs.getString("Meal_ID");
                String mealName = rs.getString("Meal_Name");
                int mealCalories = rs.getInt("Meal_Calories");
                foodIds.add(mealId);
                foodList.add(mealName + " - " + mealCalories + " kcal");
            }

            // Update the ListView on the main thread
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

        } catch (Exception e) {
            Log.e("FETCH_ERROR", "Error fetching meal data: " + e.getMessage());
        }
    }


    private void recordFood(String foodId, int calories) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Generate unique Calories_Record_ID
                String caloriesRecordId = UUID.randomUUID().toString().substring(0, 8); // Use first 8 chars of UUID
                String user_Id = Integer.toString(userId); // Replace with dynamic user ID if available
                String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Updated query: Insert calories value instead of food_id
                String query = "INSERT INTO user_food (Calories_Record_ID, Timestamp, Calories, User_ID) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, caloriesRecordId);
                pstmt.setString(2, currentTimestamp);
                pstmt.setInt(3, calories);  // Use the calories value
                pstmt.setString(4, user_Id);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Food recorded successfully!", Toast.LENGTH_SHORT).show());
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Failed to record food.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("INSERT_ERROR", "Error inserting data: " + e.getMessage());
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error recording food.", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void openMealDetails() {
        View recipeView = getLayoutInflater().inflate(R.layout.recipe_popout, null);

        // Get views and set data
        TextView nameTextView = recipeView.findViewById(R.id.HealthyRecipeTV);
        TextView ingredientsTextView = recipeView.findViewById(R.id.ingredientslistTV);
        TextView stepsTextView = recipeView.findViewById(R.id.stepslistTV);
        TextView caloriesTextView = recipeView.findViewById(R.id.recipe_caloriesTV);

        nameTextView.setText(recipedata[0]);
        ingredientsTextView.setText(recipedata[2]);
        stepsTextView.setText(recipedata[3]);
        caloriesTextView.setText(recipedata[1] + " kCal");

        Button recordCaloriesButton = recipeView.findViewById(R.id.recordCalories);
        recordCaloriesButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Record")
                    .setMessage("Do you want to record " + recipedata[0]+ "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Log.e("check",recipedata[4]);
                        recordFood(recipedata[4], Integer.parseInt(recipedata[1]));
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        // Display the pop-out view
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(recipeView);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void fetchRecipeData(String mealId) {
        try {
            String query = "SELECT * FROM diet_meal WHERE meal_ID = ? ";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, mealId); // Set the meal ID in the query
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                /*String mealName = rs.getString("Meal_Name");
                int mealCalories = rs.getInt("Meal_Calories");
                String recipe = rs.getString("Recipe");
                String food_id = mealId;

                // Assuming recipedata is an array or list with appropriate length
                recipedata[0] = mealName;
                recipedata[1] = Integer.toString(mealCalories);
                recipedata[2] = ingredients;
                recipedata[3] = recipe;
                recipedata[4] = food_id;*/

                recipedata[0] = rs.getString("Meal_Name");
                recipedata[1] = Integer.toString(rs.getInt("Meal_Calories"));
                recipedata[2] = rs.getString("Ingredients");
                recipedata[3] = rs.getString("Recipe");
                recipedata[4] = mealId;String ingredients = rs.getString("Ingredients");




            } else {
                Log.e("FETCH_ERROR", "No meal found with ID: " + mealId);
            }

        } catch (Exception e) {
            Log.e("FETCH_ERROR", "Error fetching meal data: "+ mealId + e.getMessage());
        }
    }




}
