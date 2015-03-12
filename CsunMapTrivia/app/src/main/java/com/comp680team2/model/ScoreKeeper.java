package com.comp680team2.model;

/**
 * Created by M.R. Velasquez on 3/12/15.
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
     * Adds points to the current score
     * @param points    the point to be added to the current score
     */
    public void addPoints(int points) {
        this.currentScore = currentScore + points;
    }
}
