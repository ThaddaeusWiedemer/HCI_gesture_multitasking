package com.gesturemultitasking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;

public class myFrameLayout extends FrameLayout {
    public myFrameLayout(Context context) {
        this(context, null);
    }

    public myFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public myFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup appContainer = (ViewGroup) getParent().getParent().getParent();
                View child = getChildAt(0);

                removeView(child);

                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                appContainer.addView(child, params);
                appContainer.removeViewAt(0);

            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
        //return super.onInterceptTouchEvent(ev);
    }
}
