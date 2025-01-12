package com.example.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class bmiCalculatorFragment extends Fragment {

    private TextInputEditText heightInput;
    private TextInputEditText weightInput;
    private Button btnCalculate;
    private TextView bmiResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bmi_calculator, container, false);

        // Initialize views
        heightInput = view.findViewById(R.id.heightInput);
        weightInput = view.findViewById(R.id.weightInput);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        bmiResult = view.findViewById(R.id.bmiResult);

        // Add back button click listener
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Calculate BMI button click listener
        btnCalculate.setOnClickListener(v -> calculateBMI());

        return view;
    }

    private void calculateBMI() {
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both height and weight", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float height = Float.parseFloat(heightStr) / 100; // Convert cm to m
            float weight = Float.parseFloat(weightStr);

            if (height <= 0 || weight <= 0) {
                Toast.makeText(getContext(), "Please enter valid values", Toast.LENGTH_SHORT).show();
                return;
            }

            float bmi = weight / (height * height);
            String category = getBMICategory(bmi);

            bmiResult.setVisibility(View.VISIBLE);
            bmiResult.setText(String.format("%.1f (%s)", bmi, category));

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBMICategory(float bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
}
