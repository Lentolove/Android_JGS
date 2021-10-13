package com.tsp.android.detail

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.hilibrary.utils.ColorUtil
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import kotlin.math.abs
import kotlin.math.min

/**
 *     author : shengping.tian
 *     time   : 2021/09/22
 *     desc   : 定义顶部详情页 NavigationBar 的渐变效果
 *     version: 1.0
 */
class TitleScrollListener(thresholdDp: Float = 100f, val callback: (Int) -> Unit) :
    RecyclerView.OnScrollListener() {

    //根据滑动的距离计算标题栏渐变透明的效果
    private val thresholdPx = HiDisplayUtil.dp2px(thresholdDp)
    private var lastFraction = 0f

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        //根据当前 RecyclerView 滑动的距离，与我们传递的 thresholdPx 做运算，计算滑动比例系数，在根据系数计算由白色到透明的渐变效果
        val viewHolder = recyclerView.findViewHolderForAdapterPosition(0) ?: return
        val top = abs(viewHolder.itemView.top).toFloat()
        //计算当前滑动的半分比系数
        val fraction = top / thresholdPx
        if (lastFraction > 1f) {
            lastFraction = fraction
            return
        }
        val newColor = ColorUtil.getCurrentColor(Color.TRANSPARENT, Color.WHITE, min(fraction, 1f))
        callback(newColor)
        lastFraction = fraction
    }
}