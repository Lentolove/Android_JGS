package com.tsp.android.detail.item

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.common.ext.loadUrl
import com.tsp.android.hiui.item.HiDataItem
import com.tsp.android.hiui.item.HiViewHolder

/**
 *     author : shengping.tian
 *     time   : 2021/09/15
 *     desc   : 商品详情页画廊长图
 *     version: 1.0
 */
class GalleryItem(private val url: String) : HiDataItem<Any, HiViewHolder>() {

    private var parentWidth: Int = 0

    override fun onBindData(holder: HiViewHolder, position: Int) {
        val imageView = holder.itemView as ImageView
        imageView.loadUrl(url) {
            val drawableWidth = it.intrinsicWidth
            val drawableHeight = it.intrinsicHeight
            val params = imageView.layoutParams ?: RecyclerView.LayoutParams(
                parentWidth,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            params.width = parentWidth
            params.height = (drawableHeight / (drawableWidth * 1.0f / parentWidth)).toInt()
            imageView.layoutParams = params
            ViewCompat.setBackground(imageView, it)
        }
    }

    override fun getItemView(parent: ViewGroup): View? {
        val imageView = ImageView(parent.context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setBackgroundColor(Color.WHITE)
        return imageView
    }

    override fun onViewAttachedWindow(holder: HiViewHolder) {
        //提前给imageView 预设一个高度值  等于parent的宽度
        parentWidth = (holder.itemView.parent as ViewGroup).measuredWidth
        val params = holder.itemView.layoutParams
        if (params.width != parentWidth) {
            params.width = parentWidth
            params.height = parentWidth
            holder.itemView.layoutParams = params
        }
    }

}