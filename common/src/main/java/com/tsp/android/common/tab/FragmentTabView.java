package com.tsp.android.common.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * author : shengping.tian
 * time   : 2021/09/16
 * desc   : Fragment 管理类，将Fragment的操作内聚，提供通用的一些API
 * version: 1.0
 */
public class FragmentTabView extends FrameLayout {

    private TabViewAdapter mAdapter;

    private int mCurrentPosition;

    public FragmentTabView(@NonNull Context context) {
        super(context);
    }

    public FragmentTabView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentTabView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TabViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(TabViewAdapter adapter) {
        if (this.mAdapter != null || adapter == null) return;
        this.mAdapter = adapter;
        mCurrentPosition = -1;
    }

    /**
     * 切换 fragment
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        if (position < 0 || position >= mAdapter.getCount()) return;
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            mAdapter.instantiateItem(this, position);
        }
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public Fragment getCurrentFragment() {
        if (this.mAdapter == null) {
            throw new IllegalArgumentException("please call setAdapter first.");
        }
        return mAdapter.getCurrentFragment();
    }

}
