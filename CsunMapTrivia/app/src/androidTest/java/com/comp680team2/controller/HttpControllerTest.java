package com.comp680team2.controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Russell on 3/11/2015.
 */
public class HttpControllerTest {

    HttpController httpController;

    @Before
    public void init() {
        httpController = new HttpController();
    }

    @Test
    public void testMakeRequest() throws IOException {
        httpController.makeRequest("http://nullroute.cc/rest_test/knowYourCampus/fetch_game_config.php");
    }

}
