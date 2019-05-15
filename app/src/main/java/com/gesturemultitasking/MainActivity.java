package com.gesturemultitasking;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.TextView;

//import com.almeros.android.multitouch.MoveGestureDetector;

public class MainActivity extends AppCompatActivity{

    //private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private SwipeGestureDetector mMoveGestureDetector;
    //private float mScaleFactor = 1.0f;
    //private float mFocusX = 0.f;
    //private float mFocusY = 0.f;
    private String name = "";
    private String position = "";
    private String direction = "";
    private TextView g_n, g_p, g_d;

    // Called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        //mGestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(this, new MyScaleListener());
        mMoveGestureDetector = new SwipeGestureDetector(this, new MySwipeListener());
        g_n = findViewById(R.id.gesture_name);
        g_p = findViewById(R.id.gesture_position);
        g_d = findViewById(R.id.gesture_direction);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        //retVal = mGestureDetector.onTouchEvent(event) || mMoveGestureDetector.onTouchEvent(event) || retVal;
        //return retVal || super.onTouchEvent(event);

        //this.mGestureDetector.onTouchEvent(event);
        //return super.onTouchEvent(event);

        //mGestureDetector.onTouchEvent(event);
        mMoveGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        g_n.setText(name);
        g_p.setText(position);
        g_d.setText(direction);
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            g_n.setText("onDown");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            g_n.setText("onFling");
            return true;
        }
    }

    private class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            name = "Pinch";
            position = "Focus " + detector.getFocusX() + " " + detector.getFocusY();
            direction = "Span " + detector.getCurrentSpanX() + " " + detector.getCurrentSpanY();
            return true;
        }
    }

    private class MySwipeListener extends SwipeGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public boolean onSwipe(SwipeGestureDetector detector) {
            name = "Swipe";
            position = "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y;
            direction = "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onOutSwipe(SwipeGestureDetector detector) {
            name = "OutSwipe";
            position = "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y;
            direction = "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onInSwipe(SwipeGestureDetector detector) {
            name = "InSwipe";
            position = "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y;
            direction = "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }
    }
}
