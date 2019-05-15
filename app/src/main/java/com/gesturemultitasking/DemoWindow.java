package com.gesturemultitasking;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DemoWindow extends android.support.constraint.ConstraintLayout {
    public DemoWindow(Context context, String s){
        this(context, null, s);
    }

    public DemoWindow(Context context, AttributeSet attrs, String s){
        this(context, attrs, 0, s);
    }
    public DemoWindow(Context context, AttributeSet attrs, int defStyleAttr, String s) {
        super(context, attrs, defStyleAttr);
        init(s);
    }

    private void init(String s) {
       inflate(getContext(), R.layout.demo_window, this);
        ((TextView)this.findViewById(R.id.number)).setText(s);
        ((Button)this.findViewById(R.id.top_but)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorBlocksActivity.addWindow(v, "top");
            }
        });
        ((Button)this.findViewById(R.id.bottom_but)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorBlocksActivity.addWindow(v, "bottom");
            }
        });
        ((Button)this.findViewById(R.id.left_but)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorBlocksActivity.addWindow(v, "left");
            }
        });
         ((Button)this.findViewById(R.id.right_but)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorBlocksActivity.addWindow(v, "right");
            }
        });


    }

}
