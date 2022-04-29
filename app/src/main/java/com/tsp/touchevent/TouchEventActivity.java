package com.tsp.touchevent;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.tsp.android.jgs.R;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * author : shengping.tian
 * time   : 2021/11/29
 * desc   :
 * version: 1.0
 */
public class TouchEventActivity extends AppCompatActivity {

    public static final String TAG = TouchEventActivity.class.getSimpleName();
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_envnt);

        PriorityQueue<int[]> queue = new PriorityQueue<>((o1, o2) -> 0);
        queue.add(new int[]{1,2});
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
