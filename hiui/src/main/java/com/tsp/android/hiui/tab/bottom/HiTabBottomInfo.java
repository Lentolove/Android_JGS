package com.tsp.android.hiui.tab.bottom;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * author : shengping.tian
 * time   : 2021/07/26
 * desc   : 底部导航 item 实体
 * version: 1.0
 */
public class HiTabBottomInfo<Color> {

    /**
     * 底部 tab 的类型，图片行，icon 文字型
     */
    public enum TabType {
        BITMAP, ICON
    }

    //当前 tabInfo 实例所持有的 fragment 的 class
    public Class<? extends Fragment> fragment;
    public String name;
    public Bitmap defaultBitmap;//未选中显示的bitmap
    public Bitmap selectedBitmap;//选中后显示的bitmap
    public String iconFont;//icon图标
    /**
     * Tips：在Java代码中直接设置iconfont字符串无效，需要定义在string.xml
     */
    public String defaultIconName;//默认显示的icon样式
    public String selectedIconName;//选中的icon样式
    public Color defaultColor;//默认显示的颜色
    public Color tintColor;//选中显示的颜色
    public TabType tabType;//当前类型

    public HiTabBottomInfo(String name, Bitmap defaultBitmap, Bitmap selectedBitmap) {
        this.name = name;
        this.defaultBitmap = defaultBitmap;
        this.selectedBitmap = selectedBitmap;
        this.tabType = TabType.BITMAP;
    }

    public HiTabBottomInfo(String name, String iconFont, String defaultIconName, String selectedIconName, Color defaultColor, Color tintColor) {
        this.name = name;
        this.iconFont = iconFont;
        this.defaultIconName = defaultIconName;
        this.selectedIconName = selectedIconName;
        this.defaultColor = defaultColor;
        this.tintColor = tintColor;
        this.tabType = TabType.ICON;
    }
}
