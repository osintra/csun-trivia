/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * CreditsActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreditsActivity extends Activity
{
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.credits_activity);

		Button button1 = (Button)findViewById(R.id.creditsButton1);
		button1.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.nullroute.cc/")));
			}
		});

		Button button2 = (Button)findViewById(R.id.creditsButton2);
		button2.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				finish();
			}
		});
	}
}