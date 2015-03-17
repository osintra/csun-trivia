/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * ScoreActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends Activity
{
    private static String scoreToDisplay;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_activity);

		Button button1 = (Button)findViewById(R.id.scoreButton1);
		button1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				finish();
			}
		});

        TextView myTextView = (TextView) findViewById(R.id.scoreTextView2);
        myTextView.setText(scoreToDisplay);
	}

    static public void setScoreToDisplay(int score) {
        scoreToDisplay = "Score: " + String.valueOf(score);
    }
}