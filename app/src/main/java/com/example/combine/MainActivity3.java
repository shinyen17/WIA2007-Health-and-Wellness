package com.example.combine;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private List<FAQItem> faqList;
    private FAQAdapter faqAdapter;
    private TextView noResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize and set up FAQ data
        setupFAQList();

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.faq_recycler_view);
        noResultsTextView = findViewById(R.id.noresult);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        faqAdapter = new FAQAdapter(faqList, noResultsTextView);
        recyclerView.setAdapter(faqAdapter);

        // Set up EditText for search
        EditText searchEditText = findViewById(R.id.search);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filter FAQ items as the user types
                faqAdapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setupFAQList() {
        faqList = new ArrayList<>();
        faqList.add(new FAQItem("What is mental health ?", "We all have mental health, which is made up of our beliefs, thoughts, feelings, and behaviours."));
        faqList.add(new FAQItem("What causes mental health problems ?", "Challenges or problems with your mental health can arise from psychological, biological, and social issues, as well as life events."));
        faqList.add(new FAQItem("What do I do if the support doesn't help ?", "It can be difficult to find the things that will help you, as different things help different people. It's important to be open to a range of approaches and to be committed to finding the right help and to continue to be hopeful, even when some things don't work out."));
        faqList.add(new FAQItem("Can you prevent mental health problems ?", "We can all suffer from mental health challenges, but developing our wellbeing, resilience, and seeking help early can help prevent challenges from becoming serious."));
        faqList.add(new FAQItem("Are there cures for mental health problems ?", "It is often more realistic and helpful to find out what helps with the issues you face. Talking, counseling, medication, friendships, exercise, good sleep and nutrition, and meaningful occupation can all help."));
        faqList.add(new FAQItem("What do I do if I'm worried about my mental health ?", "The most important thing is to talk to someone you trust. This might be a friend, colleague, family member, or GP."));
        faqList.add(new FAQItem("How do I know if I'm unwell?", "If your beliefs, thoughts, feelings, or behaviours have a significant impact on your ability to function in what might be considered a normal or ordinary way, it would be important to seek help."));
        faqList.add(new FAQItem("What should I do if I'm worried about a friend or relative ?", "Gently encouraging someone to seek appropriate support would be helpful to start with."));
        faqList.add(new FAQItem("How do I deal with someone telling me what to do ?", "Some people may advise you on good evidence of what works with the best of intentions, but it's important to find out what works best for you. "));
        faqList.add(new FAQItem("Can online tests and checklists diagnose me?", "No. Online mental health tests are a good way to start tracking your mental health and understanding what challenges you might be facing. However, they can't replace the expertise and judgment of a qualified clinical psychologist."));
        faqList.add(new FAQItem("How do I get a diagnosis?", "To get a diagnosis, you need to see a clinical psychologist. They will ask detailed questions about your life, which online tests can't provide. The process may take one or more sessions, but a professional's help is essential for an accurate diagnosis and treatment plan."));
    }

    static class FAQItem {
        String question;
        String answer;

        FAQItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

    }
}