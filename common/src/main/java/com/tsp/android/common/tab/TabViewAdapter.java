package com.tsp.android.common.tab;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tsp.android.hilibrary.log.HiLog;
import com.tsp.android.hiui.tab.bottom.HiTabBottomInfo;

import java.util.List;

/**
 * author : shengping.tian
 * time   : 2021/09/16
 * desc   : fragment 通用适配器,搭配 TabBottomLayout 使用
 * version: 1.0
 */
public class TabViewAdapter {

    private List<HiTabBottomInfo<?>> mInfoList;

    //当前正在显示的 fragment
    private Fragment mCurrentFragment;

    private FragmentManager mFragmentManager;

    public TabViewAdapter(List<HiTabBottomInfo<?>> infoList, FragmentManager fragmentManager) {
        this.mInfoList = infoList;
        this.mFragmentManager = fragmentManager;
    }

    /**
     * 实例化 fragment
     *
     * @param container 装在 fragment 的 ViewGroup
     * @param position  位置
     */
    public void instantiateItem(View container, int position) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mCurrentFragment != null) {
            //让当前 fragment 隐藏
            transaction.hide(mCurrentFragment);
        }
        //将当前 container 的 id 和位置作为 fragment 的 key
        String tagName = getFragmentTagName(container, position);
        Fragment fragment = mFragmentManager.findFragmentByTag(tagName);
        if (fragment != null) {
            //已经添加过了，直接显示
            transaction.show(fragment);
        } else {
            fragment = createFragmentByPos(position);
            if (fragment != null && !fragment.isAdded()) transaction.add(container.getId(), fragment, tagName);
        }
        mCurrentFragment = fragment;
        transaction.commitNowAllowingStateLoss();
    }

    /**
     * 通过反射构建当前 fragment
     *
     * @param position 根据 HiTabBottomInfo 关联的 fragment 反射构建
     * @return fragment
     */
    private Fragment createFragmentByPos(int position) {
        try {
            return mInfoList.get(position).fragment.newInstance();
        } catch (Exception e) {
            HiLog.e("createFragmentByPos error = " + e.getMessage());
        }
        return null;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public int getCount() {
        return mInfoList == null ? 0 : mInfoList.size();
    }

    /**
     * 根据 view 的 id 和当前position 构建 fragment tag
     *
     * @param view     container
     * @param position 在 container 中的位置
     * @return tagName
     */
    public String getFragmentTagName(View view, int position) {
        return view.getId() + ":" + position;
    }
}
