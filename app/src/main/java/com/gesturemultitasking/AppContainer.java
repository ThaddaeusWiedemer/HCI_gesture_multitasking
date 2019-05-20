package com.gesturemultitasking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
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
    
    public final static int WIDTH = 2160;
    public final static int HEIGHT = 1920;
    public final static int DELETE_THRESHOLD = 250;

    // gesture handling
    private ScaleGestureDetector mPinchGestureDetector;
    private SwipeGestureDetector mSwipeGestureDetector;
    private String debugText = "";
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
        mSwipeGestureDetector = new SwipeGestureDetector(context, new SwipeListener(), 50, 400, WIDTH, HEIGHT);

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
            appDrawer = new AppDrawer(getContext(), (getMeasuredWidth() - AppContainer.DIVIDER_SIZE)/2);
        }

        // add content on both sides
        if(side){ // new content on start side
            mEnd.addView(content);
            mStart.addView(appDrawer, params);
            // update end side
            if(mOrientation){ // horizontal
                int w = getMeasuredWidth();
                mEnd.update(w);
            }else{ // vertical
                int w = getMeasuredWidth();
                mEnd.update((w - AppContainer.DIVIDER_SIZE)/2);
            }
        }else{ // new content on end side
            mStart.addView(content);
            mEnd.addView(appDrawer, params);
            // update start side
            if(mOrientation){ // horizontal
                int w = getMeasuredWidth();
                mStart.update(w);
            }else{ // vertical
                int w = getMeasuredWidth();
                mStart.update((w - AppContainer.DIVIDER_SIZE)/2);
            }
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
        // update content and window count
        update(getMeasuredWidth());
        ColorBlocksActivity.nWindows--;
    }

    private void moveSplit(PointF delta){
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        int[] dividerLocation = new int[2];
        mDivider.getLocationOnScreen(dividerLocation); // gets top left corner

        if (mOrientation) { // horizontal
            int y = (int) (dividerLocation[1] + delta.y);
            constraintSet.setMargin(mDivider.getId(), LayoutParams.TOP, y);
            if(y < DELETE_THRESHOLD){
                keepOnly(mEnd);
            } else if(y > HEIGHT - DELETE_THRESHOLD){
                keepOnly(mStart);
            } else {
                //mStart.update(WIDTH);
                //mEnd.update(HEIGHT - dividerLocation[0] - DIVIDER_SIZE);
                constraintSet.applyTo(this);
            }
        }else{
            int x = (int) (dividerLocation[0] + delta.x);
            constraintSet.setMargin(mDivider.getId(), LayoutParams.START, x);
            if(x < DELETE_THRESHOLD){
                keepOnly(mEnd);
            } else if(x > WIDTH - DELETE_THRESHOLD){
                keepOnly(mStart);
            } else {
                mStart.update(x);
                mEnd.update(WIDTH - x - DIVIDER_SIZE);
                constraintSet.applyTo(this);
            }
        }
    }

    public void update(int newWidth){
        // case: one child that is an AppDrawer
        if(getChildCount() == 1 && getChildAt(0) instanceof AppDrawer) {
            AppDrawer appDrawer = (AppDrawer) getChildAt(0);
            appDrawer.update(newWidth);
        }else if (mDivider != null){
            if (mOrientation == ORIENT_H) {
                mStart.update(newWidth);
                mEnd.update(newWidth);
            }


        }
    }

    private boolean isMStart(){
        return ((AppContainer)getParent()).mStart == this;
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
        debugText = "";
        mSwipeGestureDetector.onTouchEvent(event);
        mPinchGestureDetector.onTouchEvent(event);
        if(!debugText.equals("")) {
            toast.cancel();
            toast = Toast.makeText(getContext(), debugText, Toast.LENGTH_SHORT);
            toast.show();
        }
        return true;
    }

    // handles pinch gesture
    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            debugText = "Pinch\n" +
                    "Focus " + detector.getFocusX() + " " + detector.getFocusY() + "\n" +
                    "Span " + detector.getCurrentSpanX() + " " + detector.getCurrentSpanY();
            return true;
        }
    }


    // handles swipe gestures
    private class SwipeListener extends SwipeGestureDetector.SimpleOnSwipeGestureListener {
        @Override
        public boolean isMySwipe(SwipeGestureDetector detector){
            // don't handle if there is no split
            if(getChildCount() <= 1){
                return false;
            }

            // only handle if fingers are on both sides of the divider
            int[] dividerLocation = new int[2];
            mDivider.getLocationOnScreen(dividerLocation); // gets top left corner
            if(mOrientation){ // horizontal
                int y_min = (int) (detector.getInitialFocus().y + detector.getTotalFocusDeltaY() - detector.getSpan().y);
                int y_max = (int) (detector.getInitialFocus().y + detector.getTotalFocusDeltaY() + detector.getSpan().y);
                int y = dividerLocation[1] + DIVIDER_SIZE / 2;
                if( y_min < y && y_max > y){
                    return true;
                }
            }else{ // vertical
                int x_min = (int) (detector.getInitialFocus().x + detector.getTotalFocusDeltaX() - detector.getSpan().x);
                int x_max = (int) (detector.getInitialFocus().x + detector.getTotalFocusDeltaX() + detector.getSpan().x);
                int x = dividerLocation[0] + DIVIDER_SIZE / 2;
                if( x_min < x && x_max > x){
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean onSwipe(SwipeGestureDetector detector) {
            // this check is necessary to cancel the swipe event in case the divide was deleted by
            // moving it outside of the window
            if(mDivider != null) {
                moveSplit(detector.getFocusDelta());
            }
            //debug toast
            debugText = "Swipe (depth " + mDepth + ")\n" +
                    "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                    "Delta " + detector.getTotalFocusDeltaX() + " " + detector.getTotalFocusDeltaY();
            return true;
        }

        @Override
        public boolean isMyOutSwipe(SwipeGestureDetector detector) {
            // at depth 0, handle if there is no split
            if(mDepth == 0 && getChildCount() == 1){
                return true;
            }

            // don't handle if there is no split
            if(getChildCount() <= 1){
                return false;
            }

            // orientation: horizontal true
            //              vertical   false
            // getEdge: left    1
            //          right   2
            //          top    -1
            //          bottom -2
            if((mOrientation ? -1 : 1) * detector.getEdge() < 0) {
                // only accept swipes on the same edge that created a view
                return false;
            } else {
                // if the child that is about to be deleted has children itself, let the child deal with it
                if(Math.abs(detector.getEdge()) == 1 && mStart.getChildCount() > 1){
                    return false;
                }
                if(Math.abs(detector.getEdge()) == 2 && mEnd.getChildCount() > 1){
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean onOutSwipeBegin(SwipeGestureDetector detector) {
            // orientation: horizontal true
            //              vertical   false
            // getEdge: left    1
            //          right   2
            //          top    -1
            //          bottom -2
            // special case for depth 0
            if(mDepth == 0 && getChildCount() == 1 && !(getChildAt(0) instanceof AppDrawer)){
                removeAllViews();
                AppDrawer appDrawer = new AppDrawer(getContext(),WIDTH);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                addView(appDrawer, params);
            }

            switch((mOrientation ? -1 : 1) * detector.getEdge() * (getChildCount() > 1 ? 1 : 0)){
                case 1:
                    keepOnly(mEnd);
                    break;
                case 2:
                    keepOnly(mStart);
                    break;
            }

            // debug toast
            debugText = "OutSwipe " + detector.getEdge() + "\n" +
                    "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                    "Delta " + detector.getTotalFocusDeltaX() + " " + detector.getTotalFocusDeltaY();
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
                    if (detector.getEdge()==1){
                        add(START);
                    }else{
                        add(END);
                    }
                }else{
                    mOrientation = ORIENT_H;
                    if (detector.getEdge()==-1){
                        add(START);
                    }else{
                        add(END);
                    }
                }
            } else if (mDepth < 2) {
                boolean isMStart = isMStart();
                if (mOrientation == ORIENT_H) {
                    if (detector.getEdge() == -1 ){ // top
                        add(START);
                    }else if (detector.getEdge() == -2){ // bottom
                        add(END);
                    }else if (isMStart) { // is left child
                        if (detector.getEdge() == 1) {// left
                            if (detector.getInitialFocus().y < HEIGHT / 2) { // upper left half
                                add(START);
                            } else { // lower left half
                                add(END);
                            }

                        }
                    }else { // is right child
                        if (detector.getEdge() == 2) {// right
                            if (detector.getInitialFocus().y < HEIGHT / 2) { // upper right half
                                add(START);
                            } else { // lower right half
                                add(END);
                            }

                        }
                    }

                } else {
                    if (detector.getEdge() == 1 ){ // left
                        add(START);
                    }else if (detector.getEdge() == 2){ // right
                        add(END);
                    }else if (isMStart) { // is top child
                        if (detector.getEdge() == -1) {// top
                            if (detector.getInitialFocus().x < WIDTH/ 2) { // top left half
                                add(START);
                            } else { // top right half
                                add(END);
                            }

                        }
                    }else { // is bottom child
                        if (detector.getEdge() == -2) {// bottom
                            if (detector.getInitialFocus().x < WIDTH/ 2) { // bottom left half
                                add(START);
                            } else { // bottom right half
                                add(END);
                            }

                        }
                    }
                }
            }
//            switch ((mOrientation ? -1 : 1) * detector.getEdge() * (mDepth < 2 ? 1 : 0)) {
//                case 1:
//                    add(START);
//                    break;
//                case 2:
//                    add(END);
//                    break;
//            }

            // debug toast
            debugText = "InSwipe " + detector.getEdge() + "\n" +
                    "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                    "Delta " + detector.getTotalFocusDeltaX() + " " + detector.getTotalFocusDeltaY();
            return true;
        }
    }
}
