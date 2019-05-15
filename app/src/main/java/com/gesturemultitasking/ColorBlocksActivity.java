package com.gesturemultitasking;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class ColorBlocksActivity extends AppCompatActivity {
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

        // Initial Layout
        setContentView(R.layout.activity_color_blocks);
        ConstraintLayout root = findViewById(R.id.root);
        MyConstraintLayout first = (MyConstraintLayout)root.getChildAt(0);
//        DemoWindow first = new DemoWindow(this, "first");
//        first.setId(View.generateViewId());
//
//        ConstraintLayout.LayoutParams firstp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
//        first.setLayoutParams(firstp);
//
//        root.addView(first);
//
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(root);
//        constraintSet.connect(first.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP);
//        constraintSet.connect(first.getId(), ConstraintSet.BOTTOM, root.getId(), ConstraintSet.BOTTOM);
//        constraintSet.connect(first.getId(), ConstraintSet.START, root.getId(), ConstraintSet.START);
//        constraintSet.connect(first.getId(), ConstraintSet.END, root.getId(), ConstraintSet.END);
//
//        constraintSet.applyTo(root);
//        ColorBlocksActivity.addWindow(first, "top");
//
//
//
//
//
//
//        mScaleGestureDetector = new ScaleGestureDetector(this, new PinchListener());
//        mMoveGestureDetector = new SwipeGestureDetector(this, new SwipeListener());

        // debug toasts
        Context context = getApplicationContext();
        toast = new Toast(context);
    }
}

