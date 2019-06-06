package com.gesturemultitasking;

import android.animation.LayoutTransition;
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

public class AppContainer extends ConstraintLayout {
    public int mDepth;
    public boolean isHorizontal;
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
    public final static int DIVIDER_SIZE = 16;
    
    public final static int WIDTH = 2160;
    public final static int HEIGHT = 1920;
    public final static int DELETE_THRESHOLD = 250;

    // gesture handling
    private ScaleGestureDetector mPinchGestureDetector;
    private SwipeDetector mSwipeDetector;
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
        isHorizontal = orientation;

        // initialize gesture detectors
        mPinchGestureDetector = new ScaleGestureDetector(context, new PinchListener());
        mSwipeDetector = new SwipeDetector(context, new SwipeListener(), 40, 200, WIDTH, HEIGHT);

        this.setBackgroundColor(mColor);
        this.setLayoutTransition(new LayoutTransition());
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
        mStart = new AppContainer(getContext(), mDepth + 1, !isHorizontal);
        mStart.setId(View.generateViewId());
        mEnd = new AppContainer(getContext(), mDepth + 1, !isHorizontal);
        mEnd.setId(View.generateViewId());
        split(mDivider, mStart, mEnd);

        // create appDrawer
        AppDrawer appDrawer;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(isHorizontal){ // horizontal
            appDrawer = new AppDrawer(getContext(), getMeasuredWidth());
        }else{ // vertical
            appDrawer = new AppDrawer(getContext(), (getMeasuredWidth() - AppContainer.DIVIDER_SIZE)/2);
        }

        // add content on both sides
        if(side){ // new content on start side
            mEnd.addView(content);
            mStart.addView(appDrawer, params);
            // update end side
            if(isHorizontal){ // horizontal
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
            if(isHorizontal){ // horizontal
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
        ConstraintSet constraints = new ConstraintSet();

        // add the divider, set distance from top/left
        mDivider = divider;
        addView(mDivider);
        if (isHorizontal){
            // horizontal
            constraints.connect(mDivider.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraints.connect(mDivider.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraints.connect(mDivider.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP,(getMeasuredHeight() - AppContainer.DIVIDER_SIZE)/2);
        }else{
            // vertical
            constraints.connect(mDivider.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraints.connect(mDivider.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
            constraints.connect(mDivider.getId(), ConstraintSet.START, getId(), ConstraintSet.START,(getMeasuredWidth() - AppContainer.DIVIDER_SIZE)/2);
        }

        // add the two ConstraintLayouts on both sides of the divider
        mStart = start;
        addView(mStart);
        mEnd = end;
        addView(mEnd);
        if (isHorizontal){
            constraints.connect(mStart.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraints.connect(mStart.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraints.connect(mStart.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraints.connect(mStart.getId(), ConstraintSet.BOTTOM, mDivider.getId(), ConstraintSet.TOP);
            constraints.connect(mEnd.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraints.connect(mEnd.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraints.connect(mEnd.getId(), ConstraintSet.TOP, mDivider.getId(), ConstraintSet.BOTTOM);
            constraints.connect(mEnd.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
        }else{
            constraints.connect(mStart.getId(), ConstraintSet.START, getId(), ConstraintSet.START);
            constraints.connect(mStart.getId(), ConstraintSet.END, mDivider.getId(), ConstraintSet.START);
            constraints.connect(mStart.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraints.connect(mStart.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
            constraints.connect(mEnd.getId(), ConstraintSet.START, mDivider.getId(), ConstraintSet.END);
            constraints.connect(mEnd.getId(), ConstraintSet.END, getId(), ConstraintSet.END);
            constraints.connect(mEnd.getId(), ConstraintSet.TOP, getId(), ConstraintSet.TOP);
            constraints.connect(mEnd.getId(), ConstraintSet.BOTTOM, getId(), ConstraintSet.BOTTOM);
        }

        // apply constraints
        constraints.applyTo(this);

        // draw divider correctly
        if(isHorizontal){
            mDivider.getLayoutParams().height = DIVIDER_SIZE;
        }else{
            mDivider.getLayoutParams().width = DIVIDER_SIZE;
        }
        mDivider.setBackgroundColor(Color.argb(255, 0, 0, 0));
    }

    @SuppressLint("ResourceAsColor")
    public void keepOnly(AppContainer keepSide){
        if(keepSide.isMStart() ? mEnd.isSplit() : mStart.isSplit()){
            ColorBlocksActivity.nWindows -= 2;
        } else{
            ColorBlocksActivity.nWindows--;
        }
        if(keepSide.isSplit()){ // the content that has to be copied over is a split AppContainer
            // get all children
            View divider = keepSide.getChildAt(0);
            AppContainer start = (AppContainer)keepSide.getChildAt(1);
            AppContainer end = (AppContainer)keepSide.getChildAt(2);
            keepSide.removeAllViews();
            removeAllViews();
            // recreate split
            isHorizontal = !isHorizontal;
            split(divider, start, end);
            mStart.mDepth--;
            mEnd.mDepth--;
        } else { // the content that has to be copied over is an AppContainer
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
    }

    private void moveSplit(PointF delta){
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        int[] dividerLocation = new int[2];
        mDivider.getLocationOnScreen(dividerLocation); // gets top left corner

        if (isHorizontal) { // horizontal
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
        if (isSplit()){
            // case split: update children
            if (isHorizontal == ORIENT_H) {
                mStart.update(newWidth);
                mEnd.update(newWidth);
            }
        } else if(getChildAt(0) instanceof AppDrawer){
            // case appDrawer displayed: update self
            AppDrawer appDrawer = (AppDrawer) getChildAt(0);
            appDrawer.update(newWidth);
        }
    }

    private boolean isMStart(){return ((AppContainer)getParent()).mStart == this;}

    public boolean isSplit(){return mDivider != null;}

    // function gets called in parent before child and lets the parent take possession of a motion
    // event
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        boolean intercept = mSwipeDetector.isMyEvent(event);
        //mPinchGestureDetector.onTouchEvent(event);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mSwipeDetector.onTouchEvent(event);
        //mPinchGestureDetector.onTouchEvent(event);
        return true;
    }

    // handles pinch gesture
    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return true;
        }
    }

    // handles in-/out-/swipe gestures
    private class SwipeListener extends SwipeDetector.SimpleOnSwipeListener {
        @Override
        public boolean isMySwipe(SwipeDetector detector){
            // handle if fingers are on both sides of the divider
            if(isSplit()) {
                int[] dividerLocation = new int[2];
                mDivider.getLocationOnScreen(dividerLocation); // gets top left corner
                if (isHorizontal) { // horizontal
                    int y_min = (int) (detector.getInitialFocus().y + detector.getTotalFocusDeltaY() - detector.getSpan().y);
                    int y_max = (int) (detector.getInitialFocus().y + detector.getTotalFocusDeltaY() + detector.getSpan().y);
                    int y = dividerLocation[1] + DIVIDER_SIZE / 2;
                    return y_min < y && y_max > y;
                } else { // vertical
                    int x_min = (int) (detector.getInitialFocus().x + detector.getTotalFocusDeltaX() - detector.getSpan().x);
                    int x_max = (int) (detector.getInitialFocus().x + detector.getTotalFocusDeltaX() + detector.getSpan().x);
                    int x = dividerLocation[0] + DIVIDER_SIZE / 2;
                    return x_min < x && x_max > x;
                }
            }
            return false;
        }

        @Override
        public boolean onSwipe(SwipeDetector detector) {
            // this check is necessary to cancel the swipe event in case the divide was deleted by
            // moving it outside of the window
            if(mDivider != null) {
                moveSplit(detector.getFocusDelta());
            }
            return true;
        }

        @Override
        public boolean isMyOutSwipe(SwipeDetector detector) {
            // at depth 0, handle if there is no split
            if(mDepth == 0 && !isSplit()){
                return true;
            } else if(isSplit()) {
                // handle, unless child to be deleted has children itself
                if(isHorizontal){
                    if(detector.getEdge() == SwipeDetector.EDGE_TOP && !mStart.isSplit()){
                        return true;
                    }
                    if(detector.getEdge() == SwipeDetector.EDGE_BOTTOM && !mEnd.isSplit()){
                        return true;
                    }
                    if(detector.getInitialFocus().y < HEIGHT / 2 && !mStart.isSplit()){
                        return true;
                    }
                    if(detector.getInitialFocus().y > HEIGHT / 2 && !mEnd.isSplit()){
                        return true;
                    }
                }else{
                    if(detector.getEdge() == SwipeDetector.EDGE_LEFT && !mStart.isSplit()){
                        return true;
                    }
                    if(detector.getEdge() == SwipeDetector.EDGE_RIGHT && !mEnd.isSplit()){
                        return true;
                    }
                    if(detector.getInitialFocus().x < WIDTH / 2 && !mStart.isSplit()){
                        return true;
                    }
                    if(detector.getInitialFocus().x > WIDTH / 2 && !mEnd.isSplit()){
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public boolean onOutSwipeBegin(SwipeDetector detector) {
            // getEdge: left    1
            //          right   2
            //          top    -1
            //          bottom -2
            if(mDepth == 0 && !isSplit() && !(getChildAt(0) instanceof AppDrawer)){
                // special case for depth 0: go back to appDrawer
                removeAllViews();
                AppDrawer appDrawer = new AppDrawer(getContext(), WIDTH);
                LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                addView(appDrawer, params);
                return true;
            } else if(isSplit()){
                if(isHorizontal){
                    if(detector.getEdge() == SwipeDetector.EDGE_TOP){
                        keepOnly(mEnd);
                        return true;
                    }
                    if(detector.getEdge() == SwipeDetector.EDGE_BOTTOM){
                        keepOnly(mStart);
                        return true;
                    }
                    if(detector.getInitialFocus().y < HEIGHT / 2){
                        keepOnly(mEnd);
                        return true;
                    }
                    if(detector.getInitialFocus().y > HEIGHT / 2){
                        keepOnly(mStart);
                        return true;
                    }
                }else{
                    if(detector.getEdge() == SwipeDetector.EDGE_LEFT){
                        keepOnly(mEnd);
                        return true;
                    }
                    if(detector.getEdge() == SwipeDetector.EDGE_RIGHT){
                        keepOnly(mStart);
                        return true;
                    }
                    if(detector.getInitialFocus().x < WIDTH / 2){
                        keepOnly(mEnd);
                        return true;
                    }
                    if(detector.getInitialFocus().x > WIDTH / 2){
                        keepOnly(mStart);
                        return true;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean isMyInSwipe(SwipeDetector detector){
            return !isSplit();
        }

        @Override
        public boolean onInSwipeBegin(SwipeDetector detector) {
            if(mDepth == 0){
                // handle special case depth 0: orientation has to be set
                isHorizontal = detector.getEdge() > 0 ? ORIENT_V : ORIENT_H;
                add(Math.abs(detector.getEdge()) == 1 ? START : END);
            } else if (mDepth < 2) {
                // handle other cases
                boolean isMStart = isMStart();
                if (isHorizontal) {
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
            return true;
        }
    }
}
