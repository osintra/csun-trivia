/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * MainActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity
{
    // Request code to invoke the Google Play Services status
    private static int REQUEST_CODE_PLAY_SERVICES = 1001;
	private final String TAG = "mapTrivia";
	Button playButton = null;
	private ConnectivityManager connectivityManager = null;
	private NetworkInfo activeNetwork = null;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		//Delay so that the splash logo shows for 3 seconds
		new Handler().postDelayed(new Runnable() {
			public void run() {
				//Now, we load the main activity
				setContentView(R.layout.main_activity);
				// initialize connectivity manager and get network information
				connectivityManager = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				activeNetwork = connectivityManager.getActiveNetworkInfo();

				Button rulesButton = (Button)findViewById(R.id.mainButton1);
				rulesButton.setOnClickListener(new OnClickListener() {

					public void onClick(View view) {
						startActivity(new Intent(getBaseContext(), RulesActivity.class));
					}
				});

				playButton = (Button)findViewById(R.id.mainButton2);
				playButton.setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						startActivity(new Intent(getBaseContext(), PlayActivity.class));
					}
				});

				Button submitButton = (Button)findViewById(R.id.mainButton3);
				submitButton.setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						startActivity(new Intent(getBaseContext(), LeaderboardActivity.class));
					}
				});

				Button creditsButton = (Button)findViewById(R.id.mainButton4);
				creditsButton.setOnClickListener(new OnClickListener() {
					public void onClick(View view) {
						startActivity(new Intent(getBaseContext(), CreditsActivity.class));
					}
				});

				checkPlayServices();

				// check the network connection
				if (activeNetwork == null || !activeNetwork.isConnected()) {
					playButton.setEnabled(false);
				}
			}
		}, 3000);
	}

    //TODO: extract network connection check point to method
    //TODO: Add a network connection monitor so that the Play button becomes enabled when the network connection is restored

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        // Handle the result of the Google Play Services resolution
        if (requestCode == REQUEST_CODE_PLAY_SERVICES) {
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Google Play Services APK is NOT supported");
                playButton.setEnabled(false);
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
                playButton.setEnabled(false);
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