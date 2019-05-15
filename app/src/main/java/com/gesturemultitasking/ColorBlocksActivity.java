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
import android.widget.Toast;

public class ColorBlocksActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private SwipeGestureDetector mMoveGestureDetector;
    private String text = "";
    private Toast toast;

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

        // debug toasts
        Context context = getApplicationContext();
        toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
    }

    // handels all touch events and calls the gesture listeners
    @Override
    public boolean onTouchEvent(MotionEvent event){
        text = "";
        mMoveGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        if(text != "") {
            toast.cancel();
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return true;
    }

    // handles pinch gesture
    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            text = "Pinch\n" +
                "Focus " + detector.getFocusX() + " " + detector.getFocusY() + "\n" +
                "Span " + detector.getCurrentSpanX() + " " + detector.getCurrentSpanY();
            return true;
        }
    }

    // handels swipe gestures
    private class SwipeListener extends SwipeGestureDetector.SimpleOnSwipeGestureListener {
        @Override
        public boolean onSwipe(SwipeGestureDetector detector) {
            text = "Swipe\n" +
                "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onOutSwipe(SwipeGestureDetector detector) {
            text = "OutSwipe\n" +
                "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onInSwipe(SwipeGestureDetector detector) {
            text = "InSwipe\n" +
                "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }
    }
}
