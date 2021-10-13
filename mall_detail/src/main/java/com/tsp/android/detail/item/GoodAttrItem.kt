package com.tsp.android.detail.item

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.tsp.android.detail.R
import com.tsp.android.detail.model.DetailModel
import com.tsp.android.hiui.item.HiDataItem
import com.tsp.android.hiui.item.HiViewHolder
import com.tsp.android.hiui.view.InputItemLayout

/**
 *     author : shengping.tian
 *     time   : 2021/09/15
 *     desc   : 商品详情模块
 *              品牌：阿迪萨斯
 *              产品类型： 衣物类
 *     version: 1.0
 */
class GoodAttrItem(private val detailModel: DetailModel) : HiDataItem<Any, HiViewHolder>() {

    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context ?: return
        val goodAttr = detailModel.goodAttr
        goodAttr?.let {
            // 品牌：阿迪萨斯 产品类型： 衣物类
            val iterator = it.iterator()
            var index = 0
            val attrContainer = holder.findViewById<LinearLayout>(R.id.attr_container)!!
            attrContainer.visibility = View.VISIBLE
            while (iterator.hasNext()) {
                val item = iterator.next().entries
                val key = item.first().key
                val value = item.first().value
                val attrItemView: InputItemLayout = if (index < attrContainer.childCount) {
                    attrContainer.getChildAt(index)
                } else {
                    val itemLayout = LayoutInflater.from(context)
                        .inflate(R.layout.layout_detail_item_attr_item, attrContainer, false)
                    itemLayout
                } as InputItemLayout
                //因为复用的 InputItemLayout 左右显示的文本，所以需要设置右侧EditText无法点击
                attrItemView.apply {
                    getEditText().isEnabled = false
                    getEditText().hint = value
                    getTileView().text = key
                }
                if (attrItemView.parent == null) {
                    attrContainer.addView(attrItemView)
                }
                index++
            }
        }

        val goodDescription = detailModel.goodDescription
        goodDescription?.let {
            val desTextView = holder.findViewById<TextView>(R.id.attr_desc)
            desTextView?.visibility = View.VISIBLE
            desTextView?.text = goodDescription
        }
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_attr
    }
}