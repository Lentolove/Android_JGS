package com.tsp.android.hiui.search

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.tsp.android.hilibrary.utils.MainHandler
import com.tsp.android.hiui.R
import com.tsp.android.hiui.view.IconFontTextView

/**
 *     author : shengping.tian
 *     time   : 2021/09/17
 *     desc   : 搜索控件
 *     version: 1.0
 */
class HiSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        //搜索框居中
        const val CENTER = 0

        //搜索框靠左
        const val LEFT = 1

        //联想词请求输入限流，每 200ms 做一次联想词请求
        const val DEBOUNCE_TRIGGER_DURATION = 200L
    }

    private var simpleTextWatcher: SimpleTextWatcher? = null

    lateinit var editText: EditText

    private var searchIcon: IconFontTextView? = null
    private var hintTextView: TextView? = null
    private var searchIconHintContainer: LinearLayout? = null
    private var clearIcon: IconFontTextView? = null

    //关键词控件
    private var keywordContainer: LinearLayout? = null
    private var keywordTv: TextView? = null
    private var kwClearIcon: IconFontTextView? = null

    private val viewAttrs = AttrsParse.parseSearchViewAttrs(context, attrs, defStyleAttr)

    init {
        //初始化 editText
        initEditText()
        //初始化右侧一键清楚地小按钮
        initClearIcon()
        //初始化默认的提示语
        initSearchIconHintContainer()
        //背景色
        background = viewAttrs.searchBackground

        editText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                //监听 editText 输入事件

                //是否有输入
                val hasContent = s?.trim()?.length ?: 0 > 0
                //有内容显示右侧清楚小图标
                changeVisibility(clearIcon, hasContent)
                //有内容隐藏提示语
                changeVisibility(searchIconHintContainer, !hasContent)
                if (simpleTextWatcher != null) {
                    MainHandler.remove(debounceRunnable)
                    // 200ms 内的的变化过滤掉
                    MainHandler.postDelay(DEBOUNCE_TRIGGER_DURATION, debounceRunnable)
                }
            }
        })
    }

    fun setDebounceTextChangeListener(simpleTextWatcher: SimpleTextWatcher) {
        this.simpleTextWatcher = simpleTextWatcher
    }

    /**
     * 设置提示语
     * @param hintText String
     */
    fun setHintText(hintText: String) {
        hintTextView?.text = hintText
    }

    /**
     * 设置联想词，用户点击 联想词面板的时候，会调用该方法，把关键词设置到搜索框上面
     * @param keyword String
     * @param listener OnClickListener
     */
    fun setKeyWord(keyword: String, listener: OnClickListener) {
        //1.检查 KeywordContainer 容器是否初始化
        ensureKeywordContainer()
        //2.更新 searChView 的显隐藏
        toggleSearchViewsVisibility(true)
        editText.text = null
        keywordTv?.text = keyword
        //keyword的清除按钮
        kwClearIcon?.setOnClickListener {
            //点击了keyword  的 clearIcon ,此时应该恢复默认提示语views显示
            toggleSearchViewsVisibility(false)
            listener.onClick(it)
        }
    }

    /**
     * 设置右侧清楚按钮的点击事件，当点击清楚时候，需要向外部传递事件，更新searchView的状态显示
     * @param listener OnClickListener
     */
    fun setClearIconClickListener(listener: OnClickListener) {
        clearIcon?.setOnClickListener {
            editText.text = null
            changeVisibility(clearIcon, false)
            changeVisibility(searchIcon, true)
            changeVisibility(hintTextView, true)
            //keyword容器
            changeVisibility(searchIconHintContainer, true)
            listener.onClick(it)
        }
    }

    /**
     * 获取当前的 keyword
     * @return String
     */
    fun getKeyWord(): String {
        return keywordTv?.text.toString()
    }

    /**
     * editText 输入请求限流
     */
    private val debounceRunnable = Runnable {
        if (simpleTextWatcher != null) {
            simpleTextWatcher!!.afterTextChanged(editText.text)
        }
    }

    /**
     * 切换各个View的显隐藏
     * @param showWork Boolean
     */
    private fun toggleSearchViewsVisibility(showWork: Boolean) {
        changeVisibility(editText, !showWork)
        changeVisibility(clearIcon, false)
        changeVisibility(searchIconHintContainer, !showWork)
        changeVisibility(searchIcon, !showWork)
        changeVisibility(hintTextView, !showWork)
        changeVisibility(keywordContainer, showWork)
    }

    /**
     * 确保 keyWordContainer 是否已经初始化，该布局是一个 LinearLayout 包裹的布局，内部是一个 textView 和一个清楚小图标
     */
    private fun ensureKeywordContainer() {
        if (keywordContainer != null) return
        if (!viewAttrs.keywordClearIcon.isNullOrEmpty()) {
            kwClearIcon = IconFontTextView(context, null)
            kwClearIcon!!.apply {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.keywordSize)
                setTextColor(viewAttrs.keywordColor)
                text = viewAttrs.keywordClearIcon
                id = R.id.id_search_keyword_clear_icon
                setPadding(
                    viewAttrs.iconPadding,
                    viewAttrs.iconPadding / 2,
                    viewAttrs.iconPadding,
                    viewAttrs.iconPadding / 2
                )
            }
        }
        keywordTv = TextView(context)
        keywordTv!!.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.keywordSize)
            setTextColor(viewAttrs.keywordColor)
            //material 样式默认为true，可能导致文字无法显示问题
            includeFontPadding = false
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            filters = arrayOf(InputFilter.LengthFilter(viewAttrs.keywordMaxLen))
            id = R.id.id_search_keyword_text_view
            setPadding(
                viewAttrs.iconPadding,
                viewAttrs.iconPadding / 2,
                //右侧是否设置padding 有右侧的 kwClearIcon 是否存在来决定,默认 kwClearIcon 也存在padding
                if (kwClearIcon == null) viewAttrs.iconPadding else 0,
                viewAttrs.iconPadding / 2
            )
        }

        keywordContainer = LinearLayout(context)
        keywordContainer!!.apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = viewAttrs.keywordBackground
            addView(
                keywordTv,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            if (kwClearIcon != null) {
                addView(
                    kwClearIcon,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )
            }
        }
        val kwParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        kwParams.addRule(CENTER_VERTICAL)
        kwParams.addRule(ALIGN_PARENT_LEFT)
        kwParams.leftMargin = viewAttrs.iconPadding
        kwParams.rightMargin = viewAttrs.iconPadding
        addView(keywordContainer, kwParams)
    }


    /**
     * 初始化文本输入控件
     */
    private fun initEditText() {
        editText = EditText(context).apply {
            setTextColor(viewAttrs.searchTextColor)
            setBackgroundColor(Color.TRANSPARENT)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.searchTextSize)
            //设置左右边距，防止属于的文字过于贴近输入框的两边
            setPadding(viewAttrs.iconPadding, 0, viewAttrs.iconPadding, 0)
            //设置id
            id = R.id.id_search_hint_view
        }
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.addRule(CENTER_VERTICAL)
        addView(editText,params)
    }

    /**
     * 初始化右侧小图标
     */
    private fun initClearIcon() {
        if (TextUtils.isEmpty(viewAttrs.clearIcon)) return
        clearIcon = IconFontTextView(context, null)
        clearIcon!!.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.clearIconSize)
            text = viewAttrs.clearIcon
            setTextColor(viewAttrs.searchTextColor)
            setPadding(viewAttrs.iconPadding)
            id = R.id.id_search_clear_icon
        }
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_VERTICAL)
        params.addRule(ALIGN_PARENT_RIGHT)
        clearIcon!!.layoutParams = params
        //默认隐藏，只有当输入文字才会显示
        changeVisibility(clearIcon, false)
        addView(clearIcon, params)
    }


    /**
     * 显示和隐藏
     * @param clearIcon View
     * @param show Boolean
     */
    private fun changeVisibility(view: View?, show: Boolean) {
        view?.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * 初始化默认的提示语 和 searchIcon
     */
    private fun initSearchIconHintContainer() {
        hintTextView = TextView(context)
        hintTextView!!.apply {
            setTextColor(viewAttrs.hintTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.hintTextSize)
            isSingleLine = true
            text = viewAttrs.hintText
            id = R.id.id_search_hint_view
        }
        searchIcon = IconFontTextView(context, null)
        searchIcon!!.apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, viewAttrs.searchIconSize)
            setTextColor(viewAttrs.hintTextColor)
            text = viewAttrs.searchIcon
            id = R.id.id_search_icon
            setPadding(viewAttrs.iconPadding, 0, viewAttrs.iconPadding / 2, 0)
        }
        searchIconHintContainer = LinearLayout(context)
        searchIconHintContainer!!.apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(searchIcon)
            addView(hintTextView)
        }
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.addRule(CENTER_VERTICAL)
        when (viewAttrs.gravity) {
            CENTER -> params.addRule(CENTER_IN_PARENT)
            LEFT -> params.addRule(ALIGN_PARENT_RIGHT)
            else -> throw IllegalStateException("not support gravity for now.")
        }
        addView(searchIconHintContainer, params)
    }

    /**
     * 在控件移除的时候移除消息
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        MainHandler.remove(debounceRunnable)
    }
}