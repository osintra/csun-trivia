package com.comp680team2.model;

import java.util.List;

public class QuestionHolder {
    private List<Question> questions;

    public QuestionHolder(List<Question> questions) {
        this.questions = questions;
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

}
