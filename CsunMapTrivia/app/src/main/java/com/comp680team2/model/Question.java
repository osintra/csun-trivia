/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * Question.java
 */

package com.comp680team2.model;

public class Question {
    private int difficulty;
    private String text;
    private Region answer;
    private String trivia;

    public Question(String text, int difficulty, Region answer, String trivia) {
        this.text = text;
        this.difficulty = difficulty;
        this.answer = answer;
        this.trivia = trivia;
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
