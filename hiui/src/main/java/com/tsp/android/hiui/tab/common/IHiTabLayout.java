package com.tsp.android.hiui.tab.common;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * author : shengping.tian
 * time   : 2021/07/26
 * desc   :  tabLayout上层接口
 * version: 1.0
 */
public interface IHiTabLayout<Tab extends ViewGroup, D> {

    /**
     * 根据数据类型查找对应的 Tab 布局
     */
    Tab findTab(@NonNull D data);

    /**
     * 添加监听器
     */
    void addTabSelectedChangeListener(OnTabSelectedListener<D> listener);

    /**
     * 默认选中的 item
     *
     * @param defaultInfo 默认选种
     */
    void defaultSelected(@NonNull D defaultInfo);

    /**
     * 根据数据加载布局
     *
     * @param infoList 传入的底部 item 布局数据
     */
    void inflateInfo(@NonNull List<D> infoList);

    /**
     * 选中底部状态切换的监听接口
     */
    interface OnTabSelectedListener<D> {
        /**
         * 导航栏切换监听器
         *
         * @param index    当前选中的 index 索引
         * @param prevInfo 前一个 tabItem 布局
         * @param nextInfo 下一个 tabItem 布局
         */
        void onTabSelectedChange(int index, @Nullable D prevInfo, @NonNull D nextInfo);
    }

}
