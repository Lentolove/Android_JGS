package com.tsp.android.hiui.item

import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *     author : shengping.tian
 *     time   : 2021/09/10
 *     desc   : RecyclerView 的 ViewHolder
 *     version: 1.0
 */
open class HiViewHolder(val item: View) : RecyclerView.ViewHolder(item) {

    //用于缓存的View，防止重复查找
    private var viewCache = SparseArray<View>()

    fun <T : View> findViewById(viewId: Int): T? {
        var view = viewCache.get(viewId)
        if (view == null) {
            view = itemView.findViewById<T>(viewId)
            viewCache.put(viewId, view)
        }
        return view as? T
    }

}