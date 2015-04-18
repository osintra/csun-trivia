/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * SubmitActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitActivity extends Activity
{
    private static String scoreToDisplay;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_activity);

		TextView myTextView = (TextView) findViewById(R.id.score_activity_scoreTextView);
        myTextView.setText(scoreToDisplay);

		Button button1 = (Button)findViewById(R.id.submitButton1);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(getBaseContext(), "Your score was successfully submitted!", Toast.LENGTH_LONG).show();
				//Score functionality goes here
			}
		});

		if (scoreToDisplay.charAt(0) == 'Y') {
			button1.setVisibility(View.VISIBLE);
		} else {
			button1.setVisibility(View.INVISIBLE);
		}

		Button button2 = (Button)findViewById(R.id.submitButton2);
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(getBaseContext(), "(Leaderboard functionality goes here.)", Toast.LENGTH_LONG).show();
			}
		});
	}

    public static void setScoreToDisplay(int score) {
		if (score > 0) {
			scoreToDisplay = "Your last game's score was " + score + ".\n\n" +
					"To submit this score, press the SEND button. " +
					"To see the global high scores, press the LEADERBOARD button.";

		} else {
			scoreToDisplay = "To submit a score, first play a game of Know Your Campus. " +
					"When finished, come back here and press the SEND button. " +
					"To see the global high scores, press the LEADERBOARD button.";
		}
    }
}