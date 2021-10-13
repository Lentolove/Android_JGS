package com.tsp.android.hiui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.Gravity.LEFT
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tsp.android.hiui.R

/**
 *     author : shengping.tian
 *     time   : 2021/07/05
 *     desc   : 账号密码自定义样式数据框
 *     version: 1.0
 */
class InputItemLayout : LinearLayout {

    //左边标题文本
    private lateinit var titleTextView: TextView

    //右边输入文本
    private lateinit var editTextView: EditText

    //底部线条
    private var bottomLine: Line

    //上方线条
    private var topLine: Line

    //上方线条画笔
    private val topPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    //底部线条画笔
    private val bottomPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {

        //1.LinearLayout 支持设置一个 drawable 用作 item 之间的分隔线
        dividerDrawable = ColorDrawable()
        //2.Set how dividers should be shown between items in this layout
        //            SHOW_DIVIDER_NONE,
        //            SHOW_DIVIDER_BEGINNING,
        //            SHOW_DIVIDER_MIDDLE,
        //            SHOW_DIVIDER_END
        showDividers = SHOW_DIVIDER_BEGINNING

        orientation = HORIZONTAL

        //3.加载自定义属性,获取完记得回收
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.InputItemLayout)

        //3.1 解析 title 属性
        val title = array.getString(R.styleable.InputItemLayout_title)
        val titleResId = array.getResourceId(R.styleable.InputItemLayout_titleTextAppearance, 0)
        parseTitleStyle(title, titleResId)

        //3.2 解析右侧的输入框
        val hint = array.getString(R.styleable.InputItemLayout_hint)
        val inputResId = array.getResourceId(R.styleable.InputItemLayout_inputTextAppearance, 0)
        val maxInputLength = array.getInteger(R.styleable.InputItemLayout_maxInputLength, 20)
        //输入类型，数字，密码，文本等。
        val inputType = array.getInteger(R.styleable.InputItemLayout_inputType, 0)
        parseInputStyle(hint, inputResId, inputType, maxInputLength)

        //3.3 解析上下分割线
        val topResId = array.getResourceId(R.styleable.InputItemLayout_topLineAppearance, 0)
        val bottomResId = array.getResourceId(R.styleable.InputItemLayout_bottomLineAppearance, 0)
        topLine = parseLineStyle(topResId)
        bottomLine = parseLineStyle(bottomResId)

        //上边线是否可见
        if (topLine.enable) {
            topPaint.color = topLine.color
            topPaint.style = Paint.Style.FILL_AND_STROKE
            topPaint.strokeWidth = topLine.height
        }
        if (bottomLine.enable) {
            bottomPaint.color = bottomLine.color
            bottomPaint.style = Paint.Style.FILL_AND_STROKE
            bottomPaint.strokeWidth = bottomLine.height
        }
        array.recycle()
    }

    fun getTileView(): TextView {
        return titleTextView
    }

    fun getEditText(): EditText {
        return editTextView
    }

    /**
     * 画 上边界线 和 下边界线
     */
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (topLine.enable) {
            canvas?.drawLine(
                topLine.leftMargin,
                0f,
                measuredWidth - topLine.rightMargin,
                0f,
                topPaint
            )
        }
        if (bottomLine.enable) {
            canvas?.drawLine(
                bottomLine.leftMargin,
                height - bottomLine.height,
                measuredWidth - bottomLine.rightMargin,
                height - bottomLine.height,
                bottomPaint
            )
        }
    }

    /**
     * 解析横线 style
     */
    @SuppressLint("CustomViewStyleable")
    private fun parseLineStyle(resId: Int): Line {
        val array = context.obtainStyledAttributes(resId, R.styleable.lineAppearance)
        val line = Line().apply {
            color = array.getColor(
                R.styleable.lineAppearance_color,
                resources.getColor(R.color.color_d1d2)
            )
            height = array.getDimensionPixelOffset(R.styleable.lineAppearance_height, 0).toFloat()
            leftMargin =
                array.getDimensionPixelOffset(R.styleable.lineAppearance_leftMargin, 0).toFloat()
            rightMargin =
                array.getDimensionPixelOffset(R.styleable.lineAppearance_rightMargin, 0).toFloat()
            enable = array.getBoolean(R.styleable.lineAppearance_enable, false)
        }
        array.recycle()
        return line
    }

    /**
     * 解析右侧 输入文本
     * @hint 提示文本
     * @inputResId 文本样式
     * @inputType 文本输入类型 字符型，数字型，密码型
     */
    @SuppressLint("CustomViewStyleable")
    private fun parseInputStyle(hintStr: String?, inputResId: Int, type: Int, maxInputLength: Int) {
        val array = context.obtainStyledAttributes(inputResId, R.styleable.inputTextAppearance)
        val hintColor = array.getColor(
            R.styleable.inputTextAppearance_hintColor,
            ContextCompat.getColor(context, R.color.color_d1d2)
        )
        val inputColor = array.getColor(
            R.styleable.inputTextAppearance_inputColor,
            ContextCompat.getColor(context, R.color.color_565)
        )
        //px
        val textSize = array.getDimensionPixelSize(
            R.styleable.inputTextAppearance_textSize,
            applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
        )
        editTextView = EditText(context).apply {
            //最多可输入的字符数
            filters = arrayOf(InputFilter.LengthFilter(maxInputLength))
            setPadding(0, 0, 0, 0)
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            params.weight = 1f//设置 editText 的权重
            layoutParams = params
            hint = hintStr
            setTextColor(inputColor)
            setHintTextColor(hintColor)
            gravity = LEFT or CENTER
            setBackgroundColor(Color.TRANSPARENT)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            when (type) {
                0 -> {
                    inputType = InputType.TYPE_CLASS_TEXT
                }
                1 -> {
                    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                }
                2 -> {
                    inputType = InputType.TYPE_CLASS_NUMBER
                }
            }
        }
        addView(editTextView)
        array.recycle()
    }

    /**
     * 解析 title 属性
     */
    @SuppressLint("CustomViewStyleable")
    private fun parseTitleStyle(title: String?, titleResId: Int) {
        val array = context.obtainStyledAttributes(titleResId, R.styleable.titleTextAppearance)
        val titleColor = array.getColor(
            R.styleable.titleTextAppearance_titleColor,
            resources.getColor(R.color.color_565)
        )
        //转换 sp to px
        val titleSize = array.getDimensionPixelSize(
            R.styleable.titleTextAppearance_titleSize,
            applyUnit(TypedValue.COMPLEX_UNIT_SP, 15f)
        )
        val minWidth = array.getDimensionPixelOffset(R.styleable.titleTextAppearance_minWidth, 0)

        //创建左侧的 title 的 TextView
        titleTextView = TextView(context).apply {
            text = title
            setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat()) //sp---当做sp在转换一次
            setTextColor(titleColor)
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setMinWidth(minWidth)
            gravity = LEFT or CENTER
            //添加到布局中
        }
        addView(titleTextView)
        array.recycle()
    }

    /**
     * 转换像素
     */
    private fun applyUnit(applyUnit: Int, value: Float): Int {
        return TypedValue.applyDimension(applyUnit, value, resources.displayMetrics).toInt()
    }

    /**
     * 分割线样式封装类
     */
    inner class Line {
        var color = 0
        var height = 0f
        var leftMargin = 0f
        var rightMargin = 0f
        var enable = false
    }
}