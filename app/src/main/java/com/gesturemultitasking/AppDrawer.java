package com.gesturemultitasking;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;

import java.util.Random;

public class AppDrawer extends ScrollView {
    GridLayout mGridLayout;
    int mSubViewWidth = 400;

    public AppDrawer(Context context) {
        this(context, null);
    }

    public AppDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Make and add GridLayout
        mGridLayout = new GridLayout(getContext());
        // Breaks if this is removed. This seems to be necessary in order to add a gridlayout to another layout...
        mGridLayout.setColumnCount(3);
        mGridLayout.setUseDefaultMargins(true);
        addView(mGridLayout);


        // add dummy apps
        Random rnd = new Random();
        for (int i = 0; i < 24; i++) {
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            View v = new View(getContext());
            v.setBackgroundColor(color);
            addApp(v);
        }
    }

    public void addApp(View view) {
        // Layout parameters
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = mSubViewWidth;
        params.height = mSubViewWidth * 3 / 2;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

        // OnClickListener
        view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup parent = (ViewGroup) getParent();

                    mGridLayout.removeView(v);
                    LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ViewGroup.LayoutParams p = new ViewPager.LayoutParams();
                    parent.setLayoutTransition(new LayoutTransition());

                    parent.addView(v, params);
                    parent.removeViewAt(0);

                }
        });

        mGridLayout.addView(view, params);
    }

    /**
     * used to set the number of columns dynamically.
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int nColumns = width / (mSubViewWidth + 20);
        mGridLayout.setColumnCount(nColumns);
    }
}


