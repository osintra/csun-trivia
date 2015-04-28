/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * ScoreKeeper.java
 */

package com.comp680team2.model;

/**
 * This singleton class keeps the game's current score. The current score is supposed to be updated
 * after a question is answered/runs out of time
 * This is not thread safe
 */
public final class ScoreKeeper {
	//Static instances
    private static ScoreKeeper singleInstance = new ScoreKeeper();

	/**
	 * @return  the singleton instance of ScoreKeeper
	 */
	public static ScoreKeeper getScoreKeeperSingleton() {
		return singleInstance;
	}

	//Dynamic instances
	private int currentScore;

    private ScoreKeeper() {
        currentScore = 0;
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
     * Reset current score to 0 (zero)
     */
    public void resetCurrentScore() {
        currentScore = 0;
    }
}
