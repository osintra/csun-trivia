/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * MainActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.comp680team2.controller.GameController;
import com.comp680team2.controller.HttpController;
import com.comp680team2.model.QuestionHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity
{
    private final String TAG = "mapTrivia";
    // Request code to invoke the Google Play Services status
    private static int REQUEST_CODE_PLAY_SERVICES = 1001;

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
                startActivity(new Intent(getBaseContext(), MapsActivity.class));
			}
		});

        checkPlayServices();
	}

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        // Handle the result of the Google Play Services resolution
        if (requestCode == REQUEST_CODE_PLAY_SERVICES) {
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Google Play Services APK is NOT supported");
                Toast.makeText(this, "Google Play Services must be installed", Toast.LENGTH_SHORT)
                        .show();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    // Check that the Google Play Services is correctly installed and configured on the device
    public boolean checkPlayServices() {
        Log.d(TAG, "checkPlayServices():---");
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, R.string.device_not_supported, Toast.LENGTH_LONG).show();
            }
            Log.d(TAG, "checkPlayServices(): end --- Error was not recoverable");
            return false;
        }
        Log.d(TAG, "checkPlayServices(): end --- Play Services status == " + status);
        return true;
    }

    // Show Google Play Services APK error
    private void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_PLAY_SERVICES).show();
    }
}