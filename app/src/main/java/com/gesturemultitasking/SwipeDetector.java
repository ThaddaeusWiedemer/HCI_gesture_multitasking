package com.gesturemultitasking;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.almeros.android.multitouch.BaseGestureDetector;

public class SwipeDetector extends BaseGestureDetector {

    /**
     * Listener which must be implemented which is used by SwipeDetector
     * to perform callbacks to any implementing class which is registered to a
     * SwipeDetector via the constructor.
     *
     * @see SimpleOnSwipeListener
     */
    public interface OnSwipeListener {
        boolean onSwipe(SwipeDetector detector);
        boolean onOutSwipe(SwipeDetector detector);
        boolean onInSwipe(SwipeDetector detector);
        boolean isMySwipe(SwipeDetector detector);
        boolean isMyOutSwipe(SwipeDetector detector);
        boolean isMyInSwipe(SwipeDetector detector);
        boolean onSwipeBegin(SwipeDetector detector);
        boolean onOutSwipeBegin(SwipeDetector detector);
        boolean onInSwipeBegin(SwipeDetector detector);
        void onSwipeEnd(SwipeDetector detector);
    }

    /**
     * Helper class which may be extended and where the methods may be
     * implemented. This way it is not necessary to implement all methods
     * of OnEdgeGestureListener.
     */
    public static class SimpleOnSwipeListener implements OnSwipeListener {
        public boolean onSwipe(SwipeDetector detector) {
            return false;
        }

        public boolean onOutSwipe(SwipeDetector detector) {
            return false;
        }

        public boolean onInSwipe(SwipeDetector detector) {
            return false;
        }

        public boolean isMySwipe(SwipeDetector detector) {
            return false;
        }

        public boolean isMyOutSwipe(SwipeDetector detector) {
            return false;
        }

        public boolean isMyInSwipe(SwipeDetector detector) {
            return false;
        }

        public boolean onSwipeBegin(SwipeDetector detector) {
            return true;
        }

        public boolean onOutSwipeBegin(SwipeDetector detector) {
            return true;
        }

        public boolean onInSwipeBegin(SwipeDetector detector) {
            return true;
        }

        public void onSwipeEnd(SwipeDetector detector) {
            // Do nothing, overridden implementation may be used
        }
    }

    private static final PointF FOCUS_DELTA_ZERO = new PointF();

    private final OnSwipeListener mListener;

    private PointF mInitFocus = new PointF();
    private PointF mFocusExternal = new PointF();
    private PointF mFocusDeltaExternal = new PointF();
    private PointF mSpan = new PointF();

    // type of gesture
    private int mType;
    private static final int TYPE_SWIPE = 0;
    private static final int TYPE_IN_SWIPE = 1;
    private static final int TYPE_OUT_SWIPE = 2;

    // type for in- and out-swipes
    private int mEdge;
    public static final int EDGE_LEFT = 1;
    public static final int EDGE_RIGHT = 2;
    public static final int EDGE_NONE = 0;
    public static final int EDGE_TOP = -1;
    public static final int EDGE_BOTTOM = -2;

    // edge-swipe thresholds
    private final int THRESH_IN;
    private final int THRESH_OUT;
    private final int WIDTH;
    private final int HEIGHT;

    SwipeDetector(Context context, OnSwipeListener listener) {
        this(context, listener, 50, 400, 2160, 1920);
    }

    SwipeDetector(Context context, OnSwipeListener listener, int in, int out, int width, int height) {
        super(context);
        mListener = listener;

        //set edge-swipe thresholds
        THRESH_IN = in;
        THRESH_OUT = out;
        WIDTH = width;
        HEIGHT = height;
    }

    // called by onInterceptTouchEvent, determines if gesture should be handled by an element
    boolean isMyEvent(MotionEvent event){
        final int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        if (!mGestureInProgress) {
            return isMyStartProgressEvent(actionCode, event);
        } else {
            return isMyInProgressEvent(actionCode, event);
        }
    }

    @Override
    // called by onTouchEvent, handles gesture
    public boolean onTouchEvent(MotionEvent event){
        final int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        if (!mGestureInProgress) {
            handleStartProgressEvent(actionCode, event);
        } else {
            handleInProgressEvent(actionCode, event);
        }
        return true;
    }

    private boolean isMyStartProgressEvent(int actionCode, MotionEvent event){
        boolean isMine = false;
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_DOWN:
                resetState(); // In case we missed an UP/CANCEL event
                mInitFocus = determineFocalPoint(event);
                mFocusExternal = new PointF(0, 0);
                mPrevEvent = MotionEvent.obtain(event);
                mTimeDelta = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 2) {
                    mGestureInProgress = true;
                    updateStateByEvent(event);
                    determineSwipeType();
                    switch(mType){
                        case TYPE_IN_SWIPE:
                            isMine = mListener.isMyInSwipe(this);
                            if(isMine){mListener.onInSwipeBegin(this);}
                            break;
                        case TYPE_OUT_SWIPE:
                            isMine = mListener.isMyOutSwipe(this);
                            if(isMine){mListener.onOutSwipeBegin(this);}
                            break;
                        default:
                            isMine = mListener.isMySwipe(this);
                            if(isMine){mListener.onSwipeBegin(this);}
                            break;
                    }
                    mPrevEvent.recycle();
                    mPrevEvent = MotionEvent.obtain(event);
                }
                break;
        }
        return isMine;
    }

    @Override
    // this function is only called when the element doesn't have an interceptor, otherwise the
    // start of the gesture has already been handled in there
    protected void handleStartProgressEvent(int actionCode, MotionEvent event){
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_DOWN:
                resetState(); // In case we missed an UP/CANCEL event
                mInitFocus = determineFocalPoint(event);
                mFocusExternal = new PointF(0, 0);
                mPrevEvent = MotionEvent.obtain(event);
                mTimeDelta = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 2) {
                    mGestureInProgress = true;
                    updateStateByEvent(event);
                    determineSwipeType();
                    switch(mType){
                        case TYPE_IN_SWIPE:
                            mListener.onInSwipeBegin(this);
                            break;
                        case TYPE_OUT_SWIPE:
                            mListener.onOutSwipeBegin(this);
                            break;
                        default:
                            mListener.onSwipeBegin(this);
                            break;
                    }
                    mPrevEvent.recycle();
                    mPrevEvent = MotionEvent.obtain(event);
                }
                break;
        }
    }

    private boolean isMyInProgressEvent(int actionCode, MotionEvent event){
        switch (actionCode) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mListener.onSwipeEnd(this);
                resetState();
                break;
        }
        return false;
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
                if(event.getPointerCount() == 2) {
                    updateStateByEvent(event);

                    // Only accept the event if our relative pressure is within
                    // a certain limit. This can help filter shaky data as a
                    // finger is lifted.
                    if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                        // determine if it's an in or out swipe
                        boolean updatePrevious;
                        determineSwipeType();
                        switch(mType){
                            case TYPE_IN_SWIPE:
                                updatePrevious = mListener.onInSwipe(this);
                                break;
                            case TYPE_OUT_SWIPE:
                                updatePrevious = mListener.onOutSwipe(this);
                                break;
                            default:
                                updatePrevious = mListener.onSwipe(this);
                                break;
                        }
                        if (updatePrevious) {
                            mPrevEvent.recycle();
                            mPrevEvent = MotionEvent.obtain(event);
                        }
                    }
                }
                break;
        }
    }

    private void determineSwipeType(){
        if (mInitFocus.x < THRESH_OUT && mFocusExternal.x < 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_LEFT;
        } else if (mInitFocus.x < THRESH_IN && mFocusExternal.x > 0) {
            mType = TYPE_IN_SWIPE;
            mEdge = EDGE_LEFT;
        } else if (mInitFocus.x > WIDTH - THRESH_OUT && mFocusExternal.x > 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_RIGHT;
        } else if (mInitFocus.x > WIDTH - THRESH_IN && mFocusExternal.x < 0) {
            mType = TYPE_IN_SWIPE;
            mEdge = EDGE_RIGHT;
        } else if (mInitFocus.y < THRESH_OUT && mFocusExternal.y < 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_TOP;
        } else if (mInitFocus.y < THRESH_IN && mFocusExternal.y > 0) {
            mType = TYPE_IN_SWIPE;
            mEdge = EDGE_TOP;
        } else if (mInitFocus.y > HEIGHT - THRESH_OUT && mFocusExternal.y > 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_BOTTOM;
        } else if (mInitFocus.y > HEIGHT - THRESH_IN && mFocusExternal.y < 0) {
            mType = TYPE_IN_SWIPE;
            mEdge = EDGE_BOTTOM;
        } else {
            mType = TYPE_SWIPE;
            mEdge = EDGE_NONE;
        }
    }

    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        final MotionEvent prev = mPrevEvent;

        // Focus internal
        PointF mCurrFocusInternal = determineFocalPoint(curr);
        PointF mPrevFocusInternal = determineFocalPoint(prev);

        // Focus external
        // - Prevent skipping of focus delta when a finger is added or removed
        boolean mSkipNextMoveEvent = prev.getPointerCount() != curr.getPointerCount();
        mFocusDeltaExternal = mSkipNextMoveEvent ? FOCUS_DELTA_ZERO : new PointF(mCurrFocusInternal.x - mPrevFocusInternal.x,  mCurrFocusInternal.y - mPrevFocusInternal.y);

        // - Don't directly use mFocusInternal (or skipping will occur). Add
        // 	 unskipped delta values to mFocusExternal instead.
        mFocusExternal.x += mFocusDeltaExternal.x;
        mFocusExternal.y += mFocusDeltaExternal.y;
    }

    private PointF determineFocalPoint(MotionEvent e){
        // Number of fingers on screen
        final int pCount = e.getPointerCount();
        float x = 0f;
        float y = 0f;

        for(int i = 0; i < pCount; i++){
            x += e.getX(i);
            y += e.getY(i);
        }

        if(pCount > 1){
            mSpan.x = Math.abs(e.getX(0) - e.getX(1));
            mSpan.y = Math.abs(e.getY(0) - e.getY(1));
        }

        x /= pCount;
        y /= pCount;

        float rawViewX = e.getRawX()-e.getX();
        float rawViewY = e.getRawY()-e.getY();
        x += rawViewX;
        y += rawViewY;

        return new PointF(x, y);
    }

    int getEdge() {
        return mEdge;
    }

    float getTotalFocusDeltaX() {
        return mFocusExternal.x;
    }

    float getTotalFocusDeltaY() {
        return mFocusExternal.y;
    }

    PointF getFocusDelta() {
        return mFocusDeltaExternal;
    }

    PointF getInitialFocus() {
        return mInitFocus;
    }

    PointF getSpan() {
        return mSpan;
    }
}