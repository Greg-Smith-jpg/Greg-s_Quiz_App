package com.example.gregsquizapp.Tournament;

import com.example.gregsquizapp.Questions.QuestionClass;

import java.util.ArrayList;
import java.util.List;

public class TournamentClass {
    private String name;
    private String tournamentID;
    private int categoryID;
    private String categoryName;
    private String difficulty;
    private String startDate;
    private String endDate;
    private List<QuestionClass> questions = new ArrayList<>();
    public TournamentClass() {
    }

    public String getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(String tournamentID) {
        this.tournamentID = tournamentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<QuestionClass> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionClass> questions) {
        this.questions = questions;
    }
}
