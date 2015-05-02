/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * SubmitActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comp680team2.model.HighScore;
import com.comp680team2.model.Leaderboard;
import com.comp680team2.model.ScoreKeeper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;


import static android.util.Log.d;

public class SubmitActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, View.OnClickListener {
    private Leaderboard leaderboard;
	private ScoreKeeper scoreKeeper;
	private EditText editText;
    private SignInButton signInButton;
    private Button signOutButton;
    private Button sendButton;
    private Button seeGoogleLeaderboard;
	private boolean submitted;
    final int REQ_CODE_RECOVER_PLAY_SERVICES = 1001;
    final int REQ_CODE_SIGN_IN = 9001;
    private final int REQ_CODE_LEADERBOARD = 1003;
    private boolean mResolvingConnectionFailure;
    private boolean mSignInClicked;
    private GoogleApiClient mGoogleApiClient;

    protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_activity);

        // Initialize booleans
        mResolvingConnectionFailure = false;
        submitted = false;
        mSignInClicked = false;

        // Instantiate singleton objects
		leaderboard = Leaderboard.getLeaderboardSingleton();
		scoreKeeper = ScoreKeeper.getScoreKeeperSingleton();

        // Get reference handlers
		editText = (EditText)findViewById(R.id.submitNameTextView);
        signInButton = (SignInButton) findViewById(R.id.button_sign_in);
        signOutButton = (Button) findViewById(R.id.button_sign_out);
        sendButton = (Button)findViewById(R.id.submitButton);
        seeGoogleLeaderboard = (Button)findViewById(R.id.leaderboardButton);
        seeGoogleLeaderboard.setEnabled(false);

		TextView textView = (TextView)findViewById(R.id.submitTextView3);
		textView.setText("Score: " + scoreKeeper.getCurrentScore());

        // Add the click listener to buttons
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        seeGoogleLeaderboard.setOnClickListener(this);

        // Update score to display
		TextView textView = (TextView)findViewById(R.id.scoreTextView);
		textView.setText("Score: " + scoreKeeper.getCurrentScore());

        // Instantiate the Google API client
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();
	}

    @Override
    protected void onResume() {
        super.onResume();

        if(checkForGooglePlayServices()) {
            d("onResume()", "google play services good to go");
        }
    }

    // If the submit activity is stopped, the score is reset without being submitted
    @Override
    public void onStop() {
        super.onStop();

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            d("onStop()", "Disconnecting Google client");
            mGoogleApiClient.disconnect();
        }

        if (!submitted) {
            showToastWithMessage("Warning: Your score was not submitted to the leaderboard.");
            scoreKeeper.resetCurrentScore();
            //finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQ_CODE_RECOVER_PLAY_SERVICES:
                if(resultCode == RESULT_CANCELED) {
                    // the Google Play Services recover was cancelled
                    d("onActivityResult()", "google play services recovery cancelled ->" +
                            " toast and finish activity");
                    showToastWithMessage("Google Play Services must be installed");
                    finish();
                }
                return;

            case REQ_CODE_SIGN_IN:
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if(resultCode == RESULT_OK) {
                    if(mGoogleApiClient != null) {
                        d("onActivityResult()", "Sign in req good to go -> " +
                                "attempt connection");
                        mGoogleApiClient.connect();
                    }
                } else {
                    showActivityResultErrorForSignIn(this, requestCode, resultCode,
                            R.string.signin_failure);
                    finish();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }


    /**
     * Check that the Google Play Services are supported by the device
     *
     * @return  true if the Google Play Services are supported
     */
    private boolean checkForGooglePlayServices() {
        int servicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(servicesStatus != ConnectionResult.SUCCESS) {
            if(GooglePlayServicesUtil.isUserRecoverableError(servicesStatus)) {
                showErrorDialog(servicesStatus, REQ_CODE_RECOVER_PLAY_SERVICES);
            } else {
                showToastWithMessage("Google Play Services not supported by device");
                finish();
            }
            // Services not available
            return false;
        }
        // Services are available
        return true;
    }

    /**
     * Wrapper method
     *
     * @param messageToToast
     */
    private void showToastWithMessage(String messageToToast) {
        Toast.makeText(this, messageToToast, Toast.LENGTH_SHORT).show();
    }

    /**
     * Wrapper method
     *
     * @param errCode   error code returned by isGoogleServicesAvailable(Context)
     * @param reqCode   a request code to call the startActivityForResult
     */
    private void showErrorDialog(int errCode, int reqCode) {
        GooglePlayServicesUtil.getErrorDialog(errCode, this, reqCode).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        d("onConnected()", "connection was successful -> toggling bars, enabling buttons");
        // Show the sign out bar
        showSignOutBar();
        seeGoogleLeaderboard.setEnabled(true);

    }

    @Override
    public void onConnectionSuspended(int i) {
        seeGoogleLeaderboard.setEnabled(false);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(mResolvingConnectionFailure) {
            // Already resolving the failure
            d("onConnectionFailed()", "connection failure already resolving");
            return;
        }

        if(mSignInClicked) {
            mSignInClicked = false;
            mResolvingConnectionFailure = true;
            // Attempt to resolve
            d("onConnectionFailed()", "attempt to solve connection failure");
            if(resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
                    REQ_CODE_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        d("onConnectionFailed()", "connection could not be resolved -> show sign in bar");
        // Show the sign in bar
        showSignInBar();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in:
                mSignInClicked = true;
                // Initialize sign in flow
                d("onClick()", "attempting connection");
                mGoogleApiClient.connect();
                break;

            case R.id.button_sign_out:
                mSignInClicked = false;
                try {
                    Games.signOut(mGoogleApiClient);
                } catch (Exception e) {
                    d("sign out exception", e.getMessage());
                } finally {
                    if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.disconnect();
                    }
                }
                showSignInBar();
                break;

            case R.id.submitButton:
                if (!editText.getText().toString().equals("")) {
                    leaderboard.addHighScore(new HighScore(editText.getText().toString(), scoreKeeper.getCurrentScore()));
                    showToastWithMessage("Your score was successfully submitted! Check the leaderboard to see where it ranks.");
                    submitted = true;
                    scoreKeeper.resetCurrentScore();
                    sendButton.setEnabled(false);
                } else {
                    showToastWithMessage("Please enter your name before pressing SEND");
                }
                break;

            case R.id.leaderboardButton:
                if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            getString(R.string.leaderboard_highest_scores)), REQ_CODE_LEADERBOARD);
                } else {
                    showSignInBar();
                    showToastWithMessage("Please Sign in to a Google account");
                }
                break;
        }

    }

    private void showSignInBar() {
        findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
        findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
    }

    private void showSignOutBar() {
        findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
        findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
    }

//-----From the example BaseGameUtil.java published by Google ------------------------------------//
    /**
     * Show an {@link android.app.AlertDialog} with an 'OK' button and a message
     *
     * @param activity  the Activity in which the Dialog should be displayed
     * @param message   the message to display in the Dialog
     */
    private void showAlert(Activity activity, String message) {
        (new AlertDialog.Builder(activity)).setMessage(message)
                .setNeutralButton(android.R.string.ok, null).create().show();
    }

    /**
     * Resolve a connection failure from
     * {@link com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener#onConnectionFailed(com.google.android.gms.common.ConnectionResult)}
     *
     * @param activity  the Activity trying to resolve the connection failure
     * @param client    the GoogleAPIClient instance of the Activity
     * @param result    the ConnectionResult received by the Activity
     * @param requestCode   a request code which the calling Activity can use to identify the result of this resolution in onActivityResult
     * @param fallbackErrorMessage  a generic error message to display if the failure cannot be resolved
     * @return  boolean true if the connection failure is resolved, false otherwise
     */
    private boolean resolveConnectionFailure(Activity activity, GoogleApiClient client, ConnectionResult result,
                                             int requestCode, String fallbackErrorMessage) {

        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(activity, requestCode);
                return true;
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                client.connect();
                return false;
            }
        } else {
            // Not resolvable, so show an error message
            int errorCode = result.getErrorCode();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                    activity, requestCode);
            if (dialog != null) {
                dialog.show();
            } else {
                // No built-in dialog: show the fallback error message
                showAlert(activity, fallbackErrorMessage);
            }
            return false;
        }
    }

    /**
     * Show a {@link android.app.Dialog} with the correct message for a connection error
     * @param activity  the Activity in which the Dialog should be displayed
     * @param requestCode   the request code from onActivityResult
     * @param actResp   the response code from onActivityResult
     * @param fallBackErrorDescription  a generic error message
     */
    private void showActivityResultErrorForSignIn(Activity activity, int requestCode, int actResp,
                                                  int fallBackErrorDescription) {
        Dialog errorDialog;

        switch (actResp) {
            case GamesActivityResultCodes.RESULT_APP_MISCONFIGURED:
                errorDialog = makeSimpleDialog(activity,
                        activity.getString(R.string.app_misconfigured));
                break;
            case GamesActivityResultCodes.RESULT_SIGN_IN_FAILED:
                errorDialog = makeSimpleDialog(activity,
                        activity.getString(R.string.sign_in_failed));
                break;
            case GamesActivityResultCodes.RESULT_LICENSE_FAILED:
                errorDialog = makeSimpleDialog(activity,
                        activity.getString(R.string.license_failed));
                break;
            default:
                // No meaningful Activity response code, so generate default Google
                // Play services dialog
                final int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
                errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                        activity, requestCode, null);
                if (errorDialog == null) {
                    // get fallback dialog
                    d("shwActResForSignIn()",
                            "No standard error dialog available. Making fallback dialog.");
                    errorDialog = makeSimpleDialog(activity, activity.getString(fallBackErrorDescription));
                }
        }

        if(errorDialog != null) {
            errorDialog.show();
        } else {
            d("shwActResForSignIn()", "null D");
        }
    }

    /**
     * Create a simple {@link Dialog} with an 'OK' button and a message
     *
     * @param activity  the Activity in which the Dialog should be displayed
     * @param text  the message to display in the Dialog
     * @return  an instance of {@link android.app.AlertDialog}
     */
    private Dialog makeSimpleDialog(Activity activity, String text) {
        return (new AlertDialog.Builder(activity)).setMessage(text)
                .setNeutralButton(android.R.string.ok, null).create();
    }
//------------------------------------------------------------------------------------------------//
}