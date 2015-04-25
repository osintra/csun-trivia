/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * Leaderboard.java
 */

package com.comp680team2.model;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This singleton class keeps the game's leaderboard.
 * This is not thread safe
 */
public final class Leaderboard {
	//Static instances
	private static Leaderboard singleInstance = new Leaderboard();
	private static final int MAX_RECORDS = 1000;

	/**
	 * @return  the singleton instance of ScoreKeeper
	 */
	public static Leaderboard getLeaderboardSingleton() {
		return singleInstance;
	}

	//Dynamic instances
	private ArrayList<HighScore> highScores;

	//TODO: right now, it's reading data from a local file - change this to read from a database!!
	public Leaderboard() {
		highScores = new ArrayList<>();
		String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() +
				File.separatorChar + "KnowYourCampus_Temporary.txt";
		String name;
		int score;
		Scanner scanner;

		try {
			scanner = new Scanner(new FileInputStream(fileName));
			while (scanner.hasNextLine()) {
				name = scanner.nextLine();
				if (scanner.hasNextLine()) {
					score = scanner.nextInt();
					scanner.nextLine();
					addHighScore(new HighScore(name, score));
				}
			}
			scanner.close();
		} catch (Exception e) {
			submitLeaderboard();
		}
	}

	//TODO: change this to write to a database!!
	private void submitLeaderboard() {
		String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() +
				File.separatorChar + "KnowYourCampus_Temporary.txt";
		PrintWriter printer;

		try {
			printer = new PrintWriter(new FileOutputStream(fileName));
			for (int i = 0; i < highScores.size(); i++) {
				printer.println(highScores.get(i).getName());
				printer.println(highScores.get(i).getScore());
			}
			printer.close();
		} catch (Exception e) {
			Log.e("LEADERBOARD", "File couldn't be created. Out of memory...");
		}
	}

	public void addHighScore(HighScore highScore) {
		for (int i = 0; i < highScores.size(); i++) {
			if (highScore.getScore() > highScores.get(i).getScore()) {
				for (int j = i; j < highScores.size(); j++) {
					HighScore temporary = highScores.get(j);
					highScores.set(j, highScore);
					highScore = temporary;
				}
				break;
			}
		}
		if (highScores.size() < MAX_RECORDS) {
			highScores.add(highScore);
		}
		submitLeaderboard();
	}

	public ArrayList<String> getList() {
		ArrayList<String> result = new ArrayList<>();

		result.add(String.format("%-24s\t%-24s\t%-24s", "Rank", "Name", "Score"));
		for (int i = 0; i < highScores.size(); i++) {
			result.add(String.format("%-24d\t%-24s\t%-24d", i + 1, highScores.get(i).getName(), highScores.get(i).getScore()));
		}

		return result;
	}
}