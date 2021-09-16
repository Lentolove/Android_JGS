package com.tsp.android.hilibrary.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 *     author : shengping.tian
 *     time   : 2021/09/13
 *     desc   : 沉浸式状态栏设置
 *     version: 1.0
 */
object HiStatusBar {

    /**
     *
     * @param activity Activity
     * @param darkContent Boolean  白底黑字， false:黑底白字
     * @param statusBarColor Int   状态栏的背景色
     * @param translucent Boolean  沉浸式效果，也就是页面的布局延伸到状态栏之下
     */
    fun setStatusBar(
        activity: Activity,
        darkContent: Boolean,
        statusBarColor: Int = Color.WHITE,
        translucent: Boolean
    ) {
        val window = activity.window
        val decorView = window.decorView
        var visibility = decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //请求系统 绘制状态栏的背景色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //这俩不能同时出现
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = statusBarColor
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visibility = if (darkContent) {
                //白底黑字--浅色主题
                visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                //黑底白字--深色主题
                // java  visibility &= ~ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
        if (translucent) {
            //此时 能够使得页面的布局延伸到状态栏之下，但是状图兰的图标 也看不见了-,使得状图兰的图标 恢复可见性
            visibility = visibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        decorView.systemUiVisibility = visibility
    }
}