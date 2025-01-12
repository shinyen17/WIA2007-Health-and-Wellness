package com.example.navigation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class questionAdapter extends RecyclerView.Adapter<questionAdapter.QuestionViewHolder> {

    private List<question> dassquestions;
    private int[] selectedAnswers;
    private static final float TRANSPARENCY_LEVEL = 0.5f; // Adjusted transparency level (50% opacity)

    public questionAdapter(List<question> questions) {
        this.dassquestions = questions;
        this.selectedAnswers = new int[questions.size()];
        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1;  // Initialize to -1, indicating no selection
        }
    }


    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, int position) {
        question question = dassquestions.get(position);
        holder.questionText.setText(question.getQuestionText());

        // Reset the color and transparency of the previously selected question
        resetQuestionColor(holder.questionText);
        resetRadioGroupTransparency(holder.radioGroup);

        // Clear any existing radio buttons in the RadioGroup before adding new ones
        holder.radioGroup.removeAllViews();

        String[] options = question.getOptions();

        // Create and add a new RadioButton for each option
        for (int i = 0; i < options.length; i++) {
            RadioButton radioButton = new RadioButton(holder.itemView.getContext());
            radioButton.setText(options[i]);
            radioButton.setTextSize(9);

            // Set layout params for the RadioButton to make them evenly spaced
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);  // '1f' makes each button take equal space
            radioButton.setLayoutParams(params);

            // Set the checked state based on the saved answer
            if (selectedAnswers[position] == i) {
                radioButton.setChecked(true);
            }

            // Set up the listener to store the selected answer
            final int index = i;
            radioButton.setOnClickListener(v -> {
                selectedAnswers[position] = index;
                setRadioGroupTransparency(holder.radioGroup);
                changeQuestionColor(holder.questionText);
            });
            holder.radioGroup.addView(radioButton);
        }
    }


    @Override
    public int getItemCount() {
        return dassquestions.size();
    }

    public int[] getSelectedAnswers() {
        return selectedAnswers;
    }

    // Method to set the transparency of the RadioGroup
    private void setRadioGroupTransparency(RadioGroup radioGroup) {
        radioGroup.setAlpha(TRANSPARENCY_LEVEL);  // Set transparency directly (50% opacity)
    }

    // Method to change the TextView color when an option is selected
    private void changeQuestionColor(TextView questionText) {
        questionText.setTextColor(Color.GRAY); // Change to your desired color
    }

    // Reset the TextView color when the question is not selected
    private void resetQuestionColor(TextView questionText) {
        questionText.setTextColor(ContextCompat.getColor(questionText.getContext(), R.color.blue));
    }

    // Reset the transparency of the RadioGroup when the question is not selected
    private void resetRadioGroupTransparency(RadioGroup radioGroup) {
        radioGroup.setAlpha(1f); // Set transparency back to normal (100% opacity)
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        RadioGroup radioGroup;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            radioGroup = itemView.findViewById(R.id.radioGroup);
        }
    }
}
