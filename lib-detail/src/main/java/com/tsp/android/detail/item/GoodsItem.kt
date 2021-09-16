package com.tsp.android.detail.item

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.common.ext.loadUrl
import com.tsp.android.detail.R
import com.tsp.android.detail.model.GoodsModel
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.android.hiui.item.HiDataItem
import com.tsp.android.hiui.item.HiViewHolder

/**
 *     author : shengping.tian
 *     time   : 2021/09/14
 *     desc   :
 *     version: 1.0
 */
open class GoodsItem(val goodsModel: GoodsModel, val hotTab: Boolean = false) :
    HiDataItem<Any, HiViewHolder>() {

    //最多显示三个标签
    private val MAX_TAG_SIZE = 3

    private val labelTextViewHeight = HiDisplayUtil.dp2px(16f)

    private val labelTextViewMargin = HiDisplayUtil.dp2px(5f)

    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context ?: return
        //商品图标
        holder.findViewById<ImageView>(R.id.item_image)?.loadUrl(goodsModel.sliderImage)
        holder.findViewById<TextView>(R.id.item_title)?.text = goodsModel.goodsName
        holder.findViewById<TextView>(R.id.item_price)?.text =
            selectPrice(goodsModel.groupPrice, goodsModel.marketPrice)
        holder.findViewById<TextView>(R.id.item_sale_desc)?.text = goodsModel.completedNumText

        //商品的标签 "质量好 耐脏 好看"
        val itemContainerView:LinearLayout? = holder.findViewById(R.id.item_label_container)
        if (itemContainerView != null){
            val tags = goodsModel.tags
            if (!tags.isNullOrEmpty()) {
                itemContainerView.visibility = View.VISIBLE
                val tagArray = tags.split(" ")
                val childCount = itemContainerView.childCount
                for (index in tagArray.indices) {
                    if (index > MAX_TAG_SIZE - 1) {
                        for (i in childCount - 1 downTo MAX_TAG_SIZE - 1) {
                            //需要删除后面的
                            itemContainerView.removeViewAt(i)
                        }
                        break
                    }
                    val labelTextView: TextView = if (index > childCount - 1) {
                        val view = createLabelView(context, index != 0)
                        itemContainerView.addView(view)
                        view
                    } else {
                        itemContainerView.getChildAt(index) as TextView
                    }
                    labelTextView.text = tagArray[index]
                }

            } else {
                itemContainerView.visibility = View.GONE
            }
        }

        //热门商品 和 非热门商品的区别
        if (!hotTab) {
            //上图下文字描述,计算父类的的 margin 和 padding
            val margin = HiDisplayUtil.dp2px(2f)
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            val parentLeft = mAdapter?.getAttachRecyclerView()?.left ?: 0
            val parentPaddingLeft = mAdapter?.getAttachRecyclerView()?.paddingLeft ?: 0
            val itemLeft = holder.itemView.left
            if (itemLeft == (parentLeft + parentPaddingLeft)){
                params.rightMargin = margin
            }else{
                params.leftMargin = margin
            }
            holder.itemView.layoutParams = params
        }
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "点击了:$position", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 创建商品标签
     * @param context Context
     * @param withLeftMargin Boolean 是否需要设置左边界
     * @return TextView
     */
    private fun createLabelView(context: Context, withLeftMargin: Boolean): TextView {
        val label = TextView(context).apply {
            setTextColor(ContextCompat.getColor(context, R.color.color_e75))
            setBackgroundResource(R.drawable.shape_goods_label)
            textSize = 11f
            gravity = Gravity.CENTER
        }
        val params =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, labelTextViewHeight)
        params.leftMargin = if (withLeftMargin) labelTextViewMargin else 0
        label.layoutParams = params
        return label
    }

    override fun getItemLayoutRes(): Int {
        return if (hotTab) R.layout.goods_list_item1 else R.layout.goods_list_item2
    }

    override fun getSpanSize(): Int {
        return if (hotTab) super.getSpanSize() else 1
    }

    private fun selectPrice(groupPrice: String?, marketPrice: String?): String? {
        var price: String? = if (TextUtils.isEmpty(marketPrice)) groupPrice else marketPrice
        if (price?.startsWith("¥") != true) {
            price = "¥".plus(price)
        }
        return price
    }
}


