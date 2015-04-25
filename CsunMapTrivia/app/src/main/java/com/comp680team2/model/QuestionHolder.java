/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * QuestionHolder.java
 */

package com.comp680team2.model;

import java.util.List;

public class QuestionHolder {
    private List<Question> questionsList;
    private int numberOfQuestions = 0;

    public QuestionHolder(List<Question> fetchedListOfQuestions) {
        this.questionsList = fetchedListOfQuestions;
        this.numberOfQuestions = fetchedListOfQuestions.size();
    }

    public Question getQuestion(int index) {
        return questionsList.get(index);
    }
    public int getNumberOfQuestions() {return numberOfQuestions;}

}
