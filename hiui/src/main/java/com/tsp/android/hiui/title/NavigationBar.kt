package com.tsp.android.hiui.title

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import com.tsp.android.hilibrary.utils.HiRes
import com.tsp.android.hiui.R
import com.tsp.android.hiui.view.IconFontButton
import com.tsp.android.hiui.view.IconFontTextView
import java.lang.IllegalStateException
import java.util.ArrayList

/**
 *     author : shengping.tian
 *     time   : 2021/09/18
 *     desc   : NavigationBar 控件
 *     version: 1.0
 */
class NavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyle: Int = 0
) : RelativeLayout(context, attrs, defaultStyle) {


    //主标题，出现副标题的时候标题样式上下排列，这个时候主标题的样式可能需要微调
    private var titleView: IconFontTextView? = null

    //副标题
    private var subTitleView: IconFontTextView? = null

    //标题容器父布局
    private var titleContainer: LinearLayout? = null

    /**
     * 记录左右按钮的 id,以动态方式添加左右按钮控件,记录最有一个添加的 view 的 id
     * 可能存在往 title 左边和右边添加多个控件的情况，用集合进行保存，同时记录最新添加的View的id,方便根据 id 设置相对位置
     */
    private var mLeftLastViewId = View.NO_ID
    private var mRightLastViewId = View.NO_ID
    private val mLeftViewList = ArrayList<View>()
    private val mRightViewList = ArrayList<View>()

    //属性解析获得对象
    private var navAttrs: AttrsParse.Attrs = AttrsParse.parseNavAttrs(context, attrs, defaultStyle)

    init {
        //先解析 title 标题
        if (!TextUtils.isEmpty(navAttrs.navTitle)) {
            setTitle(navAttrs.navTitle!!)
        }
        //解析是否配置了副标题
        if (!TextUtils.isEmpty(navAttrs.navSubtitle)) {
            setSubTitle(navAttrs.navSubtitle!!)
        }
        //如果设置了下划线
        if (navAttrs.lineHeight > 0) {
            addLineView()
        }
    }

    /**
     *  设置 Back 点击事件
     * @param listener OnClickListener
     */
    fun setNavBackListener(listener: OnClickListener) {
        if (!TextUtils.isEmpty(navAttrs.navIconStr)) {
            val navBackView = addLeftTextButton(navAttrs.navIconStr!!, R.id.id_nav_left_back_view)
            navBackView.setTextSize(TypedValue.COMPLEX_UNIT_PX, navAttrs.navIconSize)
            navBackView.setTextColor(navAttrs.navIconColor)
            navBackView.setOnClickListener(listener)
        }
    }

    /**
     * 向 navigationBar 的最左边添加按钮
     * @param buttonText 设置 button 的文字
     * @param viewId button id
     * @return Button
     */
    fun addLeftTextButton(buttonText: String, viewId: Int): Button {
        val button = generateTextButton().apply {
            text = buttonText
            id = viewId
            //根据添加的位置设置左右边界
            if (mLeftViewList.isEmpty()) {
                //第一个添加的,最左边设置两倍的间距
                setPadding(navAttrs.horPadding * 2, 0, navAttrs.horPadding, 0)
            } else {
                setPadding(navAttrs.horPadding, 0, navAttrs.horPadding, 0)
            }
        }
        //添加到 NavigationBar 的左边
        addLeftView(button, generateTextButtonLayoutParams())
        return button
    }

    fun addLeftTextButton(@StringRes stringRes: Int, viewId: Int): Button {
        return addLeftTextButton(HiRes.getString(stringRes), viewId)
    }

    /**
     * 将 view 添加到  NavigationBar 的左边
     * @param view View
     * @param params LayoutParams
     */
    fun addLeftView(view: View, params: LayoutParams) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw IllegalStateException("left view must has an unique id.")
        }
        //记录上次添加的 id ，以便于在磅摆放下一个控件的时候根据 id 设置位置
        if (mLeftLastViewId == View.NO_ID) {
            //添加的第一个left按钮
            params.addRule(ALIGN_PARENT_LEFT, viewId)
        } else {
            params.addRule(RIGHT_OF, mLeftLastViewId)
        }
        mLeftLastViewId = viewId
        params.alignWithParent = true  //alignParentIfMissing
        mLeftViewList.add(view)
        addView(view, params)
    }


    /**
     * 添加右边 button
     * @param buttonText String
     * @param viewId Int
     * @return Button
     */
    fun addRightTextButton(buttonText: String, viewId: Int): Button {
        val button = generateTextButton()
        button.text = buttonText
        button.id = viewId
        if (mRightViewList.isEmpty()) {
            button.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding * 2, 0)
        } else {
            button.setPadding(navAttrs.horPadding, 0, navAttrs.horPadding, 0)
        }
        addRightView(button, generateTextButtonLayoutParams())
        return button
    }

    fun addRightTextButton(@StringRes stringRes: Int, viewId: Int): Button {
        return addRightTextButton(HiRes.getString(stringRes), viewId)
    }


    /**
     * 将 view 添加到  NavigationBar 的右边
     * @param view View
     * @param params LayoutParams
     */
    fun addRightView(
        view: View,
        params: LayoutParams
    ) {
        val viewId = view.id
        if (viewId == View.NO_ID) {
            throw IllegalStateException("right view must has an unique id.")
        }
        if (mRightLastViewId == View.NO_ID) {
            params.addRule(ALIGN_PARENT_RIGHT, viewId)
        } else {
            params.addRule(LEFT_OF, mRightLastViewId)
        }
        mRightLastViewId = viewId
        params.alignWithParent = true  //alignParentIfMissing
        mRightViewList.add(view)
        addView(view, params)
    }

    /**
     * 设置标题
     * @param title String
     */
    fun setTitle(title: String) {
        ensureTitleView()
        titleView?.text = title
        titleView?.visibility = if (TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
    }

    /**
     * 设置副标题
     * @param subtitle
     */
    fun setSubTitle(subtitle: String) {
        ensureSubtitleView()
        updateTitleViewStyle()
        subTitleView?.text = subtitle
        subTitleView?.visibility = if (TextUtils.isEmpty(subtitle)) View.GONE else View.VISIBLE
    }


    /**
     * 向 NavigationBar 中间添加控件，适配一些中间不需要显示 title 而现实其他控件的场景
     * @param view View
     */
    fun setCenterView(view: View) {
        var params = view.layoutParams
        if (params == null) {
            params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        } else if (params !is LayoutParams) {
            params = LayoutParams(params)
        }
        val centerViewParams = params
        centerViewParams.addRule(RIGHT_OF, mLeftLastViewId)
        centerViewParams.addRule(LEFT_OF, mRightLastViewId)
        params.addRule(CENTER_VERTICAL)
        addView(view, centerViewParams)
    }

    /**
     * 生成 Button
     * @return Button
     */
    private fun generateTextButton(): Button {
        val button = IconFontButton(context)
        button.setBackgroundResource(0)
        button.minWidth = 0
        button.minimumWidth = 0
        button.minHeight = 0
        button.minHeight = 0
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, navAttrs.btnTextSize)
        button.setTextColor(navAttrs.btnTextColor)
        button.gravity = Gravity.CENTER
        //设置 TextView 是否包含额外的顶部和底部填充，默认值为 true
        button.includeFontPadding = false
        return button
    }

    /**
     * 添加下划线
     */
    private fun addLineView() {
        val view = View(context)
        val params = LayoutParams(LayoutParams.MATCH_PARENT, navAttrs.lineHeight)
        params.addRule(ALIGN_PARENT_BOTTOM)
        view.layoutParams = params
        view.setBackgroundColor(navAttrs.lineColor)
        addView(view)
    }

    /**
     * 设置 LayoutParams
     * @return LayoutParams
     */
    private fun generateTextButtonLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
    }

    /**
     * 实例化 subtitle
     */
    private fun ensureSubtitleView() {
        if (subTitleView == null) {
            subTitleView = IconFontTextView(context, null)
            subTitleView?.apply {
                gravity = Gravity.CENTER
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(navAttrs.subTitleTextColor)
                textSize = navAttrs.subTitleSize

                //添加到titleContainer
                ensureTitleContainer()
                titleContainer?.addView(subTitleView)
            }
        }
    }


    /**
     * 实例化 titleView
     */
    private fun ensureTitleView() {
        if (titleView == null) {
            titleView = IconFontTextView(context, null)
            titleView!!.apply {
                gravity = Gravity.CENTER
                isSingleLine = true
                //尾部截断
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(navAttrs.titleTextColor)
                //更新titleView
                updateTitleViewStyle()
                ensureTitleContainer()
                //添加到 titleContainer 中
                titleContainer?.addView(titleView, 0)
            }
        }
    }

    /**
     * 创建 Title 父容器
     */
    private fun ensureTitleContainer() {
        if (titleContainer == null) {
            titleContainer = LinearLayout(context)
            titleContainer!!.apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            params.addRule(CENTER_IN_PARENT)
            addView(titleContainer, params)
        }
    }

    /**
     * 更新 titleView 样式,样式会受 subTitle 是否存在而变化
     */
    private fun updateTitleViewStyle() {
        if (titleView != null) {
            if (subTitleView == null || TextUtils.isEmpty(subTitleView!!.text)) {
                //无副标题时候的 title size
                titleView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, navAttrs.titleTextSize)
                //设置粗体
                titleView!!.typeface = Typeface.DEFAULT_BOLD
            } else {
                //存在副标题
                titleView!!.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    navAttrs.titleTextSizeWithSubTitle
                )
                titleView!!.typeface = Typeface.DEFAULT
            }
        }
    }

    /**
     * 计算 titleContainer 控件剩余可以摆放的空间，需要去掉 左侧按钮 leftWidth 和 右侧按 rightWidth 钮所占用的空间，取最大值*2 就是 titleContainer 可用的剩余空间
     * @param widthMeasureSpec Int
     * @param heightMeasureSpec Int
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (titleContainer != null) {
            //计算标题栏左侧剩余的空间
            var leftUseSpace = paddingLeft
            for (view in mLeftViewList) {
                leftUseSpace += view.measuredWidth
            }
            var rightUseSpace = paddingRight
            for (view in mRightViewList) {
                rightUseSpace += view.measuredWidth
            }
            // titleContainer 想要获取的的宽度
            val titleContainerWidth = titleContainer!!.width
            // 需要让 titleContainer 居中，需要保证左右的空余距离是一样的
            val remainderSpace = measuredWidth - Math.max(leftUseSpace, rightUseSpace) * 2
            if (remainderSpace < titleContainerWidth) {
                //剩余的控件不够 title 显示
                val size = MeasureSpec.makeMeasureSpec(remainderSpace, MeasureSpec.EXACTLY)
                titleContainer!!.measure(size, heightMeasureSpec)
            }
        }
    }
}