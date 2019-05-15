package com.gesturemultitasking;

import android.content.Context;
import android.util.AttributeSet;

public class DemoWindow extends android.support.constraint.ConstraintLayout{
    public DemoWindow(Context context){
        this(context, null);
    }

    public DemoWindow(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }
    public DemoWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
       inflate(getContext(), R.layout.demo_window, this);
    }

}
