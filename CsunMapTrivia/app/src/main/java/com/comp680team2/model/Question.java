package com.comp680team2.model;

/**
 * Created by Russell on 3/11/2015.
 */
public class Question {
    private int id;
    private int difficulty;
    private String text;
    private Region answer;
    private String trivia;

    public Question(int id, String text, int difficulty, Region answer, String trivia) {
        this.id = id;
        this.text = text;
        this.difficulty = difficulty;
        this.answer = answer;
        this.trivia = trivia;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Region getAnswer() {
        return answer;
    }

    public String getTrivia() {
        return trivia;
    }
}
