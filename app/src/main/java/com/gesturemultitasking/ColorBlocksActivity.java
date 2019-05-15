package com.gesturemultitasking;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridLayout.Spec;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ColorBlocksActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private SwipeGestureDetector mMoveGestureDetector;
    private String text = "";
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // fullscreen
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_color_blocks);
        ConstraintLayout root = (ConstraintLayout) findViewById(R.id.root);
        DemoWindow first = new DemoWindow(this, "first");
        first.setId(View.generateViewId());

        ConstraintLayout.LayoutParams firstp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        first.setLayoutParams(firstp);

        root.addView(first);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(root);
        constraintSet.connect(first.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP);
        constraintSet.connect(first.getId(), ConstraintSet.BOTTOM, root.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(first.getId(), ConstraintSet.START, root.getId(), ConstraintSet.START);
        constraintSet.connect(first.getId(), ConstraintSet.END, root.getId(), ConstraintSet.END);

        constraintSet.applyTo(root);
        ColorBlocksActivity.addWindow(first, "top");






        mScaleGestureDetector = new ScaleGestureDetector(this, new PinchListener());
        mMoveGestureDetector = new SwipeGestureDetector(this, new SwipeListener());

        // debug toasts
        Context context = getApplicationContext();
        toast = new Toast(context);
    }

    public static void addWindow(View from, String dir){
        ConstraintLayout parent = (ConstraintLayout) from.getParent();
        int n_els = parent.getChildCount();
        View divider = new View(parent.getContext());
        divider.setId(View.generateViewId());
        int new_id = View.generateViewId();
        DemoWindow new_window = new DemoWindow(parent.getContext(), Integer.toString(new_id));
        new_window.setId(new_id);
        ConstraintLayout.LayoutParams new_params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        new_window.setLayoutParams(new_params);
        ConstraintSet c = new ConstraintSet();
        // Decide what new layout is going to be:
        if (n_els == 1) {
            switch (dir) {
                case "top":
                    c = getHorizontalSplitLayout(parent.getId(), new_window.getId(), divider.getId(), from.getId());
                    parent.addView(divider, 0);
                    parent.addView(new_window, 0);
                    divider.getLayoutParams().height=20;
                    break;

                case "bottom":
                    c = getHorizontalSplitLayout(parent.getId(), from.getId(), divider.getId(), new_window.getId());
                    parent.addView(divider, 1);
                    parent.addView(new_window, 2);
                    divider.getLayoutParams().height=20;
                    break;

                case "left":
                    c = getVerticalSplitLayout(parent.getId(), new_window.getId(), divider.getId(), from.getId());
                    parent.addView(divider, 0);
                    parent.addView(new_window, 0);
                    divider.getLayoutParams().width=20;
                    break;

                case "right":
                    c = getVerticalSplitLayout(parent.getId(), from.getId(), divider.getId(), new_window.getId());
                    parent.addView(divider, 1);
                    parent.addView(new_window, 2);
                    divider.getLayoutParams().width=20;
                    break;
            }
        }
        c.applyTo(parent);

    }
    public static ConstraintSet getVerticalSplitLayout(int root, int left, int div, int right){
        ConstraintSet ret = new ConstraintSet();
        ret.connect(left, ConstraintSet.TOP, root, ConstraintSet.TOP);
        ret.connect(left, ConstraintSet.BOTTOM, root, ConstraintSet.BOTTOM);
        ret.connect(left, ConstraintSet.START, root, ConstraintSet.START);
        ret.connect(left, ConstraintSet.END, div, ConstraintSet.START);

        ret.connect(div, ConstraintSet.TOP, root, ConstraintSet.TOP);
        ret.connect(div, ConstraintSet.BOTTOM, root, ConstraintSet.BOTTOM);
        ret.connect(div, ConstraintSet.START, root, ConstraintSet.START, 150);

        ret.connect(right, ConstraintSet.TOP, root, ConstraintSet.TOP);
        ret.connect(right, ConstraintSet.BOTTOM, root, ConstraintSet.BOTTOM);
        ret.connect(right, ConstraintSet.START, div, ConstraintSet.END);
        ret.connect(right, ConstraintSet.END, root, ConstraintSet.END);

        return ret;
    }

    public static ConstraintSet getHorizontalSplitLayout(int root, int fst, int div, int snd){
        ConstraintSet ret = new ConstraintSet();
        ret.connect(fst, ConstraintSet.START, root, ConstraintSet.START);
        ret.connect(fst, ConstraintSet.END, root, ConstraintSet.END);
        ret.connect(fst, ConstraintSet.TOP, root, ConstraintSet.TOP);
        ret.connect(fst, ConstraintSet.BOTTOM, div, ConstraintSet.TOP);

        ret.connect(div, ConstraintSet.START, root, ConstraintSet.START);
        ret.connect(div, ConstraintSet.END, root, ConstraintSet.END);
        ret.connect(div, ConstraintSet.TOP, root, ConstraintSet.TOP, 150);

        ret.connect(snd, ConstraintSet.START, root, ConstraintSet.START);
        ret.connect(snd, ConstraintSet.END, root, ConstraintSet.END);
        ret.connect(snd, ConstraintSet.TOP, div, ConstraintSet.BOTTOM);
        ret.connect(snd, ConstraintSet.BOTTOM, root, ConstraintSet.BOTTOM);

        return ret;
    }

    // handels all touch events and calls the gesture listeners
    @Override
    public boolean onTouchEvent(MotionEvent event){
        text = "";
        mMoveGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        if(text != "") {
            toast.cancel();
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
        return true;
    }

    // handles pinch gesture
    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            text = "Pinch\n" +
                "Focus " + detector.getFocusX() + " " + detector.getFocusY() + "\n" +
                "Span " + detector.getCurrentSpanX() + " " + detector.getCurrentSpanY();
            return true;
        }
    }

    // handels swipe gestures
    private class SwipeListener extends SwipeGestureDetector.SimpleOnSwipeGestureListener {
        @Override
        public boolean onSwipe(SwipeGestureDetector detector) {
            text = "Swipe\n" +
                "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onOutSwipe(SwipeGestureDetector detector) {
            text = "OutSwipe\n" +
                "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }

        public boolean onInSwipe(SwipeGestureDetector detector) {
            text = "InSwipe\n" +
                "Start " + detector.getInitialFocus().x + " " + detector.getInitialFocus().y + "\n" +
                "Delta " + detector.getFocusX() + " " + detector.getFocusY();
            return true;
        }
    }

}

