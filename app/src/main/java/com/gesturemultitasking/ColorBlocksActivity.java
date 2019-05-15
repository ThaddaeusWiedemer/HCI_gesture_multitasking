package com.gesturemultitasking;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ColorBlocksActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private SwipeGestureDetector mMoveGestureDetector;
    private String name = "";
    private String position = "";
    private String options = "";
    private TextView g_n, g_p, g_o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // fullscreen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_color_blocks);

        // setup gesture detectors
        mScaleGestureDetector = new ScaleGestureDetector(this, new PinchListener());
        mMoveGestureDetector = new SwipeGestureDetector(this, new SwipeListener());

        // debug text views
        g_n = findViewById(R.id.g_n);
        g_p = findViewById(R.id.g_p);
        g_o = findViewById(R.id.g_o);
    }

    // handels all touch events and calls the gesture listeners
    @Override
    public boolean onTouchEvent(MotionEvent event){
        mMoveGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        // output debug messages
        g_n.setText(name);
        g_p.setText(position);
        g_o.setText(options);
        return true;
    }

    // handles pinch gesture
    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            name = "Pinch";
            position = "Focus " + detector.getFocusX() + " " + detector.getFocusY();
            options = "Span " + detector.getCurrentSpanX() + " " + detector.getCurrentSpanY();
            return true;
        }
    }

    // handels swipe gestures
    private class SwipeListener extends SwipeGestureDetector.SimpleOnSwipeGestureListener {
        @Override
        public boolean onSwipe(SwipeGestureDetector detector) {
            name = "Swipe";
            position = "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y;
            options = "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onOutSwipe(SwipeGestureDetector detector) {
            name = "OutSwipe";
            position = "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y;
            options = "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onInSwipe(SwipeGestureDetector detector) {
            name = "InSwipe";
            position = "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y;
            options = "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }
    }
}
