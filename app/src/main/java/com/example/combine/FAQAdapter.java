package com.example.combine;

import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private final List<MainActivity3.FAQItem> faqList; // Original list
    private List<MainActivity3.FAQItem> filteredList; // Filtered list
    private String searchQuery = ""; // To store the current search query
    private TextView noResultsTextView;

    // Constructor to initialize the list of FAQ items
    public FAQAdapter(List<MainActivity3.FAQItem> faqList, TextView noResultsTextView) {
        this.faqList = faqList;
        this.filteredList = new ArrayList<>(faqList); // Initially, both lists are the same
        this.noResultsTextView = noResultsTextView;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each FAQ item (faq_item.xml)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_item, parent, false);
        return new FAQViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        // Get the current FAQ item
        MainActivity3.FAQItem item = filteredList.get(position);

        // Highlight the question and answer texts
        holder.questionView.setText(highlightSearchTerm(item.question));
        holder.answerView.setText(highlightSearchTerm(item.answer));

        // Initially hide the answer
        holder.answerView.setVisibility(View.GONE);

        // Toggle visibility of the answer when the card is clicked
        holder.itemView.setOnClickListener(v -> {
            if (holder.answerView.getVisibility() == View.GONE) {
                holder.answerView.setVisibility(View.VISIBLE); // Show answer
                holder.symbolView.setText("-"); // Change symbol to "-"
            } else {
                holder.answerView.setVisibility(View.GONE); // Hide answer
                holder.symbolView.setText("+"); // Change symbol back to "+"
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of FAQ items
        return filteredList.size();
    }

    // Method to filter the list based on the query
    public void filter(String query) {
        searchQuery = query;  // Update the searchQuery
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(faqList); // If query is empty, show all items
        } else {
            query = query.toLowerCase();
            for (MainActivity3.FAQItem item : faqList) {
                if (item.question.toLowerCase().contains(query) || item.answer.toLowerCase().contains(query)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged(); // Notify the adapter to update the RecyclerView

        // Check if no items are found after filtering
        if (filteredList.isEmpty()) {
            noResultsTextView.setVisibility(View.VISIBLE); // Show "No result found" text
        } else {
            noResultsTextView.setVisibility(View.GONE); // Hide the "No result found" text
        }
    }

    // Method to highlight the search term in the text
    private CharSequence highlightSearchTerm(String text) {
        if (searchQuery.isEmpty()) {
            return text;
        }

        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.toLowerCase().indexOf(searchQuery.toLowerCase());

        if (startIndex >= 0) {
            int endIndex = startIndex + searchQuery.length();
            spannableString.setSpan(new android.text.style.BackgroundColorSpan(0xFFFFFF00), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Yellow background
            startIndex = text.toLowerCase().indexOf(searchQuery.toLowerCase(), endIndex); // Continue searching for next occurrence
        }

        return spannableString;
    }



    // ViewHolder class to hold references to the views in faq_item.xml
    static class FAQViewHolder extends RecyclerView.ViewHolder {
        TextView questionView, answerView, symbolView;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views
            questionView = itemView.findViewById(R.id.question_text);
            answerView = itemView.findViewById(R.id.answer_text);
            symbolView = itemView.findViewById(R.id.symbol_text);

            // Initially hide the answer when the view is created
            answerView.setVisibility(View.GONE); // the answer is hidden by default
        }
    }
}
