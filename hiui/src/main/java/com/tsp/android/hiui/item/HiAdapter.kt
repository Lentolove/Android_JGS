package com.tsp.android.hiui.item

import android.content.Context
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 *     author : shengping.tian
 *     time   : 2021/09/13
 *     desc   : 通用的 RecyclerView 的 adapter
 *     version: 1.0
 */
class HiAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recyclerViewRef: WeakReference<RecyclerView>? = null

    private val mLayoutInflater = LayoutInflater.from(context)

    //数据集合
    private val dataSets = ArrayList<HiDataItem<*, out RecyclerView.ViewHolder>>()

    //不同类型的 item 的 位置
    private val typePosition = SparseIntArray()

    //为 RecyclerView 添加 header 视图
    private val headers = SparseArray<View>()

    //为 RecyclerView 添加 footer  视图
    private val footers = SparseArray<View>()

    //表示当前添加的为 header 视图的位置索引的 key
    private var BASE_ITEM_TYPE_HEADER = 1000000

    //表示当前添加的为 footer 视图的位置索引，从2000000 开始,这个值是随机定义的
    private var BASE_ITEM_TYPE_FOOTER = 2000000


    /**
     * 向 RecyclerView 的 header 中添加 view
     * @param view View
     */
    fun addHerderView(view: View) {
        //如果没有添加过
        if (headers.indexOfValue(view) < 0) {
            headers.put(BASE_ITEM_TYPE_HEADER++, view)
            notifyItemChanged(headers.size() - 1)
        }
    }

    /**
     * 从 RecyclerView 的 header 中添加 view
     * @param view View
     */
    fun removeHeaderView(view: View) {
        val viewIndex = headers.indexOfValue(view)
        if (viewIndex < 0) return
        headers.removeAt(viewIndex)
        notifyItemChanged(viewIndex)
    }

    /**
     * 向 RecyclerView 的底部添加 view
     * @param view View
     */
    fun addFooterView(view: View) {
        if (footers.indexOfValue(view) < 0) {
            footers.put(BASE_ITEM_TYPE_FOOTER++, view)
            //通知位置刷新
            notifyItemChanged(itemCount)
        }
    }

    /**
     * 移除 RecyclerView 底部的 view
     * @param view View
     */
    fun removeFooterView(view: View) {
        val viewIndex = footers.indexOfValue(view)
        if (viewIndex < 0) return
        footers.removeAt(viewIndex)
        //position 代表的是在列表中分位置 headers.size + recyclerview 中的数据 + viewIndex
        notifyItemRemoved(viewIndex + getHeaderSize() + getOriginalItemSize())
    }

    /**
     * 添加的 headers view 的个数
     * @return Int
     */
    fun getHeaderSize(): Int {
        return headers.size()
    }

    /**
     * 添加的 footer view 的个数
     * @return Int
     */
    fun getFooterSize(): Int {
        return footers.size()
    }

    /**
     * 返回当前 RecyclerView 中数据item的个数，不包括添加的 header 和 footer
     * @return Int
     */
    fun getOriginalItemSize(): Int {
        return dataSets.size
    }

    /**
     * 向指定位置添加数据
     * @param index Int 如果 index >=0 则，否则添加到末尾
     * @param dataItem HiDataItem<*, out ViewHolder>
     * @param notify Boolean 添加数据后是否需要数据刷新
     */
    fun addItemAt(
        index: Int, dataItem: HiDataItem<*, out RecyclerView.ViewHolder>,
        notify: Boolean
    ) {
        if (index >= 0) {
            dataSets.add(index, dataItem)
        } else {
            dataSets.add(dataItem)
        }
        //需要刷新的位置
        val notifyPosition = if (index >= 0) index else dataSets.size - 1
        if (notify) notifyItemInserted(notifyPosition)
        dataItem.setAdapter(this)
    }

    /**
     * 往现有集合的尾部逐年items集合
     * @param items List<HiDataItem<*, out ViewHolder>>
     * @param notify Boolean
     */
    fun addItems(items: List<HiDataItem<*, out RecyclerView.ViewHolder>>, notify: Boolean) {
        val start = dataSets.size
        items.forEach { dataItem ->
            dataSets.add(dataItem)
            dataItem.setAdapter(this)
        }
        if (notify) notifyItemRangeInserted(start, items.size)
    }


    /**
     * 从指定位置上移除item
     * @param index Int 移除的 item 位置
     * @return HiDataItem<*, out RecyclerView.ViewHolder>?
     */
    fun removeItemAt(index: Int): HiDataItem<*, out RecyclerView.ViewHolder>? {
        return if (index >= 0 && index < dataSets.size) {
            val removeDataItem: HiDataItem<*, out RecyclerView.ViewHolder> = dataSets.removeAt(index)
            notifyItemRemoved(index)
            removeDataItem
        } else {
            null
        }
    }

    /**
     * 指定刷新 某个item的数据
     * @param dataItem HiDataItem<*, out ViewHolder>
     */
    fun refreshItem(dataItem: HiDataItem<*, out RecyclerView.ViewHolder>) {
        val itemIndex = dataSets.indexOf(dataItem)
        notifyItemChanged(itemIndex)
    }

    /**
     * 判断当前位置是否是 Headers
     * @param position Int
     * @return Boolean
     */
    private fun isHeaderPosition(position: Int): Boolean {
        return position < headers.size()
    }

    /**
     *  判断当前位置是否是 footers
     * @param position Int
     * @return Boolean
     */
    private fun isFooterPosition(position: Int): Boolean {
        return position >= getHeaderSize() + getOriginalItemSize()
    }

    /**
     * RecyclerView 的 type 类型,当前 RecyclerView 存在多种类型的 viewType
     * 以每种 item 类型的class.hashcode为 该i tem 的 viewType,
     * 把 type 存储起来，是为了onCreateViewHolder方法能够为不同类型的item创建不同的 viewholder
     * @param position Int
     * @return Int
     */
    override fun getItemViewType(position: Int): Int {
        //headers 和 footers 直接返回当前view
        if (isHeaderPosition(position)) {
            return headers.keyAt(position)
        }
        if (isFooterPosition(position)) {
            //footer的位置计算:  position = 6 , headerSize = 1  itemSize = 5
            val footerPosition = position - getHeaderSize() - getOriginalItemSize()
            return footers.keyAt(footerPosition)
        }
        //dataItem 返回当前 item 对应的 实体数据的 hashCode
        val itemPosition = position - getHeaderSize()
        val dataItem = dataSets[itemPosition]
        val type = dataItem.javaClass.hashCode()
        typePosition.put(type, position)
        return type
    }

    /**
     * 从 RecyclerView 中移除 dataItem
     * @param dataItem HiDataItem<*, out ViewHolder>
     */
    fun removeItem(dataItem: HiDataItem<*, out RecyclerView.ViewHolder>) {
        val itemIndex = dataSets.indexOf(dataItem)
        removeItemAt(itemIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (headers.indexOfKey(viewType) >= 0) {
            //当前是 headers,注意，这里的viewType我们是从 BASE_ITEM_TYPE_HEADER 定义的,返回默认的ViewHolder
            val view = headers[viewType]
            return object : RecyclerView.ViewHolder(view) {}
        }
        if (footers.indexOfKey(viewType) >= 0) {
            val view = footers[viewType]
            return object : RecyclerView.ViewHolder(view) {}
        }
        //会出现的不同的 itemData ，但是他们的 viewType 是相同的情况。从而导致获取到的 dataItem 始终是第一次关联的 dataItem 对象。
        // 会导致通过getItemView创建的成员变量，只在第一个dataItem中，其它实例中无法生效，为了解决dataItem成员变量binding, 刷新之后无法被复用的问题
        val position = typePosition[viewType]
        val dataItem = dataSets[position]
        val vh = dataItem.onCreateViewHolder(parent)
        if (vh != null) return vh
        //因为当前itemView 的视图可能是复写了 getItemView / getItemLayoutRes 中的某一个
        var view: View? = dataItem.getItemView(parent)
        if (view == null) {
            val layoutRes = dataItem.getItemLayoutRes()
            if (layoutRes < 0) {
                //都为复写，抛出异常，提醒使用者实现  getItemView / getItemLayoutRes 中的一个方法
                throw RuntimeException("dataItem: ${dataItem.javaClass.name} must override getItemView or getItemLayoutRes")
            }
            view = mLayoutInflater.inflate(layoutRes, parent, false)
        }
        return createViewHolderInternal(dataItem.javaClass, view!!)
    }

    /**
     * 为具体的 dataItem 创建 ViewHolder
     * @return RecyclerView.ViewHolder
     */
    private fun createViewHolderInternal(
        javaClass: Class<HiDataItem<*, out RecyclerView.ViewHolder>>, view: View
    ): RecyclerView.ViewHolder {
        //得到该 Item 的父类类型,即为HiDataItem.class。 class 也是type的一个子类。 type的子类常见的有 class，类泛型,ParameterizedType参数泛型 ，TypeVariable字段泛型
        //所以进一步判断它是不是参数泛型
        val superClass = javaClass.genericSuperclass
        if (superClass is ParameterizedType) {
            //得到它携带的泛型参数的数组
            val arguments = superClass.actualTypeArguments
            //从泛型数组中遍历判断是不是需要的 RecyclerView.ViewHolder 的子类型的。
            for (type in arguments) {
                if (type is Class<*> && RecyclerView.ViewHolder::class.java.isAssignableFrom(type)) {
                    //找到了，通过反射来构建实际的泛型对象,如果直接在 HiDataItem 子类上标记 RecyclerView.ViewHolder，抽象类是不允许反射的
                    try {
                        return type.getConstructor(View::class.java)
                            .newInstance(view) as RecyclerView.ViewHolder
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
        //没有找到就用默认的 HiViewHolder
        return object : HiViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeaderPosition(position) || isFooterPosition(position)) return
        val itemPosition = position - getHeaderSize()
        val dataItem = getItem(itemPosition)
        //调用 dataItem 的 onBindData 讲具体的业务逻辑交给实现类去复写
        dataItem?.onBindData(holder, itemPosition)
    }

    override fun getItemCount(): Int {
       return dataSets.size + getHeaderSize() + getFooterSize()
    }

    /**
     * 获取当前 item 的数据对象
     * @param position Int
     * @return HiDataItem<*, RecyclerView.ViewHolder>?
     */
    fun getItem(position: Int): HiDataItem<*, RecyclerView.ViewHolder>? {
        if (position < 0 || position >= dataSets.size) return null
        return dataSets[position] as HiDataItem<*, RecyclerView.ViewHolder>
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViewRef = WeakReference(recyclerView)
        //为列表上的 item 适配 网格布局
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager){
            val spanCount  = layoutManager.spanCount
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    if (isHeaderPosition(position) || isFooterPosition(position)) return spanCount
                    val itemPosition = position - getHeaderSize()
                    if(itemPosition < dataSets.size){
                        val dataItem = getItem(itemPosition)
                        if (dataItem != null){
                            val spanSize = dataItem.getSpanSize()
                            return if (spanSize <= 0) spanCount else spanSize
                        }
                    }
                    return spanCount
                }
            }
        }
    }

    /**
     * 当 RecyclerView 从屏幕移除的时候
     * @param recyclerView RecyclerView
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerViewRef?.clear()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
       val recyclerView = getAttachRecyclerView() ?: return
        //瀑布流的 item 占比适配
        val position = recyclerView.getChildAdapterPosition(holder.itemView)
        val isHeaderOrFooter = isHeaderPosition(position) || isFooterPosition(position)
        val itemPosition = position - getHeaderSize()
        val dataItem = getItem(itemPosition) ?: return
        val lp = holder.itemView.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams){
            val manager = recyclerView.layoutManager as StaggeredGridLayoutManager
            if (isHeaderOrFooter){
                lp.isFullSpan = true
                return
            }
            val spanSize = dataItem.getSpanSize()
            if (spanSize == manager.spanCount){
                lp.isFullSpan = true
            }
        }
        dataItem.onViewAttachedWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        if (isHeaderPosition(position) || isFooterPosition(position)) return
        val itemPosition = position - getHeaderSize()
        val dataItem = getItem(itemPosition) ?: return
        dataItem.onViewDetachedFromWindow(holder)

    }

    fun cleatItems(){
        dataSets.clear()
        notifyDataSetChanged()
    }

    open fun getAttachRecyclerView():RecyclerView?{
        return  recyclerViewRef?.get()
    }
}