/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
- * Comp 680, Team 2, Spring 2015, Prof. Boctor
- * MapsActivity.java
- */

package com.comp680team2.csunmaptrivia;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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

public class MapsActivity extends FragmentActivity {

    // Custom colors
    private static final int BLUE_BG = Color.argb(50,0,0,255);
    private static final int BLACK_BG = Color.argb(150,0,0,0);
    private static final int RED_BG = Color.argb(150, 200, 0, 0);
    private static final int GREEN_OUTLINE = Color.argb(100,0,255,0);
    private static final int RED_OUTLINE = Color.argb(100,255,0,0);


    //TODO: submit the achieved score when the game ends successfully
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double pressedLatitude = 0;
    private double pressedLongitude = 0;
    private double vertX[] = new double[4];
    private double vertY[] = new double[4];
    private int seconds = 10;
    private TextView timerTextView = null;
    private TextView questionTextView = null;
    private TextView scoreTextView = null;
    private Polygon polygon = null;
    private String trivia = "";
    private String label = "";
    private Marker myMarker;
    private ScoreKeeper scoreKeeper = null;
    private boolean questionAnsweredAlready = false;
    private Button nextQuestionButton = null;
    private QuestionHolder questionHolder = null;
    private int questionNumber = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        timerTextView = (TextView) findViewById(R.id.maps_activity_timerTextView);
        questionTextView = (TextView) findViewById(R.id.maps_activity_questionTextView);
        scoreTextView = (TextView) findViewById(R.id.maps_activity_scoreTextView);
        scoreKeeper = ScoreKeeper.getScoreKeeperSingleton();
        nextQuestionButton = (Button) findViewById(R.id.maps_activity_next_button);

        nextQuestionButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                // reset flag. The next question has not been answered
                questionAnsweredAlready = false;
                // hide next question button
                nextQuestionButton.setVisibility(View.GONE);
                setUpQuestion(++questionNumber);
                //enabling all gestures on the Map back to true
                mMap.clear();
                mMap.getUiSettings().setAllGesturesEnabled(true);
                setUpMap();
            }
        });

        initializeGame();

        Thread backgroundThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    synchronized (this) {
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timerTextView.setText(Integer.toString(seconds));
                                if (seconds > 0) seconds--;
                            }
                        });
                    }
                }
            }
        });
        backgroundThread.start();
    }



    // If the game activity is stopped, the game ends
    // the score is reset without being submitted
    @Override
    public void onStop() {
        Toast.makeText(this, "Quitting Game", Toast.LENGTH_SHORT).show();
        super.onStop();
        scoreKeeper.resetCurrentScore();
        finish();
    }


    @Override
    protected void onStart() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void initializeGame() {
        Thread initThread = new Thread(new Runnable() {
            public void run() {
                //TODO: show some sort of loading mask
                // fetch question set
                questionHolder = new GameController().fetchQuestionSet();
                runOnUiThread(new Runnable() {
                    public void run() {
                        setUpQuestion(questionNumber);
                    }
                });
            }
        });
        initThread.start();
    }

    /**
     * Set up the question object for the next question/answer cycle of the game
     * @param questionIndex
     */
    private void setUpQuestion(int questionIndex) {
        Question question;

        // check that the index is not out of bounds
        if (questionIndex < questionHolder.getNumberOfQuestions()) {
            try {
                // get current question from holder at index
                question = questionHolder.getQuestion(questionIndex);
                // get current expected answer corner coordinates
                for (int i = 0; i < 4; i++) {
                    vertX[i] = question.getAnswer().getCoordinate(i).getX();
                    vertY[i] = question.getAnswer().getCoordinate(i).getY();
                }
                // get current trivia
                if (question.getTrivia() != null) {
                    trivia = question.getTrivia();
                } else if (question.getTrivia() == "") {
                    trivia = "Trivia was empty";
                } else {
                    trivia = "NULL trivia for this question";
                }
                // get current label
                label = question.getAnswer().getLabel();
                // update question text view
                questionTextView.setBackgroundColor(RED_BG);
                questionTextView.setText(question.getText());
            } catch (Exception setUpQuestionException) {
                setUpQuestionException.printStackTrace();
                Toast.makeText(getBaseContext(), "Exception setting up question", Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Toast.makeText(getBaseContext(), "Game Finished! Go to Last Score to see your points", Toast.LENGTH_LONG).show();
            scoreKeeper.submitCurrentScore();
            finish();
        }
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
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
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
                MapsActivity.this.pressedLatitude = point.latitude;
                MapsActivity.this.pressedLongitude = point.longitude;

                // check that pressed coordinates are not zero and that the question has not been answered already
                if (!(MapsActivity.this.pressedLatitude == 0.0 && MapsActivity.this.pressedLongitude == 0.0)
                        && !questionAnsweredAlready){
                    questionAnsweredAlready = true; // avoid adding extra points by clicking repeatedly

                    // check if long press coordinates are within the answer area
                    boolean myResult = findInPolygon(sides, vertX, vertY, MapsActivity.this.pressedLatitude, MapsActivity.this.pressedLongitude);
                    if (myResult) {
                        // add new polygon on map
                        polygon = mMap.addPolygon(new PolygonOptions()
                                .add(new LatLng(vertX[0], vertY[0]), new LatLng(vertX[1], vertY[1]), new LatLng(vertX[2], vertY[2]),
                                        new LatLng(vertX[3], vertY[3]))
                                .strokeColor(GREEN_OUTLINE).fillColor(BLUE_BG));

                        // add 1 (one) point to the current score
                        //TODO: change to addPoints(...) for second sprint
                        scoreKeeper.addPoint();
                        // update the score text view
                        scoreTextView.setText("Score: " + String.valueOf(scoreKeeper.getCurrentScore()));


                    } else {
                        // add new polygon on map
                        polygon = mMap.addPolygon(new PolygonOptions()
                                .add(new LatLng(vertX[0], vertY[0]), new LatLng(vertX[1], vertY[1]), new LatLng(vertX[2], vertY[2]),
                                        new LatLng(vertX[3], vertY[3]))
                                .strokeColor(RED_OUTLINE).fillColor(BLUE_BG));
                    }

                    // change the map center and marker to the building in question
                    double newX = ((vertX[0] + vertX[2])/2.0);
                    double newY = ((vertY[0] + vertY[1])/2.0);
                    LatLng newLatLng = new LatLng(newX, newY);
                    LatLng newLatLng1 = new LatLng(vertX[0], vertY[0]);

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
                }
            }
        });
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
                    flag = true;
                    return flag;
                } else {
                    for (j=0; j< side; j++) {
                        if(testX == coordinateX[j]){
                            flag = true;
                            return flag;
                        }
                    }
                }
            } else if(testX == coordinateX[i]){
                if(testY > coordinateY[0] && testY < coordinateY[1]){
                    flag = true;
                    return flag;
                } else {
                    for (j=0; j< side; j++) {
                        if(testY == coordinateY[j]){
                            flag = true;
                            return flag;
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
