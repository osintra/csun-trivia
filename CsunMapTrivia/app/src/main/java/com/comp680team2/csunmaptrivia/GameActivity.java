/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * GameActivity.java
 */

package com.comp680team2.csunmaptrivia;

import java.util.Random;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp680team2.model.ScoreKeeper;

public class GameActivity extends Activity
{
	private TextView textView;
	private double scale;
    private ScoreKeeper scoreKeeper = null;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity);
        scoreKeeper = ScoreKeeper.getScoreKeeperSingleton();

		PseudoDatabase database = new PseudoDatabase(this, R.raw.trivia_database); // Object representation of the database
		int difficulties = 3, databaseSection = 10, chosenSection = 4;
		int totalChosen = difficulties * chosenSection;
		int[] locationId = new int[totalChosen];
		String[] locationName = new String[totalChosen];
		int[] locationX = new int[totalChosen];
		int[] locationY = new int[totalChosen];
		int[] locationWidth = new int[totalChosen];
		int[] locationHeight = new int[totalChosen];
		String[] locationQuestion = new String[totalChosen];
		String[] locationTrivia = new String[totalChosen];
		int[][] databaseIndex = new int[difficulties][databaseSection];
		Random generator = new Random();

		for (int i = 0; i < difficulties; i++)
		{
			// Initialize the indices to choose from
			for (int j = 0; j < databaseSection; j++)
			{
				databaseIndex[i][j] = j + (databaseSection * i);
			}
			// Shuffle the indices to choose from
			for (int j = 0, randomIndex, temporary; j < databaseSection; j++)
			{
				randomIndex = generator.nextInt(databaseSection);
				temporary = databaseIndex[i][j];
				databaseIndex[i][j] = databaseIndex[i][randomIndex];
				databaseIndex[i][randomIndex] = temporary;
			}
			// Make database queries based on a small amount of randomized indices to populate the game's data
			for (int j = 0, dataIndex; j < chosenSection; j++)
			{
				dataIndex = j + (chosenSection * i);
				locationId[dataIndex] = Integer.parseInt(database.getValue(0, databaseIndex[i][j]));
				locationName[dataIndex] = database.getValue(1, databaseIndex[i][j]);
				locationX[dataIndex] = Integer.parseInt(database.getValue(2, databaseIndex[i][j]));
				locationY[dataIndex] = Integer.parseInt(database.getValue(3, databaseIndex[i][j]));
				locationWidth[dataIndex] = Integer.parseInt(database.getValue(4, databaseIndex[i][j]));
				locationHeight[dataIndex] = Integer.parseInt(database.getValue(5, databaseIndex[i][j]));
				locationQuestion[dataIndex] = database.getValue(generator.nextInt(2) + 6, databaseIndex[i][j]);
				locationTrivia[dataIndex] = database.getValue(generator.nextInt(2) + 8, databaseIndex[i][j]);
			}
		}

		textView = (TextView)findViewById(R.id.gameTextView2);

		ImageView map = (ImageView)findViewById(R.id.gameImageView1);
		map.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View view, MotionEvent event)
			{
				//int x = (int)(event.getX() * scale);
				//int y = (int)(event.getY() * scale);
                scoreKeeper.addPoints(1);
                textView.setText("Score = " + String.valueOf(scoreKeeper.getCurrentScore()));
				//textView.setText("(" + x + ", " + y + ")");
				return false;
			}
		});

		int mapWidth = 1080; //The width of the map's display
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		scale = (double)mapWidth / screenWidth;

		Button button1 = (Button)findViewById(R.id.gameButton1);
		button1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
                scoreKeeper.submitCurrentScore();
				scoreKeeper.resetCurrentScore();
                finish();
			}
		});
	}

    // Will not support platform versions older than 2.0
    @Override
    public void onBackPressed() {
        scoreKeeper.submitCurrentScore();
        scoreKeeper.resetCurrentScore();
        finish();
    }
}