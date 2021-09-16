package com.tsp.android.hiui.slider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.android.hilibrary.utils.HiRes
import com.tsp.android.hiui.R

/**
 *     author : shengping.tian
 *     time   : 2021/09/10
 *     desc   : Slider 样式资源解析器
 *     version: 1.0
 */
internal object SliderAttrsParse {

    //提供一些默认属性
    private val MENU_WIDTH = HiDisplayUtil.dp2px(100f)
    private val MENU_HEIGHT = HiDisplayUtil.dp2px(45f)
    private val MENU_TEXT_SIZE = HiDisplayUtil.sp2px(14f)

    private val TEXT_COLOR_NORMAL = HiRes.getColor(R.color.color_666)//Color.parseColor("#666666")
    private val TEXT_COLOR_SELECT = HiRes.getColor(R.color.color_127)//Color.parseColor("#DD3127")

    private val BG_COLOR_NORMAL = HiRes.getColor(R.color.color_8f9)//Color.parseColor("#F7F8F9")
    private val BG_COLOR_SELECT = HiRes.getColor(R.color.color_white)//Color.parseColor("#ffffff")


    fun parseMenuItemAttr(context: Context, attrs: AttributeSet?): MenuItemAttr {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.HiSliderView)
        //左侧菜单 item 的宽度
        val menuItemWidth = typeArray.getDimensionPixelOffset(
            R.styleable.HiSliderView_menuItemWidth,
            MENU_WIDTH
        )
        //左侧菜单 item 的高度
        val menuItemHeight =
            typeArray.getDimensionPixelOffset(R.styleable.HiSliderView_menuItemHeight, MENU_HEIGHT)

        //左侧菜单 item 的文字大小
        val menuItemTextSize = typeArray.getDimensionPixelOffset(
            R.styleable.HiSliderView_menuItemTextSize,
            MENU_TEXT_SIZE
        )

        //左侧菜单 item 被选中时的文字大小
        val menuItemSelectTextSize = typeArray.getDimensionPixelOffset(
            R.styleable.HiSliderView_menuItemSelectTextSize,
            MENU_TEXT_SIZE
        )
        //左侧菜单 item 的文字的颜色
        val menuItemTextColor =
            typeArray.getColorStateList(R.styleable.HiSliderView_menuItemTextColor)
                ?: generateColorStateList()

        //左侧菜单 item 的指示器
        val menuItemIndicator = typeArray.getDrawable(R.styleable.HiSliderView_menuItemIndicator)
            ?: ContextCompat.getDrawable(context, R.drawable.shape_hi_slider_indicator)


        val menuItemBackgroundColor =
            typeArray.getColor(R.styleable.HiSliderView_menuItemBackGroundColor, BG_COLOR_NORMAL)

        val menuItemBackgroundSelectColor = typeArray.getColor(
            R.styleable.HiSliderView_menuItemSelectBackGroundColor,
            BG_COLOR_SELECT
        )
        typeArray.recycle()
        //将所有的参数封装成数据类，方便传递
        return MenuItemAttr(
            menuItemWidth,
            menuItemHeight,
            menuItemTextColor,
            menuItemBackgroundSelectColor,
            menuItemBackgroundColor,
            menuItemTextSize,
            menuItemSelectTextSize,
            menuItemIndicator
        )
    }

    data class MenuItemAttr(
        val width: Int,
        val height: Int,
        val textColor: ColorStateList,
        val selectBackgroundColor: Int,
        val normalBackgroundColor: Int,
        val textSize: Int,
        val selectTextSize: Int,
        val indicator: Drawable?
    )

    /**
     * 构建Selector选择器，相当于在 drawable 中设置的背景选择器
     */
    private fun generateColorStateList(): ColorStateList {
        val states = Array(2) { IntArray(2) }
        val colors = IntArray(2)
        //被选中时候的颜色
        colors[0] = TEXT_COLOR_SELECT
        //未被选中时的颜色
        colors[1] = TEXT_COLOR_NORMAL
        //被选择的状态
        states[0] = IntArray(1) { android.R.attr.state_selected }
        //其他
        states[1] = IntArray(1)
        return ColorStateList(states, colors)
    }

}