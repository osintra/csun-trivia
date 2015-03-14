package com.comp680team2.controller;

import com.comp680team2.model.QuestionHolder;

import java.io.IOException;

/**
 * Created by Russell on 3/11/2015.
 */
public class GameController {

    HttpController httpController;

    public GameController() {
        httpController = new HttpController();
    }

    /** TODO: make this */
    public QuestionHolder fetchQuestionSet() {
        String json = httpController.makeRequest("http://nullroute.cc/rest_test/knowYourCampus/fetch_game_config.php");


        return null;
    }
}
