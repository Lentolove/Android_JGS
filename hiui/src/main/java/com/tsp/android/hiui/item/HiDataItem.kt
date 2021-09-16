package com.tsp.android.hiui.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *     author : shengping.tian
 *     time   : 2021/09/13
 *     desc   : 通用数据类型
 *     version: 1.0
 */
abstract class HiDataItem<DATA, VH : RecyclerView.ViewHolder>(data: DATA? = null) {

    var mAdapter: HiAdapter? = null
    var mData: DATA? = null

    init {
        this.mData = data
    }

    /**
     * 绑定数据，由具体的业务层去实现绑定逻辑
     * @param holder VH ViewHolder
     * @param position Int 位置
     */
    abstract fun onBindData(holder: VH, position: Int)

    /**
     * 返回当前布局 item 的资源 id，至少需要实现 getItemLayoutRes 和 getItemView 中的一步 open 方法
     * @return Int
     */
    open fun getItemLayoutRes(): Int {
        return -1;
    }

    /**
     * 返回该item的视图view
     * @return 有的视图可能直接是一个 View
     */
    open fun getItemView(parent: ViewGroup): View? {
        return null
    }

    /**
     * 为当前 itemData 设置 adapter
     * @param adapter HiAdapter
     */
    fun setAdapter(adapter: HiAdapter) {
        if (mAdapter != null && mAdapter == adapter) return
        this.mAdapter = adapter
    }

    /**
     * 向 RecyclerView 中添加 item 时候，可通知刷新当前视图
     */
    fun refreshItem() {
        mAdapter?.refreshItem(this)
    }

    /**
     * 从 RecyclerView 中移除 item
     */
    fun removeItem() {
        mAdapter?.removeItem(this)
    }

    /**
     * RecyclerView 可能为网格布局或者瀑布流布局，当前 item 占的列数
     * @return Int 当前 item 在 RecyclerView 中占几列
     */
    open fun getSpanSize(): Int {
        return 0
    }

    /**
     * 当当前 view 被滑进屏幕
     * @param holder VH
     */
    open fun onViewAttachedWindow(holder: VH){

    }

    /**
     * 当前 view 被滑出屏幕
     * @param holder VH
     */
    open fun onViewDetachedFromWindow(holder: VH) {

    }

    /**
     * 创建 itemData 的视图的 ViewHolder 子类需要复写
     * @param parent ViewGroup
     * @return VH?
     */
    open fun onCreateViewHolder(parent: ViewGroup): VH? {
        return null
    }
}