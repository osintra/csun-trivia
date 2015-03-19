package com.comp680team2.csunmaptrivia;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by nishika on 3/18/2015.
 */
public class SwipeActivity extends MapsActivity implements SwipeGestureDetector.SimpleGestureListener {

    private SwipeGestureDetector detector;
   // SwipeGestureDetector gestureListener;
   private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);

        // Detect touched area
       // detector = new SwipeGestureDetector(this, this);
        // detector = new GestureDetector.OnGestureListener(new SwipeGestureDetector(this,this));

        // Gesture detection
        gestureDetector = new GestureDetector(this, new SwipeGestureDetector(this,this));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        questionTextView.setOnTouchListener(gestureListener);
    }


    //@Override
   // public boolean dispatchTouchEvent (MotionEvent event){
    // Call onTouchEvent of SwipeGestureDetector class
     //   this.detector.onTouchEvent(event);
     //   return super.dispatchTouchEvent(event);
   // }

    @Override
    public void onSwipe(int direction){
            switch (direction){
                case SwipeGestureDetector.SWIPE_RIGHT:
                    //flag++;
                    break;
                case SwipeGestureDetector.SWIPE_LEFT:
                   // flag++;
                    break;
                case SwipeGestureDetector.SWIPE_DOWN:
                    break;
                case SwipeGestureDetector.SWIPE_UP:
                    break;
            }
        Toast.makeText(this, "Swiped", Toast.LENGTH_SHORT).show();
        }

}
