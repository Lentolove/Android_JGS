package com.tsp.android.hiui.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.tsp.android.hiui.R

/**
 *     author : shengping.tian
 *     time   : 2021/07/30
 *     desc   : 数据异常控件展示
 *     version: 1.0
 */
class EmptyView : LinearLayout {

    private var title: TextView
    private var icon: TextView
    private var desc: TextView
    private var button: Button

    private var emptyTipView: IconFontTextView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_empty_view, this, false)
        icon = rootView.findViewById(R.id.empty_icon)
        title = rootView.findViewById(R.id.empty_title)
        desc = rootView.findViewById(R.id.empty_text)
        button = rootView.findViewById(R.id.empty_action)
        emptyTipView = rootView.findViewById(R.id.empty_tips)
    }


    /**
     * 设置icon，需要在string.xml中定义 iconfont.ttf中的unicode码
     */
    fun setIcon(@StringRes iconRes: Int) {
        icon.setText(iconRes)
    }

    fun setTitle(text: String) {
        title.text = text
        title.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
    }

    fun setDesc(text: String) {
        desc.text = text
        desc.visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
    }

    @JvmOverloads
    fun setHelpAction(@StringRes actionId: Int = R.string.if_detail, listener: OnClickListener) {
        emptyTipView.setText(actionId)
        emptyTipView.setOnClickListener(listener)
        emptyTipView.visibility = if (actionId == -1) View.GONE else View.VISIBLE
    }

    fun setButton(text: String, listener: OnClickListener) {
        if (TextUtils.isEmpty(text)) {
            button.visibility = View.GONE
        } else {
            button.visibility = View.VISIBLE
            button.text = text
            button.setOnClickListener(listener)
        }
    }
}