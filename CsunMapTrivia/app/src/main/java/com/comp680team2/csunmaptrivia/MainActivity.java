/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * MainActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.comp680team2.controller.GameController;
import com.comp680team2.controller.HttpController;
import com.comp680team2.model.QuestionHolder;

public class MainActivity extends Activity
{
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		Button button1 = (Button)findViewById(R.id.mainButton1);
		button1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				startActivity(new Intent(getBaseContext(), GameActivity.class));
			}
		});

		Button button2 = (Button)findViewById(R.id.mainButton2);
		button2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				startActivity(new Intent(getBaseContext(), ScoreActivity.class));
			}
		});

		Button button3 = (Button)findViewById(R.id.mainButton3);
		button3.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				startActivity(new Intent(getBaseContext(), CreditsActivity.class));
			}
		});

		Button button4 = (Button)findViewById(R.id.mainButton4);
		button4.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				initializeGame();
			}
		});
	}



    private void initializeGame() {
        Thread initThread = new Thread(new Runnable() {
            public void run() {
                //show some sort of loading mask
                QuestionHolder questionHolder = new GameController().fetchQuestionSet();
                //hide the loading mask
                startActivity(new Intent(getBaseContext(), MapsActivity.class));
            }
        });
        initThread.start();
    }
}