package com.gesturemultitasking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

public class MyConstraintLayout extends ConstraintLayout {
    public int mDepth;
    public boolean mOrientation;
    public View mDivider;
    public MyConstraintLayout mStart;
    public MyConstraintLayout mEnd;
    /**          _____              ____
     * vertical |  |  | horizontal |____| start
     *          |__|__|            |____| end
     *        start end
     */
    public final static boolean ORIENT_V = false;
    public final static boolean ORIENT_H = true;
    public final static boolean END = false;
    public final static boolean START = true;
    public final static int DIVIDER_SIZE = 20;


    // gesture handling
    private ScaleGestureDetector mPinchGestureDetector;
    private SwipeGestureDetector mSwipeGestureDetector;
    private String text = "";
    private Toast toast;
    private Context mContext;
    public int mColor = android.R.color.white;

    public MyConstraintLayout(Context context){
        this(context, null, 0, 0, ORIENT_V);
    }

    public MyConstraintLayout(Context context, AttributeSet attrs){
        this(context, attrs, 0, 0, ORIENT_V);
    }

    public MyConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr){
        this(context, attrs, defStyleAttr, 0, ORIENT_V);
    }

    public MyConstraintLayout(Context context, int depth, boolean orientation){
        this(context, null, 0, depth, orientation);
    }

    @SuppressLint("ResourceAsColor")
    public MyConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr, int depth, boolean orientation){
        super(context, attrs, defStyleAttr);

        mDepth = depth;
        mOrientation = orientation;

        // initialize gesture detectors
        mPinchGestureDetector = new ScaleGestureDetector(context, new PinchListener());
        mSwipeGestureDetector = new SwipeGestureDetector(context, new SwipeListener());

        // debug toasts
        mContext = context;
        toast = new Toast(context);

        this.setBackgroundColor(mColor);
    }

    @SuppressLint("ResourceAsColor")
    public void add(boolean side){
        ConstraintSet constraintSet = new ConstraintSet();

        // add the divider, set distance from top/left
        mDivider = new View(mContext);
        mDivider.setId(View.generateViewId());
        addView(mDivider);
        if (mOrientation){
            // horizontal
            constraintSet.connect(mDivider.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraintSet.connect(mDivider.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraintSet.connect(mDivider.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP,(getMeasuredHeight() - MyConstraintLayout.DIVIDER_SIZE)/2);
        }else{
            // vertical
            constraintSet.connect(mDivider.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraintSet.connect(mDivider.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
            constraintSet.connect(mDivider.getId(), ConstraintSet.START, getId(), ConstraintSet.START,(getMeasuredWidth() - MyConstraintLayout.DIVIDER_SIZE)/2);
        }

        // add the two ConstraintLayouts on both sides of the divider
        mStart = new MyConstraintLayout(mContext, mDepth + 1, !mOrientation);
        mStart.setId(View.generateViewId());
        addView(mStart);
        mEnd = new MyConstraintLayout(mContext, mDepth + 1, !mOrientation);
        mEnd.setId(View.generateViewId());
        addView(mEnd);
        if (mOrientation){
            constraintSet.connect(mStart.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraintSet.connect(mStart.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraintSet.connect(mStart.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraintSet.connect(mStart.getId(), ConstraintSet.BOTTOM, mDivider.getId(), ConstraintSet.TOP);
            constraintSet.connect(mEnd.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraintSet.connect(mEnd.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraintSet.connect(mEnd.getId(), ConstraintSet.TOP, mDivider.getId(), ConstraintSet.BOTTOM);
            constraintSet.connect(mEnd.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
        }else{
            constraintSet.connect(mStart.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraintSet.connect(mStart.getId(), ConstraintSet.END, mDivider.getId(), ConstraintSet.START);
            constraintSet.connect(mStart.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraintSet.connect(mStart.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
            constraintSet.connect(mEnd.getId(), ConstraintSet.START, mDivider.getId(), ConstraintSet.END);
            constraintSet.connect(mEnd.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraintSet.connect(mEnd.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraintSet.connect(mEnd.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
        }

        // apply constraintSet
        constraintSet.applyTo(this);

        // draw divider correctly
        if(mOrientation){
            mDivider.getLayoutParams().height = DIVIDER_SIZE;
        }else{
            mDivider.getLayoutParams().width = DIVIDER_SIZE;
        }
        mDivider.setBackgroundColor(android.R.color.black);

        // handle content
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        if(side){
            mStart.mColor = mColor;
            mStart.setBackgroundColor(mStart.mColor);
            mEnd.mColor = color;
            mEnd.setBackgroundColor(mEnd.mColor);

        }else{
            mStart.mColor = color;
            mStart.setBackgroundColor(mStart.mColor);
            mEnd.mColor = mColor;
            mEnd.setBackgroundColor(mEnd.mColor);
        }
    }

    private void delete(){

    }

    private void moveSplit(){

    }

    // handles all touch events and calls the gesture listeners
    @Override
    public boolean onTouchEvent(MotionEvent event){
        text = "";
        mSwipeGestureDetector.onTouchEvent(event);
        mPinchGestureDetector.onTouchEvent(event);
        if(text != "") {
            toast.cancel();
            toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            toast.show();
        }
        add(END);
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
