package com.gesturemultitasking;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ScrollView;

import java.util.Random;

public class AppDrawer extends ScrollView {
    private GridLayout mGridLayout;
    private final int MINAPPWIDTH = 300;
    private final int SUBVIEWCOUNT = 24;
    private int columnCount;
    private int mWidth;
    private int mHeight;

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
        columnCount = width / MINAPPWIDTH - 1;
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
        for (int i = 0; i < SUBVIEWCOUNT; i++) {
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            View v = new View(getContext());
            v.setBackgroundColor(color);
            addApp(v, i);
        }
    }

    public void addApp(View view, int position) {
        // Layout parameters
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = MINAPPWIDTH;
        params.height = MINAPPWIDTH * 3 / 2;
        params.columnSpec = GridLayout.spec(position % columnCount, 1.f);

        // OnClickListener
        view.setOnClickListener(new OnClickListener() {
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
        int nColumns = width / (MINAPPWIDTH + 20);
        mGridLayout.setColumnCount(nColumns);
    }
}


