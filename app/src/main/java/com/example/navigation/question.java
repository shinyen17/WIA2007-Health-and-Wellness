package com.example.navigation;

public class question {
    private String questionText;
    private String[] options;

    public question(String questionText, String[] options) {
        this.questionText = questionText;
        this.options = options;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }
}
