/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * PlayActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.comp680team2.controller.GameController;
import com.comp680team2.model.Question;
import com.comp680team2.model.QuestionHolder;
import com.comp680team2.model.ScoreKeeper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayActivity extends FragmentActivity {

	// Rect/view background colors
	private static final int BLUE_BG = Color.argb(50,0,0,255);
	private static final int BLACK_BG = Color.argb(150,0,0,0);
	private static final int RED_BG = Color.argb(150, 200, 0, 0);

	//Rect border colors
	private static final int GREEN_OUTLINE = Color.argb(100,0,255,0);
	private static final int RED_OUTLINE = Color.argb(100,255,0,0);

	//Difficulty colors
	private static final int EASY_COLOR = Color.argb(200,0,255,0);
	private static final int MEDIUM_COLOR = Color.argb(200,255,255,0);
	private static final int HARD_COLOR = Color.argb(200,255,0,0);
	private static final int EXTREME_COLOR = Color.argb(200,255,50,50);

	private final int MAX_SECONDS = 20;
	String timeDisplay = "";
	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	private double pressedLatitude = 0;
	private double pressedLongitude = 0;
	private double vertX[] = new double[4];
	private double vertY[] = new double[4];
	private TextView timeTextView = null;
	private TextView questionTextView = null;
	private TextView scoreTextView = null;
	private TextView difficultyTextView = null;
	private String trivia = "";
	private String label = "";
	private Marker myMarker;
	private ScoreKeeper scoreKeeper = null;
	private boolean questionAnsweredAlready = false;
	private Button nextQuestionButton = null;
	private QuestionHolder questionHolder = null;
	private int questionIndex = 0;
	private double remainingTime = 0;
	private long systemTimeAtStartOfQuestion = 0;
	private boolean gameEndedSuccessfully = false;
	private Thread timerThread = null;





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity);
		timeTextView = (TextView) findViewById(R.id.playTextView1);
		questionTextView = (TextView) findViewById(R.id.playTextView4);
		scoreTextView = (TextView) findViewById(R.id.playTextView3);
		scoreKeeper = ScoreKeeper.getScoreKeeperSingleton();
		difficultyTextView = (TextView) findViewById(R.id.playTextView2);
		nextQuestionButton = (Button) findViewById(R.id.playButton1);

		nextQuestionButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				// reset flag. The next question has not been answered
				questionAnsweredAlready = false;
				// hide next question button
				nextQuestionButton.setVisibility(View.GONE);
				questionIndex++;
				setUpQuestion();
				//enabling all gestures on the Map back to true
				mMap.clear();
				mMap.getUiSettings().setAllGesturesEnabled(true);
				setUpMap();
			}
		});

		initializeGame();
	}


	// If the game activity is stopped, the game ends
	// the score is reset without being submitted
	@Override
	public void onStop() {
		super.onStop();
		if (!gameEndedSuccessfully) {
			Toast.makeText(this, "Warning: The game was quit before it finished.", Toast.LENGTH_LONG).show();
			scoreKeeper.resetCurrentScore();
			finish();
		}
	}


	@Override
	protected void onStart() {
		super.onResume();
		setUpMapIfNeeded();
	}


	private void initializeGame() {
		Thread initThread = new Thread(new Runnable() {
			public void run() {
				// fetch question set
				questionHolder = new GameController().fetchQuestionSet();
				runOnUiThread(new Runnable() {
					public void run() {
						updateScoreView();
						setUpQuestion();
						setUpTimer();
					}
				});
			}
		});
		initThread.start();
	}

	private void setUpQuestion() {
		Question question;
		remainingTime = MAX_SECONDS;
		systemTimeAtStartOfQuestion = System.currentTimeMillis();

		// check that the index is not out of bounds
		//if (questionIndex < questionHolder.getNumberOfQuestions()) {
		if (questionIndex < 3) {
			try {
				// get current question from holder at index
				question = questionHolder.getQuestion(questionIndex);
				// get current expected answer corner coordinates
				for (int i = 0; i < 4; i++) {
					vertX[i] = question.getAnswer().getCoordinate(i).getX();
					vertY[i] = question.getAnswer().getCoordinate(i).getY();
				}
				// get current trivia
				if (question.getTrivia() != null && !question.getTrivia().equals("")) {
					trivia = "Trivia: " + question.getTrivia();
				} else if (question.getTrivia() != null) {
					trivia = "Trivia was empty";
				} else {
					trivia = "NULL trivia for this question";
				}
				// get current label
				label = question.getAnswer().getLabel();
				// update question text view
				questionTextView.setBackgroundColor(RED_BG);
				questionTextView.setText("Q: " + question.getText());
				// update difficulty view
				setUpDifficulty(question.getDifficulty());
			} catch (Exception setUpQuestionException) {
				setUpQuestionException.printStackTrace();
				Toast.makeText(getBaseContext(), "Exception setting up question", Toast.LENGTH_LONG).show();
				finish();
			}
		} else {
			gameEndedSuccessfully = true;
			//By calling an activity and then finish after it, the child activity will backtrack to its grandparent activity
			startActivity(new Intent(getBaseContext(), SubmitActivity.class));
			finish();
		}
	}

	private void setUpDifficulty(int difficulty) {
		int difficultyColor = BLACK_BG;
		String difficultyText = "Difficulty";
		switch(difficulty) {
			case 0:
				difficultyColor = EASY_COLOR;
				difficultyText = "Easy";
				break;
			case 1:
				difficultyColor = MEDIUM_COLOR;
				difficultyText = "Medium";
				break;
			case 2:
				difficultyColor = HARD_COLOR;
				difficultyText = "Hard";
				break;
			case 3:
				difficultyColor = EXTREME_COLOR;
				difficultyText = "Extreme";
				break;
		}
		difficultyTextView.setText((questionIndex + 1) + "/" + questionHolder.getNumberOfQuestions() + "   " + difficultyText);
		difficultyTextView.setTextColor(difficultyColor);
	}

	private void setUpTimer() {
		timerThread = new Thread(new Runnable() {
			public void run() {
				boolean timerLoop = true;

				while (timerLoop) {
					synchronized (this) {
						try {
							Thread.sleep(1);
							//Thread.sleep(1); will take up 10 ms on an emulator
							//Thread.sleep(10); will take up 20 ms on an emulator
							//Either way, elapsed time remains the same
						} catch (InterruptedException e) {
							//timer stops
						}

						if (!questionAnsweredAlready) {
							long elapsedMilliseconds = System.currentTimeMillis() - systemTimeAtStartOfQuestion;
							remainingTime = MAX_SECONDS - (elapsedMilliseconds / 1000.0);
							timeDisplay = String.format("Time: %d", (int)Math.abs(remainingTime));
						} else {
							timeDisplay = String.format("Time: %.3f", Math.abs(remainingTime));
						}

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								timeTextView.setText(timeDisplay);
							}
						});

						if (remainingTime <= 0 && !questionAnsweredAlready) {
							remainingTime = 0; //so that it always displays as 0.000 when time is up
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									handleUserResponse(false);  //question is failed when time is up
								}
							});
						}
					}
				}
			}
		});
		timerThread.start();
	}

	// reusable score view refresh
	private void updateScoreView() {
		scoreTextView.setText("Score: " + String.valueOf(scoreKeeper.getCurrentScore()));
	}

	/**
	 -     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 -     * installed) and the map has not already been instantiated.. This will ensure that we only ever
	 -     * call {@link #setUpMap()} once when {@link #mMap} is not null.
	 -     * <p/>
	 -     * A user can return to this FragmentActivity after following the prompt and correctly
	 -     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	 -     * have been completely destroyed during this process (it is likely that it would only be
	 -     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	 -     * method in {@link #onResume()} to guarantee that it will be called.
	 -     */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.playMapFragment)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}


	/**
	 -     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	 -     * just add a marker near Africa.
	 -     * <p/>
	 -     * This should only be called once and when we are sure that {@link #mMap} is not null.
	 -     */
	private void setUpMap() {
		mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		double x = 34.240089;
		double y = -118.529435;
		LatLng startingLatAndLng = new LatLng(x, y);
		final float startingZoom = 17;
		// sets the marker and its label to google map
		myMarker = mMap.addMarker(new MarkerOptions().position(startingLatAndLng).title("CSUN").visible(true));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLatAndLng, startingZoom));
		onMapClickValidation();
	}

	/**
	 -     * This function registers an onLongClick event on the map and then calls the validation method to
	 -     * check if the click is inside the right areas and adds the polygon and marker on the map
	 -     * <p/>
	 -     * This should only be called once and when we are sure that {@link #mMap} is not null.
	 -     */
	public void onMapClickValidation(){

		final int sides = 4;
		// register a long click/press map event on the google map
		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

			@Override
			public void onMapLongClick(final LatLng point) {
				PlayActivity.this.pressedLatitude = point.latitude;
				PlayActivity.this.pressedLongitude = point.longitude;

				// check that pressed coordinates are not zero and that the question has not been answered already
				if (!(PlayActivity.this.pressedLatitude == 0.0 && PlayActivity.this.pressedLongitude == 0.0)
						&& !questionAnsweredAlready) {

					// check if long press coordinates are within the answer area
					boolean myResult = findInPolygon(sides, vertX, vertY, PlayActivity.this.pressedLatitude, PlayActivity.this.pressedLongitude);

					// handles the user response based on whether it is right or wrong
					handleUserResponse(myResult);
				}
			}
		});
	}

	private void handleUserResponse(boolean isCorrect) {
		questionAnsweredAlready = true; // avoid adding extra points by clicking repeatedly

		// add new polygon on map
		int outlineColor = isCorrect ? GREEN_OUTLINE : RED_OUTLINE;
		Polygon polygon = mMap.addPolygon(new PolygonOptions()
				.add(new LatLng(vertX[0], vertY[0]), new LatLng(vertX[1], vertY[1]), new LatLng(vertX[2], vertY[2]),
						new LatLng(vertX[3], vertY[3]))
				.strokeColor(outlineColor).fillColor(BLUE_BG));
		if (isCorrect) {
			int score = calculateScore();
			scoreKeeper.addPoints(score);
			// update the score text view
			updateScoreView();
		}

		//stop timer
		if (!timerThread.isInterrupted()) {
			timerThread.interrupt();
		}

		// change the map center and marker to the building in question
		double newX = ((vertX[0] + vertX[2])/2.0);
		double newY = ((vertY[0] + vertY[1])/2.0);
		LatLng newLatLng = new LatLng(newX, newY);

		//removing the old marker
		myMarker.remove();

		//adding the new marker
		mMap.addMarker(new MarkerOptions().position(newLatLng).title(label).snippet(label).visible(true));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
		mMap.getUiSettings().setAllGesturesEnabled(false);
		mMap.getUiSettings().setMapToolbarEnabled(false);
		//setting the trivia in place of the question
		questionTextView.setText(trivia);
		questionTextView.setBackgroundColor(BLACK_BG);
		nextQuestionButton.setVisibility(View.VISIBLE);
		nextQuestionButton.bringToFront();
		if (questionIndex < questionHolder.getNumberOfQuestions() - 1) {
			nextQuestionButton.setText("Next Question");
		} else {
			nextQuestionButton.setText("Submit Score");
		}
	}


	/**
	 -     * Check if the coordinates are within the bounds of the correct location corresponding to the question
	 -     * @param side
	 -     * @param coordinateX
	 -     * @param coordinateY
	 -     * @param testX
	 -     * @param testY
	 -     * @return
	 -     */
	private boolean findInPolygon(int side, double coordinateX[], double coordinateY[], double testX, double testY) {
		int i, j;
		boolean flag = false;

		for (i=0; i< side; i++) {
			if(testY == coordinateY[i]) {
				if(testX < coordinateX[0] && testX > coordinateX[2]) {
					return true;
				} else {
					for (j=0; j< side; j++) {
						if(testX == coordinateX[j]){
							return true;
						}
					}
				}
			} else if(testX == coordinateX[i]){
				if(testY > coordinateY[0] && testY < coordinateY[1]){
					return true;
				} else {
					for (j=0; j< side; j++) {
						if(testY == coordinateY[j]){
							return true;
						}
					}
				}
			}
		}

		for (i = 0, j = side-1; i < side; j = i++) {
			if ( ((coordinateY[i]>testY) != (coordinateY[j]>testY)) &&
					(testX < (coordinateX[j]-coordinateX[i]) * (testY-coordinateY[i]) / (coordinateY[j]-coordinateY[i]) + coordinateX[i]) ) {
				flag = !flag;
			}
		}
		return flag;
	}


	// calculates score based on difficulty as a coefficient and remaining time
	private int calculateScore() {
		int difficulty = questionHolder.getQuestion(questionIndex).getDifficulty();
		return (int)(remainingTime * 1000) * (difficulty + 1); // scoring formula
	}


	// -- Rest Example. Tested to work.
	public class DoPOST extends AsyncTask<String, Void, Boolean> {

		Context mContext = null;
		String strNameToSearch = "";
		//Result data
		String strFirstName;
		String strLastName;
		int intAge;
		int intPoints;
		Exception exception = null;

		DoPOST(Context context, String nameToSearch) {
			mContext = context;
			strNameToSearch = nameToSearch;
		}

		@Override
		protected Boolean doInBackground(String... arg0) {

			try {

				//Setup the parameters
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("FirstNameToSearch", strNameToSearch));
				//Add more parameters as necessary
				//Create the HTTP request
				HttpParams httpParameters = new BasicHttpParams();
				//Setup timeouts
				HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
				HttpConnectionParams.setSoTimeout(httpParameters, 15000);
				HttpClient httpclient = new DefaultHttpClient(httpParameters);
				HttpPost httppost = new HttpPost("http://nullroute.cc/rest_test/login.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity);
				// Create a JSON object from the request response
				JSONObject jsonObject = new JSONObject(result);
				//Retrieve the data from the JSON object
				strFirstName = jsonObject.getString("FirstName");
				strLastName = jsonObject.getString("LastName");
				intAge = jsonObject.getInt("Age");
				intPoints = jsonObject.getInt("Points");
			} catch (Exception e) {
				Log.e("ClientServerDemo", "Error:", e);
				exception = e;
			}
			return true;
		}
	}
}