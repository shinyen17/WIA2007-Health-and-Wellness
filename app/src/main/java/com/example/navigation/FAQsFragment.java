package com.example.navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FAQsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FAQsFragment extends Fragment {

    private List<FAQItem> faqList;
    private FAQAdapter faqAdapter;
    private TextView noResultsTextView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FAQsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FAQsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FAQsFragment newInstance(String param1, String param2) {
        FAQsFragment fragment = new FAQsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faqs, container, false);

        // Initialize and set up FAQ data
        setupFAQList();

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.faq_recycler_view);
        noResultsTextView = view.findViewById(R.id.noresult);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        faqAdapter = new FAQAdapter(faqList, noResultsTextView);
        recyclerView.setAdapter(faqAdapter);

        // Set up EditText for search
        EditText searchEditText = view.findViewById(R.id.search);
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

        return view;
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