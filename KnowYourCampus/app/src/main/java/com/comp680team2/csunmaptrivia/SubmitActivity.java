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
    final int REQ_CODE_RECOVER_PLAY_SERVICES = 1001;
    final int REQ_CODE_SIGN_IN = 9001;
    private final int REQ_CODE_LEADERBOARD = 1003;
    private Leaderboard leaderboard;
    private ScoreKeeper scoreKeeper;
	private EditText editText;
    private SignInButton signInButton;
    private Button signOutButton;
    private Button submitButton;
    private Button sendButton;
    private Button googleLeaderboardButton;
    private boolean scoreSubmitted;
    private boolean scoreSent;
    private boolean mResolvingConnectionFailure;
    private boolean mSignInClicked;
    private GoogleApiClient mGoogleApiClient;

    protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_activity);

        // Initialize booleans
        mResolvingConnectionFailure = false;
        scoreSubmitted = false;
        scoreSent = false;
        mSignInClicked = false;

        // Instantiate singleton objects
		leaderboard = Leaderboard.getLeaderboardSingleton();
		scoreKeeper = ScoreKeeper.getScoreKeeperSingleton();

        // Get reference handlers
		editText = (EditText)findViewById(R.id.submitNameTextView);
        signInButton = (SignInButton) findViewById(R.id.button_sign_in);
        signOutButton = (Button) findViewById(R.id.button_sign_out);
        submitButton = (Button) findViewById(R.id.submitButton);
        sendButton = (Button) findViewById(R.id.submit_send_google_button);
        googleLeaderboardButton = (Button) findViewById(R.id.leaderboardButton);
        googleLeaderboardButton.setEnabled(false);


        // Add the click listener to buttons
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        googleLeaderboardButton.setOnClickListener(this);

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

        if (!scoreSubmitted) {
            showToastWithMessage("Warning: Your score was not submitted to the leaderboard.");
        }

        scoreKeeper.resetCurrentScore();
        //finish();
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
                mResolvingConnectionFailure = false;
                if(resultCode == RESULT_OK) {
                    if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
                        d("onActivityResult()", "Sign in req good to go -> " +
                                "attempt connection");
                        mGoogleApiClient.connect();
                    }
                } else {
                    mSignInClicked = false;
                    showActivityResultErrorForSignIn(this, requestCode, resultCode,
                            R.string.signin_failure);
                }
                return;

            case REQ_CODE_LEADERBOARD:
                if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                    d("onActivityResult()", "leaderboar REQ returned inconsistent state");
                    googleLeaderboardButton.setEnabled(false);
                    mGoogleApiClient.disconnect();
                    showSignInBar();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Wrapper method
     *
     * @param messageToToast    a message to toast
     */
    private void showToastWithMessage(String messageToToast) {
        Toast.makeText(this, messageToToast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        d("onConnected()", "connection was successful -> toggling bars, enabling buttons");
        // Show the sign out bar
        showSignOutBar();
        googleLeaderboardButton.setEnabled(true);
        if (!scoreSent) {
            sendButton.setEnabled(true);
        }
        mSignInClicked = false;

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleLeaderboardButton.setEnabled(false);
        sendButton.setEnabled(false);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mResolvingConnectionFailure) {
            if (mSignInClicked) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(this, REQ_CODE_SIGN_IN);
                        mResolvingConnectionFailure = true;
                    } catch (IntentSender.SendIntentException e) {
                        // The intent was canceled before it was sent. Return to the default state
                        // and attempt to connect again to get and updated ConnectionResult
                        mResolvingConnectionFailure = false;
                        mGoogleApiClient.connect();
                    }
                } else {
                    Dialog connectionUtterFailureDialog = GooglePlayServicesUtil
                            .getErrorDialog(connectionResult.getErrorCode(), this, REQ_CODE_SIGN_IN);
                    if (connectionUtterFailureDialog != null) {
                        connectionUtterFailureDialog.show();
                    } else {
                        // No built-in dialog: show the fallback error message
                        showAlert(this, getString(R.string.signin_other_error));
                    }
                }
            }
        }
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
                googleLeaderboardButton.setEnabled(false);
                sendButton.setEnabled(false);
                showSignInBar();
                break;

            case R.id.submitButton:
                if (!editText.getText().toString().equals("")) {
                    leaderboard.addHighScore(new HighScore(editText.getText().toString(), scoreKeeper.getCurrentScore()));
                    showToastWithMessage("Your score was successfully submitted! Check the leaderboard to see where it ranks.");
                    scoreSubmitted = true;
                    submitButton.setEnabled(false);
                } else {
                    showToastWithMessage("Please enter your name before pressing SEND");
                }
                break;

            case R.id.submit_send_google_button:
                scoreSent = true;
                sendButton.setEnabled(false);
                Games.Leaderboards.submitScore(mGoogleApiClient,
                        getString(R.string.leaderboard_highest_scores_ID), scoreKeeper.getCurrentScore());
                break;

            case R.id.leaderboardButton:
                if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            getString(R.string.leaderboard_highest_scores_ID)), REQ_CODE_LEADERBOARD);
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
//-----From the example Implementing GCM Client on Android published by Google -------------------//
// http://developer.android.com/google/gcm/client.html

    /**
     * Check that the Google Play Services are supported by the device
     *
     * @return true if the Google Play Services are supported
     */
    private boolean checkForGooglePlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        REQ_CODE_RECOVER_PLAY_SERVICES);
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
        errorDialog.show();
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