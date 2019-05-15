package com.gesturemultitasking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;

public class ColorBlocksActivity extends AppCompatActivity {
    int n_demo_windows = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_color_blocks);
        GridLayout root = (GridLayout) findViewById(R.id.root);
        //DemoWindow first = new DemoWindow(this);
        LayoutParams p = new LayoutParams(LayoutParams.)



        //root.addView(first);
        //add_view(first, "bottom");



    }
   public void add_view(View relative_to, String direction){
        LinearLayout parent = (LinearLayout) relative_to.getParent();
        LinearLayout new_parent = parent;
        DemoWindow new_window = new DemoWindow(this);

        switch (direction){
            case "top":
                if(parent.getChildCount() ==2 ){
                    new_parent = new LinearLayout(this);
                    parent.addView(new_parent);
                    new_parent.addView(relative_to);
                }
                new_parent.setOrientation(LinearLayout.VERTICAL);
                new_parent.addView(new_window, 0);
                break;
            case "bottom":
                if(parent.getChildCount() ==2 ){
                    new_parent = new LinearLayout(this);
                    parent.addView(new_parent);
                    new_parent.addView(relative_to);
                }
                new_parent.setOrientation(LinearLayout.VERTICAL);
                new_parent.addView(new_window, 1);
                break;
            case "left":
                if(parent.getChildCount()==2) {
                    new_parent = new LinearLayout(this);
                    parent.addView(new_parent);
                    new_parent.addView(relative_to);
                }
                new_parent.setOrientation(LinearLayout.HORIZONTAL);
                new_parent.addView(new_window, 0);
                break;
            case "right":
                if(parent.getChildCount()==2) {
                    new_parent = new LinearLayout(this);
                    parent.addView(new_parent);
                    new_parent.addView(relative_to);
                }
                new_parent.setOrientation(LinearLayout.HORIZONTAL);
                new_parent.addView(new_window, 1);
                break;

        }



   }



}
