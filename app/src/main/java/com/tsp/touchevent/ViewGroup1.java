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
public class ViewGroup1 extends LinearLayout {

    public static final String TAG = ViewGroup1.class.getSimpleName();


    public ViewGroup1(Context context) {
        super(context);
    }

    public ViewGroup1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewGroup1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return true;
    }
}
