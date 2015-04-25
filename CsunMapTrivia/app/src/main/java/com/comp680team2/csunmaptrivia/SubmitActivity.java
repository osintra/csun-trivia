/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * SubmitActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comp680team2.model.HighScore;
import com.comp680team2.model.Leaderboard;
import com.comp680team2.model.ScoreKeeper;

public class SubmitActivity extends Activity
{
	private Leaderboard leaderboard;
	private ScoreKeeper scoreKeeper;
	private EditText editText;
	private boolean submitted;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_activity);

		leaderboard = Leaderboard.getLeaderboardSingleton();
		scoreKeeper = ScoreKeeper.getScoreKeeperSingleton();

		editText = (EditText)findViewById(R.id.submitEditText1);

		TextView textView = (TextView)findViewById(R.id.submitTextView3);
		textView.setText("Score: " + scoreKeeper.getCurrentScore());

		Button button1 = (Button)findViewById(R.id.submitButton1);
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!editText.getText().toString().equals("")) {
					leaderboard.addHighScore(new HighScore(editText.getText().toString(), scoreKeeper.getCurrentScore()));
					Toast.makeText(getBaseContext(), "Your score was successfully submitted! Check the leaderboard to see where it ranks.", Toast.LENGTH_LONG).show();
					submitted = true;
					scoreKeeper.resetCurrentScore();
					finish();
				} else {
					Toast.makeText(getBaseContext(), "Please enter your name before pressing SEND.", Toast.LENGTH_LONG).show();
				}
			}
		});

		submitted = false;
	}

	// If the submit activity is stopped, the score is reset without being submitted
	@Override
	public void onStop() {
		super.onStop();
		if (!submitted) {
			Toast.makeText(this, "Warning: Your score was not submitted to the leaderboard.", Toast.LENGTH_LONG).show();
			scoreKeeper.resetCurrentScore();
			finish();
		}
	}
}