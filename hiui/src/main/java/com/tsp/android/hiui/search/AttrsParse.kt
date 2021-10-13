package com.tsp.android.hiui.search

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.android.hilibrary.utils.HiRes
import com.tsp.android.hiui.R

/**
 *     author : shengping.tian
 *     time   : 2021/09/17
 *     desc   : HiSearchView 样式解析
 *     version: 1.0
 */
internal object AttrsParse {

    /**
     * 常规套路，解析 HiSearchView 中的属性
     * @param context Context
     * @param attrs AttributeSet?
     * @param defStyleAttr Int
     */
    fun parseSearchViewAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int): Attrs {
        //1.注意可以在 app 的 theme 中配置默认的 searchStyle 属性，
        val value = TypedValue()
        //2.检查 theme 中是否为 HiSearchView 设置了默认的样式
        context.theme.resolveAttribute(R.attr.hiSearchViewStyle, value, true)
        //3.优先选择 theme 中配置的 HiSearchView 的样式，如果未配置，则选择默认的 searchViewStyle
        val defStyleRes = if (value.resourceId != 0) value.resourceId else R.style.searchViewStyle

        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.HiSearchView,
            defStyleAttr,
            defStyleRes
        )

        //搜索背景
        val searchBackground = array.getDrawable(R.styleable.HiSearchView_search_background)
            ?: HiRes.getDrawable(R.drawable.shape_search_view)

        //搜索图标
        val searchIcon = array.getString(R.styleable.HiSearchView_search_icon)
        val searchIconSize = array.getDimensionPixelSize(
            R.styleable.HiSearchView_search_icon_size,
            HiDisplayUtil.sp2px(16f)
        )
        val iconPadding = array.getDimensionPixelOffset(
            R.styleable.HiSearchView_icon_padding,
            HiDisplayUtil.sp2px(4f)
        )
        //清除按钮
        val clearIcon = array.getString(R.styleable.HiSearchView_clear_icon)
        val clearIconSize = array.getDimensionPixelSize(
            R.styleable.HiSearchView_clear_icon_size,
            HiDisplayUtil.sp2px(16f)
        )

        //提示语
        val hintText = array.getString(R.styleable.HiSearchView_hint_text)
        val hintTextSize = array.getDimensionPixelSize(
            R.styleable.HiSearchView_hint_text_size,
            HiDisplayUtil.sp2px(16f)
        )
        val hintTextColor = array.getColor(
            R.styleable.HiSearchView_hint_text_color,
            HiRes.getColor(R.color.color_000)
        )
        //相对位置
        val gravity = array.getInteger(R.styleable.HiSearchView_hint_gravity, 1)

        //输入文本
        val searchTextSize = array.getDimensionPixelSize(
            R.styleable.HiSearchView_search_text_size,
            HiDisplayUtil.sp2px(16f)
        )
        val searchTextColor = array.getColor(
            R.styleable.HiSearchView_search_text_color,
            HiRes.getColor(R.color.color_000)
        )

        //keyword关键词
        val keywordSize = array.getDimensionPixelSize(
            R.styleable.HiSearchView_key_word_size,
            HiDisplayUtil.sp2px(13f)
        )
        val keywordColor = array.getColor(R.styleable.HiSearchView_key_word_color, Color.WHITE)
        val keywordMaxLen = array.getInteger(R.styleable.HiSearchView_key_word_max_length, 10)
        val keywordBackground = array.getDrawable(R.styleable.HiSearchView_key_word_background)

        //关键词清除图标
        val keywordClearIcon = array.getString(R.styleable.HiSearchView_clear_icon)
        val keywordClearIconSize = array.getDimensionPixelSize(
            R.styleable.HiSearchView_clear_icon_size,
            HiDisplayUtil.sp2px(12f)
        )

        array.recycle()

        return Attrs(
            searchBackground,
            searchIcon,
            searchIconSize.toFloat(),
            iconPadding,
            clearIcon,
            clearIconSize.toFloat(),
            hintText,
            hintTextSize.toFloat(),
            hintTextColor,
            gravity,
            searchTextSize.toFloat(),
            searchTextColor,
            keywordSize.toFloat(),
            keywordColor,
            keywordMaxLen,
            keywordBackground,
            keywordClearIcon,
            keywordClearIconSize.toFloat()
        )
    }

    data class Attrs(
        /*search view background*/
        val searchBackground: Drawable?,
        /*search icon 🔍*/
        val searchIcon: String?,
        val searchIconSize: Float,
        val iconPadding: Int,
        val clearIcon: String?,
        val clearIconSize: Float,
        val hintText: String?,
        val hintTextSize: Float,
        val hintTextColor: Int,
        val gravity: Int,
        val searchTextSize: Float,
        val searchTextColor: Int,
        val keywordSize: Float,
        val keywordColor: Int,
        val keywordMaxLen: Int,
        val keywordBackground: Drawable?,
        val keywordClearIcon: String?,
        val keywordClearIconSize: Float
    )


}