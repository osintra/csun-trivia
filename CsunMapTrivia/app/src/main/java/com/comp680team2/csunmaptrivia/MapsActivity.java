/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * MapsActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.comp680team2.controller.GameController;
import com.comp680team2.model.QuestionHolder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    double latitude = 0;
    double longitude = 0;
    double vertx[] = {34.239095, 34.239104, 34.238452, 34.238452};
    double verty[] = {-118.530277, -118.530143, -118.530143, -118.530277};
    int seconds = 10;
    TextView timerTextView;
    TextView questionText;
    Polygon polygon = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        timerTextView = (TextView) findViewById(R.id.timer);
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
                                seconds--;

                            }
                        });

                    }

                }


            }
        });
        backgroundThread.start();
    }

    private void initializeGame() {
        Thread initThread = new Thread(new Runnable() {
            public void run() {
                //show some sort of loading mask
                final QuestionHolder questionHolder = new GameController().fetchQuestionSet();
                //hide the loading mask

                runOnUiThread(new Runnable() {
                    public void run() {
                        questionText.setText(questionHolder.getQuestion(0).getText());
                    }
                });
            }
        });
        initThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        double x = 34.240089;
        double y = -118.529435;
        LatLng ll = new LatLng(x, y);
        float zoom = 17;
        mMap.addMarker(new MarkerOptions().position(ll).title("CSUN"));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));

        int sides = 4;

        boolean result = pnpoly(sides, vertx, verty, x, y);


        if (result) {
            polygon = mMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(34.239095, -118.530277), new LatLng(34.239104, -118.530143), new LatLng(34.238452, -118.530143), new LatLng(34.238452, -118.530277))
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.BLUE));

        } else {
            polygon = mMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(34.239095, -118.530277), new LatLng(34.239104, -118.530143), new LatLng(34.238452, -118.530143), new LatLng(34.238452, -118.530277))
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
        }
/*
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(34.24123938043965, -118.53193402290344))
                .add(new LatLng(34.2419134421894, -118.53192329406738))  // North of the previous point, but at the same longitude
                .add(new LatLng(34.241931180583606, -118.53333950042725))  // Same latitude, and 30km to the west
                .add(new LatLng(34.241257118975916, -118.53331804275513))  // Same longitude, and 16km to the south
                .add(new LatLng(34.241257118975916, -118.53331804275513)); // Closes the polyline.
        Polyline polyline = mMap.addPolyline(rectOptions);
*/
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng point) {
                MapsActivity.this.latitude = point.latitude;
                MapsActivity.this.longitude = point.longitude;
                if(!(MapsActivity.this.latitude == 0.0 && MapsActivity.this.longitude == 0.0))
                {
                    polygon.remove();
                    boolean myresult = pnpoly(4,vertx, verty,MapsActivity.this.latitude,MapsActivity.this.longitude);
                    polygonDraw(myresult);
                }

            }


        });


        /*
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(34.24123938043965, -118.53193402290344), new LatLng(34.2419134421894, -118.53192329406738), new LatLng(34.241931180583606, -118.53333950042725), new LatLng(34.241257118975916, -118.53331804275513))
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
        DoPOST mDoPOST = new DoPOST(this, "yo");
        mDoPOST.execute("");*/
    }
    public void polygonDraw (boolean result) {
        if(result) {
            polygon = mMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(34.239095, -118.530277), new LatLng(34.239104, -118.530143), new LatLng(34.238452, -118.530143), new LatLng(34.238452, -118.530277))
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.BLUE));
        } else {
            polygon = mMap.addPolygon(new PolygonOptions()
                    .add(new LatLng(34.239095, -118.530277), new LatLng(34.239104, -118.530143), new LatLng(34.238452, -118.530143), new LatLng(34.238452, -118.530277))
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));
        }
    }

    boolean pnpoly(int nvert, double vertx[], double verty[], double testx, double testy)
    {
        int i, j;
        int flag =0;
        char check;
        boolean c = false;

        for (i=0; i< nvert; i++) {
            if(testy == verty[i]) {
                if(testx < vertx[0] && testx > vertx[2]) {
                    c = true;
                    return c;
                } else {
                    for (j=0; j< nvert; j++) {
                        if(testx == vertx[j]){
                            c = true;
                            return c;
                        }
                    }
                }
            } else if(testx == vertx[i]){
                if(testy > verty[0] && testy < verty[1]){
                    c = true;
                    return c;
                } else {
                    for (j=0; j< nvert; j++) {
                        if(testy == verty[j]){
                            c = true;
                            return c;
                        }
                    }
                }
            }
        }

        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( ((verty[i]>testy) != (verty[j]>testy)) &&
                    (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) ) {
                c = !c;
            }
        }
        return c;
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