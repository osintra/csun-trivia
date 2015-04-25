/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * HighScore.java
 */

package com.comp680team2.model;

public class HighScore {
	private String name;
	private int score;

	public HighScore(String n, int s) {
		name = n;
		score = s;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}
}