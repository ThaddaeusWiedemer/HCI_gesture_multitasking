package com.gesturemultitasking;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AppDrawer extends ScrollView {
    private GridLayout mGridLayout;
    private final int MINAPPWIDTH = 400;
    private int APPCOUNT= 28;
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

        WebView web1 = makeWebClient("https://www.shine.cn");
        mApps.add(web1);

        WebView web2 = makeWebClient("http://m.xinhuanet.com/");
        mApps.add(web2);

        WebView web3 = makeWebClient("http://www.taiwan.cn/m/");
        mApps.add(web3);

        WebView web4 = makeWebClient("http://wap.chengdu.cn/");
        mApps.add(web4);

        CalendarView cal1 = new CalendarView(getContext());
        mApps.add(cal1);

        View col1 = new View(getContext());
        int c1 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        col1.setBackgroundColor(c1);
        mApps.add(col1);

        View col2 = new View(getContext());
        int c2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        col2.setBackgroundColor(c2);
        mApps.add(col2);

        View col3 = new View(getContext());
        int c3 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        col3.setBackgroundColor(c3);
        mApps.add(col3);

        View col4 = new View(getContext());
        int c4 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        col4.setBackgroundColor(c4);
        mApps.add(col4);

        APPCOUNT = mApps.size();

        Collections.shuffle(mApps);

        for (int i = 0; i<APPCOUNT; i++){
            myFrameLayout frame = new myFrameLayout(getContext());
            frame.addView((View)mApps.get(i));
            mApps.set(i, frame);

        }
        draw();
    }
    private WebView makeWebClient(String url){
        WebView web1 = new WebView(getContext());
        web1.loadUrl(url);
        web1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
        return web1;
    }

    public void draw() {
        for(int i = 0; i < APPCOUNT; i++) {
            // Layout parameters
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = MINAPPWIDTH;
            params.height = MINAPPWIDTH * 3 / 2;
            params.columnSpec = GridLayout.spec(i % columnCount, 1.f);
            // add views
            //myFrameLayout fr = new myFrameLayout(getContext());

            //fr.addView((View) mApps.get(i));
            //mGridLayout.addView(fr, params);
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


