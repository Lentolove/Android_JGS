package com.tsp.android.hiui.tab.common;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

/**
 * author : shengping.tian
 * time   : 2021/07/26
 * desc   : HiTab对外接口
 * version: 1.0
 */
public interface IHiTab<D> extends IHiTabLayout.OnTabSelectedListener<D> {

    void setHiTabInfo(@NonNull D data);

    /**
     * 动态修改某个 item 的高度
     *
     * @param height px 高度值，让某个item凸起等
     */
    void resetHeight(@Px int height);
}
