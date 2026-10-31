package com.example.gregsquizapp.OpenTDB;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenTDBQuestion {
    private String type;
    private String difficulty;
    private String category;
    private String question;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("incorrect_answers")
    private List<String> incorrectAnswers;

    public String getType() {
        return type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }
}