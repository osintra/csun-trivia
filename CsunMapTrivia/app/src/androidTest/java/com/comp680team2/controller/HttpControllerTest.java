package com.comp680team2.controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class HttpControllerTest {

    HttpController httpController;

    @Before
    public void init() {
        httpController = new HttpController();
    }

    @Test
    public void testMakeRequest() throws IOException {
        httpController.makeGetRequest("http://nullroute.cc/rest_test/knowYourCampus/fetch_game_config.php");
    }

}
