package com.tsp.android.hiui.title

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.android.hilibrary.utils.HiRes
import com.tsp.android.hiui.R

/**
 *     author : shengping.tian
 *     time   : 2021/09/18
 *     desc   : 解析 navigationBar 属性
 *     version: 1.0
 */
internal object AttrsParse {
    /**
     * 解析 navigation 属性，优先xml 中配置的，然后再试主题中配置的，最后才是默认的 style
     * @param context Context
     * @param attrs AttributeSet?
     * @param defStyleAttr Int
     */
    fun parseNavAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int): Attrs {
        val value = TypedValue()
        context.theme.resolveAttribute(R.attr.navigationStyle, value, true)
        val defStyleRes = if (value.resourceId != 0) value.resourceId else R.style.navigationStyle
        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.NavigationBar,
            defStyleAttr,
            defStyleRes
        )
        val navIcon = array.getString(R.styleable.NavigationBar_nav_icon)
        val navIconColor = array.getColor(R.styleable.NavigationBar_nav_icon_color, Color.BLACK)
        val navIconSize = array.getDimensionPixelSize(
            R.styleable.NavigationBar_nav_icon_size,
            HiDisplayUtil.sp2px(18f)
        )
        val navTitle = array.getString(R.styleable.NavigationBar_nav_title)
        val navSubTitle = array.getString(R.styleable.NavigationBar_nav_subtitle)
        val horPadding = array.getDimensionPixelSize(R.styleable.NavigationBar_hor_padding, 0)
        val btnTextSize = array.getDimensionPixelSize(
            R.styleable.NavigationBar_text_btn_text_size,
            HiDisplayUtil.sp2px(16f)
        )
        val btnTextColor = array.getColorStateList(R.styleable.NavigationBar_text_btn_text_color)

        val titleTextSize = array.getDimensionPixelSize(
            R.styleable.NavigationBar_title_text_size,
            HiDisplayUtil.sp2px(17f)
        )
        val titleTextSizeWithSubtitle = array.getDimensionPixelSize(
            R.styleable.NavigationBar_title_text_size_with_subTitle,
            HiDisplayUtil.sp2px(16f)
        )
        val titleTextColor = array.getColor(
            R.styleable.NavigationBar_title_text_color,
            HiRes.getColor(R.color.color_000)
        )

        val subTitleTextSize = array.getDimensionPixelSize(
            R.styleable.NavigationBar_subTitle_text_size,
            HiDisplayUtil.sp2px(14f)
        )
        val subTitleTextColor = array.getColor(
            R.styleable.NavigationBar_subTitle_text_color,
            HiRes.getColor(R.color.color_000)
        )

        val lineColor =
            array.getColor(R.styleable.NavigationBar_nav_line_color, Color.parseColor("#eeeeee"))
        val lineHeight =
            array.getDimensionPixelOffset(R.styleable.NavigationBar_nav_line_height, 0)

        array.recycle()
        return Attrs(
            navIcon,
            navIconColor,
            navIconSize.toFloat(),
            navTitle,
            navSubTitle,
            horPadding,
            btnTextSize.toFloat(),
            btnTextColor,
            titleTextSize.toFloat(),
            titleTextSizeWithSubtitle.toFloat(),
            titleTextColor,
            subTitleTextSize.toFloat(),
            subTitleTextColor,
            lineColor,
            lineHeight
        )
    }

    data class Attrs(
        val navIconStr: String?,
        val navIconColor: Int,
        val navIconSize: Float,
        val navTitle: String?,
        val navSubtitle: String?,
        val horPadding: Int,
        val btnTextSize: Float,
        val btnTextColor: ColorStateList?,
        val titleTextSize: Float,
        val titleTextSizeWithSubTitle: Float,
        val titleTextColor: Int,
        val subTitleSize: Float,
        val subTitleTextColor: Int,
        val lineColor: Int,
        val lineHeight: Int
    )
}