package com.tsp.test.slider

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.hilibrary.utils.HiDisplayUtil

/**
 *     author : shengping.tian
 *     time   : 2021/09/10
 *     desc   : contentView 的 ItemDecoration
 *     version: 1.0
 */
class CategoryItemDecoration(
    val callback: (Int) -> String,
    private val spanCount: Int
) : RecyclerView.ItemDecoration() {
    private val groupFirstPositions = mutableMapOf<String, Int>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.isFakeBoldText = true
        paint.textSize = HiDisplayUtil.dp2px(15f).toFloat()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        //1. 根据 view对象，找到他在列表中处于的位置 adapterPosition
        val adapterPosition = parent.getChildAdapterPosition(view)
        if (adapterPosition >= parent.adapter!!.itemCount || adapterPosition < 0) return

        //2.拿到当前位置 adapterPosition 对应的 groupName
        val groupName = callback(adapterPosition)
        //3.拿到前面一个位置的 groupName
        val preGroupName = if (adapterPosition > 0) callback(adapterPosition - 1) else null

        val sameGroup = TextUtils.equals(groupName, preGroupName)

        if (!sameGroup && !groupFirstPositions.containsKey(groupName)) {
            //就说明当前位置 adapterPosition 对应的 item 是当前组的第一个位置。
            //此时存储起来，记录下来，目的是为了方便后面计算，计算后面 item 是否是第一行
            groupFirstPositions[groupName] = adapterPosition
        }

        val firstRowPosition = groupFirstPositions[groupName] ?: 0
        val samRow = adapterPosition - firstRowPosition in 0 until spanCount  //3

        //不是同一组，或者是同一行，就需要给 ItemDecoration 设置一个高度
        if (!sameGroup || samRow) {
            outRect.set(0, HiDisplayUtil.dp2px(40f), 0, 0)
            return
        }
        outRect.set(0, 0, 0, 0)
    }

    /**
     * 在提供给 RecyclerView 的 Canvas 中绘制任何适当的装饰。
     * 使用此方法绘制的任何内容都将在绘制项目视图之后绘制，因此会出现在视图之上。
     * @param c Canvas
     * @param parent RecyclerView
     * @param state State
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        for (index in 0 until childCount) {
            val view = parent.getChildAt(index)
            val adapterPosition = parent.getChildAdapterPosition(view)
            if (adapterPosition >= parent.adapter!!.itemCount || adapterPosition < 0) continue
            val groupName = callback(adapterPosition)
            //判断当前位置 是不是分组的第一个位置
            //如果是，在他的位置上绘制标题
            val groupFirstPosition = groupFirstPositions[groupName]
            if (groupFirstPosition == adapterPosition) {
                val decorationBounds = Rect()
                //为了拿到当前item 的 左上右下的坐标信息 包含了 margin 和 padding 空间的
                parent.getDecoratedBoundsWithMargins(view, decorationBounds)
                val textBounds = Rect()
                paint.getTextBounds(groupName, 0, groupName.length, textBounds)
                //将 GroupName 文字画上去
                c.drawText(
                    groupName,
                    HiDisplayUtil.dp2px(16f).toFloat(),
                    (decorationBounds.top + 2 * textBounds.height()).toFloat(),
                    paint
                )
            }
        }
    }

    fun clear() {
        groupFirstPositions.clear()
    }
}