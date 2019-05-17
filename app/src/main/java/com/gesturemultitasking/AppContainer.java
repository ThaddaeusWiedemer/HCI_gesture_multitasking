package com.gesturemultitasking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class AppContainer extends ConstraintLayout {
    public int mDepth;
    public boolean mOrientation;
    public View mDivider;
    public AppContainer mStart;
    public AppContainer mEnd;
    /**          _____              ____
     * vertical |  |  | horizontal |____| start
     *          |__|__|            |____| end
     *        start end
     */
    public final static boolean ORIENT_V = false;
    public final static boolean ORIENT_H = true;
    public final static boolean END = false;
    public final static boolean START = true;
    public final static int DIVIDER_SIZE = 4;


    // gesture handling
    private ScaleGestureDetector mPinchGestureDetector;
    private SwipeGestureDetector mSwipeGestureDetector;
    private String text = "";
    private Toast toast;
    public int mColor = android.R.color.white;

    public AppContainer(Context context){
        this(context, null, 0, 0, ORIENT_V);
    }

    public AppContainer(Context context, AttributeSet attrs){
        this(context, attrs, 0, 0, ORIENT_V);
    }

    public AppContainer(Context context, AttributeSet attrs, int defStyleAttr){
        this(context, attrs, defStyleAttr, 0, ORIENT_V);
    }

    public AppContainer(Context context, int depth, boolean orientation){
        this(context, null, 0, depth, orientation);
    }

    @SuppressLint("ResourceAsColor")
    public AppContainer(Context context, AttributeSet attrs, int defStyleAttr, int depth, boolean orientation){
        super(context, attrs, defStyleAttr);

        mDepth = depth;
        mOrientation = orientation;

        // initialize gesture detectors
        mPinchGestureDetector = new ScaleGestureDetector(context, new PinchListener());
        mSwipeGestureDetector = new SwipeGestureDetector(context, new SwipeListener(), 50, 400, 2160, 1920);

        // debug toasts
        toast = new Toast(context);

        this.setBackgroundColor(mColor);
    }

    @SuppressLint("ResourceAsColor")
    public void add(boolean side){
        // only three windows can be active
        if(ColorBlocksActivity.nWindows >= 3) {
            return;
        }

        // remove the current content
        View content = getChildAt(0);
        removeView(content);

        // create the split
        mDivider = new View(getContext());
        mDivider.setId(View.generateViewId());
        mStart = new AppContainer(getContext(), mDepth + 1, !mOrientation);
        mStart.setId(View.generateViewId());
        mEnd = new AppContainer(getContext(), mDepth + 1, !mOrientation);
        mEnd.setId(View.generateViewId());
        split(mDivider, mStart, mEnd);

        // create appDrawer
        AppDrawer appDrawer;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(mOrientation){ // horizontal
            appDrawer = new AppDrawer(getContext(), getMeasuredWidth());
        }else{ // vertical
            appDrawer = new AppDrawer(getContext(), getMeasuredWidth() / 2);
        }

        // add content on both sides
        if(side){ // new content on start side
            mStart.addView(appDrawer, params);
            mEnd.addView(content);
        }else{ // new content on end side
            mStart.addView(content);
            mEnd.addView(appDrawer, params);
        }

        ColorBlocksActivity.nWindows++;
    }

    public void split(View divider, AppContainer start, AppContainer end){
        ConstraintSet constraintSet = new ConstraintSet();

        // add the divider, set distance from top/left
        mDivider = divider;
        addView(mDivider);
        if (mOrientation){
            // horizontal
            constraintSet.connect(mDivider.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraintSet.connect(mDivider.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraintSet.connect(mDivider.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP,(getMeasuredHeight() - AppContainer.DIVIDER_SIZE)/2);
        }else{
            // vertical
            constraintSet.connect(mDivider.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraintSet.connect(mDivider.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
            constraintSet.connect(mDivider.getId(), ConstraintSet.START, getId(), ConstraintSet.START,(getMeasuredWidth() - AppContainer.DIVIDER_SIZE)/2);
        }

        // add the two ConstraintLayouts on both sides of the divider
        mStart = start;
        addView(mStart);
        mEnd = end;
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
        mDivider.setBackgroundColor(Color.argb(255, 0, 0, 0));
    }

    @SuppressLint("ResourceAsColor")
    public void keepOnly(AppContainer keepSide){
        if(keepSide.getChildCount() > 1) { // end side is split
            // get all children
            View divider = keepSide.getChildAt(0);
            AppContainer start = (AppContainer)keepSide.getChildAt(1);
            AppContainer end = (AppContainer)keepSide.getChildAt(2);
            keepSide.removeAllViews();
            removeAllViews();
            // recreate split
            mOrientation = !mOrientation;
            split(divider, start, end);
            mStart.mDepth--;
            mEnd.mDepth--;
        } else { // end side has normal content
            // copy content over
            View content = keepSide.getChildAt(0);
            keepSide.removeAllViews();
            removeAllViews();
            addView(content);
            // reset split
            mDivider = null;
            mStart = null;
            mEnd = null;
        }
        // update window count
        ColorBlocksActivity.nWindows--;
    }

    private void moveSplit(int to){
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        int anchor = LayoutParams.START;
        if (mOrientation == ORIENT_H) {
            anchor = LayoutParams.TOP;
        }

        constraintSet.setMargin(mDivider.getId(),anchor, to);
        constraintSet.applyTo(this);
    }

    // function gets called in parent before child and lets the parent take possession of a motion
    // event
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        boolean intercept = mSwipeGestureDetector.isMyEvent(event);
        mPinchGestureDetector.onTouchEvent(event);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
          text = "";
          mSwipeGestureDetector.onTouchEvent(event);
          mPinchGestureDetector.onTouchEvent(event);
          if(!text.equals("")) {
              toast.cancel();
              toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
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
            //debug toast
            text = "Swipe\n" +
                    "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                    "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        @Override
        public boolean isMyOutSwipe(SwipeGestureDetector detector) {
            // don't handle at all, if this element doesn't have children
            if(getChildCount() <= 1){
                return false;
            }

            // if this element's child has children, let the child deal with it
            if(mOrientation == ORIENT_H){
                if(detector.getEdge() == SwipeGestureDetector.EDGE_TOP && mStart.getChildCount() > 1){
                    return false;
                }
                if(detector.getEdge() == SwipeGestureDetector.EDGE_BOTTOM && mEnd.getChildCount() > 1){
                    return false;
                }
                if(detector.getEdge() == SwipeGestureDetector.EDGE_RIGHT){
                    return false;
                }
                if(detector.getEdge() == SwipeGestureDetector.EDGE_LEFT){
                    return false;
                }
            }else{
                if(detector.getEdge() == SwipeGestureDetector.EDGE_LEFT && mStart.getChildCount() > 1){
                    return false;
                }
                if(detector.getEdge() == SwipeGestureDetector.EDGE_RIGHT && mEnd.getChildCount() > 1){
                    return false;
                }
                if(detector.getEdge() == SwipeGestureDetector.EDGE_TOP){
                    return false;
                }
                if(detector.getEdge() == SwipeGestureDetector.EDGE_BOTTOM){
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean onOutSwipeBegin(SwipeGestureDetector detector) {
            // orientation: horizontal -> true
            //              vertical -> false
            //getTyoe : left -> 1
            //          right -> 2
            //          top   -> -1
            //          bottom ->  -2
            switch((mOrientation ? -1 : 1) * detector.getEdge() * (getChildCount() > 1 ? 1 : 0)){
                case 1:
                    keepOnly(mEnd);
                    break;
                case 2:
                    keepOnly(mStart);
                    break;
            }

            // debug toast
            text = "OutSwipe " + detector.getEdge() + "\n" +
                    "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                    "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        @Override
        public boolean isMyInSwipe(SwipeGestureDetector detector){
            // only handle the gesture if this is the innermost element
            return getChildCount() <= 1;
        }

        @Override
        public boolean onInSwipeBegin(SwipeGestureDetector detector) {
            // set orientation for depth 0
            if(mDepth == 0){
                if(detector.getEdge() > 0) {
                    mOrientation = ORIENT_V;
                }else{
                    mOrientation = ORIENT_H;
                }
            }

            // add new window on correct side
            switch ((mOrientation ? -1 : 1) * detector.getEdge() * (mDepth < 2 ? 1 : 0)) {
                case 1:
                    add(START);
                    break;
                case 2:
                    add(END);
                    break;
            }

            // debug toast
            text = "InSwipe " + detector.getEdge() + "\n" +
                    "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                    "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }
    }
}
