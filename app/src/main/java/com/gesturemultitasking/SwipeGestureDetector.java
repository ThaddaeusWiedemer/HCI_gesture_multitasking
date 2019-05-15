package com.gesturemultitasking;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.almeros.android.multitouch.BaseGestureDetector;

public class SwipeGestureDetector extends BaseGestureDetector {

    /**
     * Listener which must be implemented which is used by MoveGestureDetector
     * to perform callbacks to any implementing class which is registered to a
     * MoveGestureDetector via the constructor.
     *
     * @see SwipeGestureDetector.SimpleOnMoveGestureListener
     */
    public interface OnMoveGestureListener {
        public boolean onSwipe(SwipeGestureDetector detector);
        public boolean onOutSwipe(SwipeGestureDetector detector);
        public boolean onInSwipe(SwipeGestureDetector detector);
        public boolean onSwipeBegin(SwipeGestureDetector detector);
        public void onSwipeEnd(SwipeGestureDetector detector);
    }

    /**
     * Helper class which may be extended and where the methods may be
     * implemented. This way it is not necessary to implement all methods
     * of OnEdgeGestureListener.
     */
    public static class SimpleOnMoveGestureListener implements SwipeGestureDetector.OnMoveGestureListener {
        public boolean onSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean onOutSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean onInSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean onSwipeBegin(SwipeGestureDetector detector) {
            return true;
        }

        public void onSwipeEnd(SwipeGestureDetector detector) {
            // Do nothing, overridden implementation may be used
        }
    }

    private static final PointF FOCUS_DELTA_ZERO = new PointF();

    private final SwipeGestureDetector.OnMoveGestureListener mListener;

    private PointF mCurrFocusInternal;
    private PointF mPrevFocusInternal;
    private PointF mInitFocus = new PointF();
    private PointF mFocusExternal = new PointF();
    private PointF mFocusDeltaExternal = new PointF();


    public SwipeGestureDetector(Context context, SwipeGestureDetector.OnMoveGestureListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected void handleStartProgressEvent(int actionCode, MotionEvent event){
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                resetState(); // In case we missed an UP/CANCEL event

                mInitFocus = determineFocalPoint(event);
                mFocusExternal = new PointF(0, 0);
                mPrevEvent = MotionEvent.obtain(event);
                mTimeDelta = 0;

                updateStateByEvent(event);
                break;

            case MotionEvent.ACTION_MOVE:
                mGestureInProgress = mListener.onSwipeBegin(this);
                break;
        }
    }

    @Override
    protected void handleInProgressEvent(int actionCode, MotionEvent event){
        switch (actionCode) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mListener.onSwipeEnd(this);
                resetState();
                break;

            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                    // determine if it's an in or out swipe
                    int x_lower = 300;
                    int x_upper = 780;
                    int y_lower = 300;
                    int y_upper = 1620;
                    final boolean updatePrevious;
                    if(mInitFocus.x < x_lower && mFocusExternal.x < 0){
                        updatePrevious = mListener.onOutSwipe(this);
                    } else if(mInitFocus.x < x_lower && mFocusExternal.x > 0){
                        updatePrevious = mListener.onInSwipe(this);
                    } else if(mInitFocus.x > x_upper && mFocusExternal.x > 0){
                        updatePrevious = mListener.onOutSwipe(this);
                    } else if(mInitFocus.x > x_upper && mFocusExternal.x < 0){
                        updatePrevious = mListener.onInSwipe(this);
                    } else if(mInitFocus.y < y_lower && mFocusExternal.y < 0){
                        updatePrevious = mListener.onOutSwipe(this);
                    } else if(mInitFocus.y < y_lower && mFocusExternal.y > 0){
                        updatePrevious = mListener.onInSwipe(this);
                    } else if(mInitFocus.y > y_upper && mFocusExternal.y > 0){
                        updatePrevious = mListener.onOutSwipe(this);
                    } else if(mInitFocus.y > y_upper && mFocusExternal.y < 0){
                        updatePrevious = mListener.onInSwipe(this);
                    } else {
                        updatePrevious = mListener.onSwipe(this);
                    }
                    if (updatePrevious) {
                        mPrevEvent.recycle();
                        mPrevEvent = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        final MotionEvent prev = mPrevEvent;

        // Focus internal
        mCurrFocusInternal = determineFocalPoint(curr);
        mPrevFocusInternal = determineFocalPoint(prev);

        // Focus external
        // - Prevent skipping of focus delta when a finger is added or removed
        boolean mSkipNextMoveEvent = prev.getPointerCount() != curr.getPointerCount();
        mFocusDeltaExternal = mSkipNextMoveEvent ? FOCUS_DELTA_ZERO : new PointF(mCurrFocusInternal.x - mPrevFocusInternal.x,  mCurrFocusInternal.y - mPrevFocusInternal.y);

        // - Don't directly use mFocusInternal (or skipping will occur). Add
        // 	 unskipped delta values to mFocusExternal instead.
        mFocusExternal.x += mFocusDeltaExternal.x;
        mFocusExternal.y += mFocusDeltaExternal.y;
    }

    /**
     * Determine (multi)finger focal point (a.k.a. center point between all
     * fingers)
     *
     * @param MotionEvent e
     * @return PointF focal point
     */
    private PointF determineFocalPoint(MotionEvent e){
        // Number of fingers on screen
        final int pCount = e.getPointerCount();
        float x = 0f;
        float y = 0f;

        for(int i = 0; i < pCount; i++){
            x += e.getX(i);
            y += e.getY(i);
        }

        return new PointF(x/pCount, y/pCount);
    }

    public float getFocusX() {
        return mFocusExternal.x;
    }

    public float getFocusY() {
        return mFocusExternal.y;
    }

    public PointF getFocusDelta() {
        return mFocusDeltaExternal;
    }

    public PointF getInitialFocus() {
        return mInitFocus;
    }
}