package com.tsp.android.hiui.slider

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tsp.android.hiui.R
import com.tsp.android.hiui.item.HiViewHolder

/**
 *     author : shengping.tian
 *     time   : 2021/09/10
 *     desc   : HiSlideView 自定义view，左边为一个垂直的 RecyclerView显示menu菜单
 *              右边一个 RecyclerView 显示菜单对应的内容
 *     version: 1.0
 */
class HiSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    //左侧菜单的item 布局样式
    private val MENU_ITEM_LAYOUT_RES_ID = R.layout.hi_slider_menu_item

    //右侧内容区的 item 样式
    private val COTENT_ITEM_LAYOUT_RES_ID = R.layout.hi_slider_content_item

    val menuView = RecyclerView(context)

    val contentView = RecyclerView(context)

    private var menuItemAttr: SliderAttrsParse.MenuItemAttr =
        SliderAttrsParse.parseMenuItemAttr(context, attrs)


    init {
        orientation = HORIZONTAL

        menuView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        //去掉 menuView 滑动到顶部和底部的动画
        menuView.overScrollMode = View.OVER_SCROLL_NEVER
        menuView.itemAnimator = null

        contentView.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        contentView.overScrollMode = View.OVER_SCROLL_NEVER
        contentView.itemAnimator = null
        //水平布局，将两个 recyclerView 水平摆放
        addView(menuView)
        addView(contentView)
    }

    /**
     * 为 menuView 绑定回调和参数
     *  @layoutRes menuItem 资源样式
     *  @itemCount 数量
     *  @onBindView 绑定数据的时候回调出去，有具体业务去实现具体绑定逻辑
     *  @onItemClick 将点击事件回调出去
     */
    fun bindMenuView(
        layoutRes: Int = MENU_ITEM_LAYOUT_RES_ID,
        itemCount: Int,
        onBindView: (HiViewHolder, Int) -> Unit,
        onItemClick: (HiViewHolder, Int) -> Unit
    ) {
        menuView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        menuView.adapter = MenuAdapter(layoutRes, itemCount, onBindView, onItemClick)
    }

    /**
     * 绑定内容取数的数据及点击事件
     * @param layoutRes 右侧内容区域资源样式
     * @param itemCount  数据条目
     * @param itemDecoration  自定义的 ItemDecoration
     * @param layoutManager  布局布局方式
     * @param onBindView 绑定数据的回调
     * @param onItemClick 点击事件回调
     * 每次点击左侧菜单按钮，右侧内容区都会变化
     */
    fun bindContentView(
        layoutRes: Int = COTENT_ITEM_LAYOUT_RES_ID,
        itemCount: Int,
        itemDecoration: RecyclerView.ItemDecoration?,
        layoutManager: RecyclerView.LayoutManager,
        onBindView: (HiViewHolder, Int) -> Unit,
        onItemClick: (HiViewHolder, Int) -> Unit
    ) {
        if (contentView.layoutManager == null) {
            contentView.layoutManager = layoutManager
            contentView.adapter = ContentAdapter(layoutRes)
            itemDecoration?.let {
                contentView.addItemDecoration(it)
            }
        }
        val contentAdapter = contentView.adapter as ContentAdapter
        contentAdapter.update(itemCount, onBindView, onItemClick)
        contentAdapter.notifyDataSetChanged()
        contentView.scrollToPosition(0)
    }


    /**
     * content 区域的数据是会实时改变的，每个条目的布局及数目可能不一样,所以需要动态绑定数据
     * @property layoutRes Int
     * @constructor
     */
    inner class ContentAdapter(private val layoutRes: Int) : RecyclerView.Adapter<HiViewHolder>() {
        private lateinit var onItemClick: (HiViewHolder, Int) -> Unit
        private lateinit var onBindView: (HiViewHolder, Int) -> Unit
        private var count: Int = 0


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HiViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutRes, parent, false)
            return HiViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: HiViewHolder, position: Int) {
            //让具体的业务场景去实现
            onBindView(holder, position)
            holder.itemView.setOnClickListener {
                onItemClick(holder, position)
            }
        }

        override fun getItemCount(): Int {
            return count
        }

        fun update(
            itemCount: Int,
            onBindView: (HiViewHolder, Int) -> Unit,
            onItemClick: (HiViewHolder, Int) -> Unit
        ) {
            this.onItemClick = onItemClick
            this.count = itemCount
            this.onBindView = onBindView
        }

        /**
         * 当视图与窗口绑定时候，如果是网格布局等，不同的条目可能占用的 spanSize 不一样
         * @param holder HiViewHolder
         */
        override fun onViewAttachedToWindow(holder: HiViewHolder) {
            super.onViewAttachedToWindow(holder)
            //右侧内容区域剩余的宽度
            val remainSpace = width - paddingLeft - paddingRight - menuItemAttr.width
            val layoutManager = contentView.layoutManager
            var spanCount = 0
            if (layoutManager is GridLayoutManager) {
                spanCount = layoutManager.spanCount
            } else if (layoutManager is StaggeredGridLayoutManager) {
                spanCount = layoutManager.spanCount
            }
            if (spanCount > 0) {
                //创建content itemView  ，设置它的layoutParams 的原因，是防止图片未加载出来之前，列表滑动时 上下闪动的效果，提前根据 spanCount 设置每个条目的宽度设置大小
                val itemWith = remainSpace / spanCount
                val layoutParams = holder.itemView.layoutParams
                layoutParams.width = itemWith
                layoutParams.height = itemWith
                holder.itemView.layoutParams = layoutParams
            }
        }
    }


    /**
     * 左侧 RecyclerView 的 适配器
     */
    inner class MenuAdapter(
        val layoutRes: Int,
        val count: Int,
        val onBindView: (HiViewHolder, Int) -> Unit,
        val onItemClick: (HiViewHolder, Int) -> Unit
    ) : RecyclerView.Adapter<HiViewHolder>() {

        //本次选中的 item 位置
        private var currentSelectIndex = 0

        //上一次选中的item的位置
        private var lastSelectIndex = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HiViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutRes, parent, false)
            val params = RecyclerView.LayoutParams(menuItemAttr.width, menuItemAttr.height)
            itemView.layoutParams = params
            itemView.setBackgroundColor(menuItemAttr.normalBackgroundColor)
            return HiViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: HiViewHolder, position: Int) {
            holder.findViewById<TextView>(R.id.menu_item_title)
                ?.setTextColor(menuItemAttr.textColor)
            holder.findViewById<ImageView>(R.id.menu_item_indicator)
                ?.setImageDrawable(menuItemAttr.indicator)

            //初次绑定数据的时候没有点击事件，需要有一个默认的选择
            holder.itemView.setOnClickListener {
                currentSelectIndex = position
                notifyItemChanged(position)
                notifyItemChanged(lastSelectIndex)
                //如果直接在这里更改样式，会有一种延迟效果，导致左侧 item 短暂时间内有两个item的被选中
            }

            //初次绑定的时候有个默认被选中的条目,回调给使用者自己处理。当点击item条目时候，
            if (currentSelectIndex == position) {
                onItemClick(holder, position)
                lastSelectIndex = currentSelectIndex
            }
            applyItemAttr(position, holder)
            onBindView(holder, position)
        }

        override fun getItemCount(): Int {
            return count
        }

        /**
         * 选中后更新 menuItem 的样式
         */
        private fun applyItemAttr(position: Int, holder: HiViewHolder) {
            val selected = position == currentSelectIndex
            val titleView: TextView? = holder.findViewById(R.id.menu_item_title)
            val indicatorView: ImageView? = holder.findViewById(R.id.menu_item_indicator)
            indicatorView?.visibility = if (selected) View.VISIBLE else View.GONE
            titleView?.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                if (selected) menuItemAttr.selectTextSize.toFloat()
                else menuItemAttr.textSize.toFloat()
            )
            holder.itemView.setBackgroundColor(if (selected) menuItemAttr.selectBackgroundColor else menuItemAttr.normalBackgroundColor)
            titleView?.isSelected = selected
        }
    }
}