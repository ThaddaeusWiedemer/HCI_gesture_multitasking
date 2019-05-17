package com.gesturemultitasking;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppDrawer extends ScrollView {
    private GridLayout mGridLayout;
    private final int MINAPPWIDTH = 400;
    private final int APPCOUNT = 28;
    private int columnCount;
    private List mApps = new ArrayList();

    public AppDrawer(Context context) {
        this(context, null);
    }

    public AppDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 2160);
    }

    public AppDrawer(Context context, int width) {
        this(context, null, 0, width);
    }

    public AppDrawer(Context context, AttributeSet attrs, int defStyleAttr, int width) {
        super(context, attrs, defStyleAttr);
        // determine how many cells can fit next to each other
        columnCount = Math.max((width) / (MINAPPWIDTH + 10), 1);
        init();
    }

    public void init() {
        // make a GridLayout
        mGridLayout = new GridLayout(getContext());
        // set margins between cells
        mGridLayout.setUseDefaultMargins(true);
        // add the layout
        addView(mGridLayout);

        // add dummy apps
        Random rnd = new Random();
        for (int i = 0; i < APPCOUNT; i++) {
            // set color
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            View v = new View(getContext());
            v.setBackgroundColor(color);

            // set OnClickListener
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup parent = (ViewGroup) getParent();
                    mGridLayout.removeView(v);
                    LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    parent.addView(v, params);
                    parent.removeViewAt(0);
                    v.setOnClickListener(null);
                }
            });

            // add to list
            mApps.add(v);
        }

        draw();
    }

    public void draw() {
        for(int i = 0; i < APPCOUNT; i++) {
            // Layout parameters
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = MINAPPWIDTH;
            params.height = MINAPPWIDTH * 3 / 2;
            params.columnSpec = GridLayout.spec(i % columnCount, 1.f);
            // add views
            mGridLayout.addView((View) mApps.get(i), params);
        }
    }

    public void update(int newWidth){
        columnCount = Math.max((newWidth) / (MINAPPWIDTH + 10), 1);
        mGridLayout.removeAllViews();
        mGridLayout.setColumnCount(columnCount);
        draw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int nColumns = Math.max((width) / (MINAPPWIDTH + 10), 1);
        mGridLayout.setColumnCount(nColumns);
    }
}


