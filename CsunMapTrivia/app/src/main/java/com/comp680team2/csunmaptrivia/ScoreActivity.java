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

public class ScoreActivity extends Activity
{
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
	}
}