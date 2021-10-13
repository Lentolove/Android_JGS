package com.tsp.test.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tsp.android.jgs.R;

/**
 * author : shengping.tian
 * time   : 2021/10/09
 * desc   :
 * version: 1.0
 */
public class MViewGroup extends FrameLayout {
    private static final String TAG = "MViewGroup";

    public MViewGroup(@NonNull Context context) {
        super(context);
        init();
    }

    public MViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.leftMargin = 200;
        TextView textView = new TextView(getContext());
        textView.setText("手动添加的TextView");
        textView.setLayoutParams(params);
        addView(textView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(TAG, "onLayout = " + ",left = " + left + ",top = " + top + ",right = " + right + ",bottom = " + bottom);
        View head = getChildAt(0);
        head.layout(left, top + 100, right, bottom + 100);
    }
}
