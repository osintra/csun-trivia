package com.comp680team2.model;

import com.comp680team2.csunmaptrivia.ScoreActivity;

/**
 * This singleton class keeps the game's current score. The current score is supposed to be updated
 * after a question is answered/runs out of time
 * This is not thread safe
 */
public final class ScoreKeeper {
    private int currentScore;
    private static ScoreKeeper singleInstance = new ScoreKeeper();

    private ScoreKeeper() {
        currentScore = 0;
    }


    /**
     * @return  the singleton instance of ScoreKeeper
     */
    public static ScoreKeeper getScoreKeeperSingleton() {
        return singleInstance;
    }


    /**
     * @return  the current score
     */
    public int getCurrentScore() {
        return currentScore;
    }


    /**
     * Add points to the current score
     * @param points    the point to be added to the current score
     */
    public void addPoints(int points) {
        this.currentScore = currentScore + points;
    }


    /**
     * Add 1 (one) point to the current score
     */
    public void addPoint() {
        this.currentScore = currentScore + 1;
    }


    /**
     * Reset current score to 0 (zero)
     */
    public void resetCurrentScore() {
        currentScore = 0;
    }


    /**
     * Submit current score
     */
    public void submitCurrentScore() {
        ScoreActivity.setScoreToDisplay(currentScore);
    }
    //TODO: implement Google Play leaderboard current score submission for second sprint
}
