/* Chris Bowles, Victor Perez, Russell Templet, Nishika Malhotra, Maria Velasquez
 * Comp 680, Team 2, Spring 2015, Prof. Boctor
 * MapsActivity.java
 */

package com.comp680team2.csunmaptrivia;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);
        setUpMapIfNeeded();
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
        LatLng ll = new LatLng(34.243324, -118.529543);
        float zoom = 13;
        mMap.addMarker(new MarkerOptions().position(ll).title("Marker"));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
/*
        PolylineOptions rectOptions = new PolylineOptions()
                .add(new LatLng(34.24123938043965, -118.53193402290344))
                .add(new LatLng(34.2419134421894, -118.53192329406738))  // North of the previous point, but at the same longitude
                .add(new LatLng(34.241931180583606, -118.53333950042725))  // Same latitude, and 30km to the west
                .add(new LatLng(34.241257118975916, -118.53331804275513))  // Same longitude, and 16km to the south
                .add(new LatLng(34.241257118975916, -118.53331804275513)); // Closes the polyline.
        Polyline polyline = mMap.addPolyline(rectOptions);
*/
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(34.241239380333965, -118.53193402290344), new LatLng(34.2419134421894, -118.53192329406738), new LatLng(34.241931180583606, -118.53333950042725), new LatLng(34.241257118975916, -118.53331804275513))
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
    }
}