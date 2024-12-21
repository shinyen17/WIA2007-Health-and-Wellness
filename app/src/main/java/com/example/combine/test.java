package com.example.combine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class test extends AppCompatActivity {

    private questionAdapter adapter;
    private String selectedTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the selected test (DASS-21 or PHQ-9)
        Intent intent = getIntent();
        selectedTest = intent.getStringExtra("selectedTest");

        RecyclerView recyclerView = findViewById(R.id.questionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create questions list based on the selected test
        final List<question> questions = initializeQuestions(selectedTest);;

        //Use questions to set the adapter
        adapter = new questionAdapter(questions);
        recyclerView.setAdapter(adapter);

        // Submit button logic
        Button submitB = findViewById(R.id.submit);
        submitB.setOnClickListener(v -> {

            int[] answers = adapter.getSelectedAnswers();
            if (areAllQuestionsAnswered(answers)) {
                int totalScore = calculateTotalScore(answers, questions);

                if(selectedTest.equals("DASS-21")){
                    Intent resultIntent = new Intent(test.this, dassResult.class);
                    resultIntent.putExtra("totalScore", totalScore);
                    resultIntent.putExtra("selectedTest", selectedTest);
                    startActivity(resultIntent);
                }else if(selectedTest.equals("PHQ-9")){
                    Intent resultIntent = new Intent(test.this, phqResult.class);
                    resultIntent.putExtra("totalScore", totalScore);
                    resultIntent.putExtra("selectedTest", selectedTest);
                    startActivity(resultIntent);
                }

            } else {
                Toast.makeText(test.this, "Please answer all questions before submitting.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<question> initializeQuestions(String selectedTest) {
        List<question> questions = new ArrayList<>();

        // Initialize the questions based on the selected test
        if ("DASS-21".equals(selectedTest)) {
            questions = getDASS21Questions();
        } else if ("PHQ-9".equals(selectedTest)) {
            questions = getPHQ9Questions();
        }

        return questions;
    }

    private boolean areAllQuestionsAnswered(int[] answers) {
        for (int answer : answers) {
            if (answer == -1) return false;
        }
        return true;
    }

//    private int calculateTotalScore(int[] answers, List<question> questions) {
//        int totalScore = 0;
//        for (int i = 0; i < answers.length; i++) {
//            switch (questions.get(i).getOptions()[answers[i]]) {
//                case "Never":
//                    totalScore += 0;
//                    break;
//                case "Sometimes":
//                    totalScore += 1;
//                    break;
//                case "Often":
//                    totalScore += 2;
//                    break;
//                case "Almost Always":
//                    totalScore += 3;
//                    break;
//            }
//        }
//        return totalScore;
//    }

    private int calculateTotalScore(int[] answers, List<question> questions) {
        int totalScore = 0;
        for (int i = 0; i < answers.length; i++) {
            String selectedOption = questions.get(i).getOptions()[answers[i]];

            // Use if-else to check each condition
            if (selectedOption.equals("Never") || selectedOption.equals("Not at all")) {
                totalScore += 0;
            } else if (selectedOption.equals("Sometimes") || selectedOption.equals("Several days")) {
                totalScore += 1;
            } else if (selectedOption.equals("Often") || selectedOption.equals("More than half the days")) {
                totalScore += 2;
            } else if (selectedOption.equals("Almost Always") || selectedOption.equals("Nearly every day")) {
                totalScore += 3;
            }
        }
        return totalScore;
    }

    private List<question> getDASS21Questions(){
        // Create questions list
        List<question> questions = new ArrayList<>();
        questions.add(new question("1. I found it hard to wind down", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("2. I was aware of dryness of my mouth", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("3. I couldn’t seem to experience any positive feeling at all", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("4. I experienced breathing difficulty (eg, excessively rapid breathing, breathlessness in the absence of physical exertion)", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("5. I found it difficult to work up the initiative to do things", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("6. I tended to over-react to situations", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("7. I experienced trembling (eg, in the hands)", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("8. I felt that I was using a lot of nervous energy", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("9. I was worried about situations in which I might panic and make a fool of myself", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("10. I felt that I had nothing to look forward to", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("11. I found myself getting agitated", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("12. I found it difficult to relax", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("13. I felt down-hearted and blue", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("14. I was intolerant of anything that kept me from getting on with what I was doing", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("15. I felt I was close to panic", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("16. I was unable to become enthusiastic about anything", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("17. I felt I wasn’t worth much as a person", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("18. I felt that I was rather touchy", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("19. I was aware of the action of my heart in the absence of physical exertion (eg, sense of heart rate increase, heart missing a beat)", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("20. I felt scared without any good reason", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        questions.add(new question("21. I felt that life was meaningless", new String[]{"Never", "Sometimes", "Often", "Almost Always"}));
        return questions;
    }

    private List<question> getPHQ9Questions() {
        List<question> questions = new ArrayList<>();
        questions.add(new question("1. Little interest or pleasure in doing things", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("2. Feeling down, depressed, or hopeless", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("3. Trouble falling or staying asleep, or sleeping too much", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("4. Feeling tired or having little energy", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("5. Poor appetite or overating", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("6. Feeling bad about yourself or that you are failure or have let yourself or your family down", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("7. Trouble concentrating on things, such as reading the newspaper or wathcing televison", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("8. Moving or speaking so slowly that other people could have noticed. Or the opposite - being so figety or restless that you have been moving around a lot more than usual", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        questions.add(new question("9. Thoughts that you would be better off dead, or of hurting yourself", new String[]{"Not at all", "Several days", "More than half the days", "Nearly every day"}));
        return questions;
    }
}