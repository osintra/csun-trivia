package com.comp680team2.controller;

import com.comp680team2.model.Coordinate;
import com.comp680team2.model.Question;
import com.comp680team2.model.QuestionHolder;
import com.comp680team2.model.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flexjson.JSONDeserializer;


public class GameController {

    private static final String REGION_KEY = "regions";
    private static final String REGION_ID_KEY = "r_id";
    private static final String REGION_LABEL_KEY = "r_label";
    private static final String COORDINATE_KEY = "coord";
    private static final String COORDINATE_X_KEY = "c_x";
    private static final String COORDINATE_Y_KEY = "c_y";
    private static final String QUESTION_TEXT_KEY = "q_text";
    private static final String QUESTION_TRIVIA_KEY = "trivia";

    HttpController httpController;

    public GameController() {
        httpController = new HttpController();
    }

    public QuestionHolder fetchQuestionSet() {
        String json = httpController.makeGetRequest("http://nullroute.cc/rest_test/knowYourCampus/fetch_game_config.php");
        Map<String, Object> responseObj = new JSONDeserializer<Map<String, Object>>().deserialize(json);

        return constructQuestionHolder(responseObj);
    }

    private QuestionHolder constructQuestionHolder(Map<String, Object> responseObj) {

        //constructs regions
        Map<String, Region> regionMap = constructRegionMap(responseObj);

        //constructs questions
        List<Question> questionList = constructQuestionList(responseObj, regionMap);

        return new QuestionHolder(questionList);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Region> constructRegionMap(Map<String, Object> responseObj) {
        Map<String, Object> regionStartMap = (Map<String, Object>)responseObj.get(REGION_KEY);
        Map<String, Region> regionEndMap = new HashMap<>();    //stores regions so they can be referenced by questions

        List<Map<String, Object>> coordinateStartList;
        List<Coordinate> coordinateEndList;

        double xValue, yValue;
        String regionLabel;

        Map<String, Object> regionStartConfig;
        for (String key : regionStartMap.keySet()) {
            regionStartConfig = (Map<String, Object>)regionStartMap.get(key);

            regionLabel = String.valueOf(regionStartConfig.get(REGION_LABEL_KEY));

            coordinateStartList = (List<Map<String, Object>>)regionStartConfig.get(COORDINATE_KEY);
            coordinateEndList = new ArrayList<>();

            for (Map<String, Object> coordinateStartConfig : coordinateStartList) {
                xValue = Double.parseDouble(String.valueOf(coordinateStartConfig.get(COORDINATE_X_KEY)));
                yValue = Double.parseDouble(String.valueOf(coordinateStartConfig.get(COORDINATE_Y_KEY)));
                coordinateEndList.add(new Coordinate(xValue, yValue));
            }

            regionEndMap.put(key, new Region(Integer.parseInt(key), regionLabel, coordinateEndList));
        }

        return regionEndMap;
    }

    @SuppressWarnings("unchecked")
    private List<Question> constructQuestionList(Map<String, Object> responseObj, Map<String, Region> regionMap) {
        List<Question> questionEndList = new ArrayList<>();

        int questionDifficulty;
        String questionText, questionTrivia;
        Region questionAnswer;

        List<Map<String, Object>> questionStartSubList;
        for (String key : responseObj.keySet()) {
            if (key.equalsIgnoreCase(REGION_KEY)) continue;

            questionStartSubList = (List<Map<String, Object>>)responseObj.get(key);
            for (Map<String, Object> questionStartConfig : questionStartSubList) {
                questionDifficulty = getDifficultyMapping(key.charAt(0));
                questionText = String.valueOf(questionStartConfig.get(QUESTION_TEXT_KEY));
                questionTrivia = String.valueOf(questionStartConfig.get(QUESTION_TRIVIA_KEY));
                questionAnswer = regionMap.get(String.valueOf(questionStartConfig.get(REGION_ID_KEY)));

                questionEndList.add(new Question(questionText, questionDifficulty, questionAnswer, questionTrivia));
            }
        }

        return questionEndList;
    }

    private int getDifficultyMapping(char diff) {
        switch (diff) {
            case 'e': return 0;
            case 'm': return 1;
            case 'h': return 2;
            case 'x': return 3;
        }
        return -1;  //error case
    }
}
