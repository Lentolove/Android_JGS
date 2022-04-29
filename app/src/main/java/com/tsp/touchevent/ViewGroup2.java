package com.tsp.touchevent;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * author : shengping.tian
 * time   : 2021/11/29
 * desc   :
 * version: 1.0
 */
public class ViewGroup2 extends LinearLayout {

    public static final String TAG = ViewGroup2.class.getSimpleName();


    public ViewGroup2(Context context) {
        super(context);
    }

    public ViewGroup2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewGroup2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG, "dispatchTouchEvent: ");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: ");
        return super.onTouchEvent(event);
    }
}
