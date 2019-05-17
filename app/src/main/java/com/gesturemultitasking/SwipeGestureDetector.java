package com.gesturemultitasking;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.almeros.android.multitouch.BaseGestureDetector;

import java.util.List;

public class SwipeGestureDetector extends BaseGestureDetector {

    /**
     * Listener which must be implemented which is used by SwipeGestureDetector
     * to perform callbacks to any implementing class which is registered to a
     * SwipeGestureDetector via the constructor.
     *
     * @see SimpleOnSwipeGestureListener
     */
    public interface OnSwipeGestureListener {
        boolean onSwipe(SwipeGestureDetector detector);
        boolean onOutSwipe(SwipeGestureDetector detector);
        boolean onInSwipe(SwipeGestureDetector detector);
        boolean isMySwipe(SwipeGestureDetector detector);
        boolean isMyOutSwipe(SwipeGestureDetector detector);
        boolean isMyInSwipe(SwipeGestureDetector detector);
        boolean onSwipeBegin(SwipeGestureDetector detector);
        boolean onOutSwipeBegin(SwipeGestureDetector detector);
        boolean onInSwipeBegin(SwipeGestureDetector detector);
        void onSwipeEnd(SwipeGestureDetector detector);
    }

    /**
     * Helper class which may be extended and where the methods may be
     * implemented. This way it is not necessary to implement all methods
     * of OnEdgeGestureListener.
     */
    public static class SimpleOnSwipeGestureListener implements OnSwipeGestureListener {
        public boolean onSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean onOutSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean onInSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean isMySwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean isMyOutSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean isMyInSwipe(SwipeGestureDetector detector) {
            return false;
        }

        public boolean onSwipeBegin(SwipeGestureDetector detector) {
            return true;
        }

        public boolean onOutSwipeBegin(SwipeGestureDetector detector) {
            return true;
        }

        public boolean onInSwipeBegin(SwipeGestureDetector detector) {
            return true;
        }

        public void onSwipeEnd(SwipeGestureDetector detector) {
            // Do nothing, overridden implementation may be used
        }
    }

    private static final PointF FOCUS_DELTA_ZERO = new PointF();

    private final OnSwipeGestureListener mListener;

    private PointF mInitFocus = new PointF();
    private PointF mFocusExternal = new PointF();
    private PointF mFocusDeltaExternal = new PointF();
    private PointF mSpan = new PointF();;

    // type of gesture
    private int mType;
    private static final int TYPE_SWIPE = 0;
    private static final int TYPE_IN_SWIPE = 1;
    private static final int TYPE_OUT_SWIPE = 2;

    // type for in- and out-swipes
    private int mEdge;
    static final int EDGE_LEFT = 1;
    static final int EDGE_RIGHT = 2;
    private static final int EDGE_NONE = 0;
    static final int EDGE_TOP = -1;
    static final int EDGE_BOTTOM = -2;

    // edge-swipe thresholds
    private int thresh_in;
    private int thresh_out;
    private int res_x;
    private int res_y;

    SwipeGestureDetector(Context context, OnSwipeGestureListener listener) {
        this(context, listener, 50, 400, 2160, 1920);
    }

    SwipeGestureDetector(Context context, OnSwipeGestureListener listener, int in, int out, int x, int y) {
        super(context);
        mListener = listener;

        //set edge-swipe thresholds
        thresh_in = in;
        thresh_out = out;
        res_x = x;
        res_y = y;
    }

    boolean isMyEvent(MotionEvent event){
        final int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        if (!mGestureInProgress) {
            return isMyStartProgressEvent(actionCode, event);
        } else {
            return isMyInProgressEvent(actionCode, event);
        }
    }

    @Override
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
            case MotionEvent.ACTION_DOWN:
                resetState(); // In case we missed an UP/CANCEL event

                mInitFocus = determineFocalPoint(event);
                mFocusExternal = new PointF(0, 0);
                mPrevEvent = MotionEvent.obtain(event);
                mTimeDelta = 0;

                updateStateByEvent(event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //updateStateByEvent(event);
                mPrevEvent.recycle();
                mPrevEvent = MotionEvent.obtain(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 2) {
                    updateStateByEvent(event);
                    determineSwipeType();
                    switch(mType){
                        case TYPE_IN_SWIPE:
                            isMine = mListener.isMyInSwipe(this);
                            break;
                        case TYPE_OUT_SWIPE:
                            isMine = mListener.isMyOutSwipe(this);
                            break;
                        default:
                            isMine = mListener.isMySwipe(this);
                            break;
                    }
                    //mGestureInProgress = true;
                    mPrevEvent.recycle();
                    mPrevEvent = MotionEvent.obtain(event);
                }
                break;
        }
        return isMine;
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

            case MotionEvent.ACTION_POINTER_DOWN:
                //updateStateByEvent(event);
                mPrevEvent.recycle();
                mPrevEvent = MotionEvent.obtain(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 2) {
                    updateStateByEvent(event);
                    determineSwipeType();
                    switch(mType){
                        case TYPE_IN_SWIPE:
                            mGestureInProgress = mListener.onInSwipeBegin(this);
                            break;
                        case TYPE_OUT_SWIPE:
                            mGestureInProgress = mListener.onOutSwipeBegin(this);
                            break;
                        default:
                            mGestureInProgress = mListener.onSwipeBegin(this);
                            break;
                    }
                    //mGestureInProgress = mListener.onSwipeBegin(this);
                    mPrevEvent.recycle();
                    mPrevEvent = MotionEvent.obtain(event);
                }
                break;
        }
    }

    private boolean isMyInProgressEvent(int actionCode, MotionEvent event){
        boolean isMine = false;
        switch (actionCode) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mListener.onSwipeEnd(this);
                resetState();
                isMine = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() == 1) {
                    updateStateByEvent(event);

                    // Only accept the event if our relative pressure is within
                    // a certain limit. This can help filter shaky data as a
                    // finger is lifted.
                    if (mCurrPressure / mPrevPressure > PRESSURE_THRESHOLD) {
                        // determine if it's an in or out swipe
                        determineSwipeType();
                        switch(mType){
                            case TYPE_IN_SWIPE:
                                isMine = mListener.isMyInSwipe(this);
                                break;
                            case TYPE_OUT_SWIPE:
                                isMine = mListener.isMyOutSwipe(this);
                                break;
                            default:
                                isMine = mListener.isMySwipe(this);
                                break;
                        }
                        if (isMine) {
                            mPrevEvent.recycle();
                            mPrevEvent = MotionEvent.obtain(event);
                        }

                    }
                }
                break;
        }
        return isMine;
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
        if (mInitFocus.x < thresh_out && mFocusExternal.x < 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_LEFT;
        } else if (mInitFocus.x < thresh_in && mFocusExternal.x > 0) {
            mType = TYPE_IN_SWIPE;
            mEdge = EDGE_LEFT;
        } else if (mInitFocus.x > res_x - thresh_out && mFocusExternal.x > 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_RIGHT;
        } else if (mInitFocus.x > res_x - thresh_in && mFocusExternal.x < 0) {
            mType = TYPE_IN_SWIPE;
            mEdge = EDGE_RIGHT;
        } else if (mInitFocus.y < thresh_out && mFocusExternal.y < 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_TOP;
        } else if (mInitFocus.y < thresh_in && mFocusExternal.y > 0) {
            mType = TYPE_IN_SWIPE;
            mEdge = EDGE_TOP;
        } else if (mInitFocus.y > res_y - thresh_out && mFocusExternal.y > 0) {
            mType = TYPE_OUT_SWIPE;
            mEdge = EDGE_BOTTOM;
        } else if (mInitFocus.y > res_y - thresh_in && mFocusExternal.y < 0) {
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

    public PointF getFocusDelta() {
        return mFocusDeltaExternal;
    }

    PointF getInitialFocus() {
        return mInitFocus;
    }

    public PointF getSpan() {
        return mSpan;
    }
}