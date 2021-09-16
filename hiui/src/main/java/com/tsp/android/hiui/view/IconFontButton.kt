package com.tsp.android.hiui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/**
 *     author : shengping.tian
 *     time   : 2021/07/30
 *     desc   : iconFont 形式的 button
 *     version: 1.0
 */
class IconFontButton @JvmOverloads constructor(
        context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attributes, defStyleAttr) {
    init {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/iconfont.ttf");
        setTypeface(typeface)
    }
}