package com.tsp.learn

import android.app.Activity
import android.content.Context

/**
 * @author tsp
 * Date: 2023/11/17
 * desc:
 */
object ScreenUtils {


    fun getScreenWidth(activity: Context) : Int{
        val metrics = activity.resources.displayMetrics
        return metrics.widthPixels
    }

    fun getScreenHeight(activity: Context) : Int{
        val metrics = activity.resources.displayMetrics
        return metrics.heightPixels
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }


}