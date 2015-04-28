/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * LeaderboardActivity.java
 */

package com.comp680team2.knowyourcampus;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.comp680team2.model.Leaderboard;

public class LeaderboardActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_activity);

		Leaderboard leaderboard = Leaderboard.getLeaderboardSingleton();

		ListView listView = (ListView)findViewById(R.id.leaderboardListView1);
		//"android.R.layout.simple_list_item_1" is a pre-defined layout that contains a TextView for
		//strings to be rendered with. Customized layouts can be used by replacing this argument.
		listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leaderboard.getList()));
    }
}