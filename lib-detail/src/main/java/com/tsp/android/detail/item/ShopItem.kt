package com.tsp.android.detail.item

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.common.ext.loadUrl
import com.tsp.android.detail.R
import com.tsp.android.detail.model.DetailModel
import com.tsp.android.detail.model.GoodsModel
import com.tsp.android.hiui.item.HiAdapter
import com.tsp.android.hiui.item.HiDataItem
import com.tsp.android.hiui.item.HiViewHolder

/**
 *     author : shengping.tian
 *     time   : 2021/09/14
 *     desc   : 店铺详情 dataItem
 *     version: 1.0
 */
class ShopItem(val detailModel: DetailModel) : HiDataItem<Any, HiViewHolder>() {

    private val SHOP_GOODS_SPAN_COUNT = 3

    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context ?: return
        //店铺信息
        val shop = detailModel.shop
        holder.findViewById<ImageView>(R.id.detail_shop_logo)?.loadUrl(shop.logo)
        holder.findViewById<TextView>(R.id.detail_shop_title)?.text = shop.name
        holder.findViewById<TextView>(R.id.detail_shop_dec)?.text =
            String.format(context.getString(R.string.shop_desc), shop.goodsNum, shop.completedNum)
        //"evaluation":"描述相符 高 服务态度 高 物流服务 高"
        val evaluation = shop.evaluation
        val evaContainer = holder.findViewById<LinearLayout>(R.id.detail_tag_container)
        evaContainer?.let {
            evaContainer.visibility = View.VISIBLE
            val evaArray = evaluation.split(" ")
            var index = 0
            for (evaIndex in 0 until evaArray.size / 2) {
                val evaTextView: TextView = if (evaIndex < evaContainer.childCount) {
                    evaContainer.getChildAt(evaIndex) as TextView
                } else {
                    val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.weight = 1f
                    val itemView = TextView(context).apply {
                        layoutParams = params
                        gravity = Gravity.CENTER
                        textSize = 14f
                        setTextColor(ContextCompat.getColor(context, R.color.color_666))
                    }
                    evaContainer.addView(itemView)
                    itemView
                }
                val name = if (index >= evaArray.size) continue else evaArray[index]
                val value = evaArray[index + 1]
                index += 2
                //描述相符 高 对 高 进行富文本操作加粗
                val spanTag = spanTag(context, name, value)
                evaTextView.text = spanTag
            }
        }
        //店铺关联的商品
        val flowGoods = detailModel.flowGoods ?: return
        val goodsRecyclerView: RecyclerView =
            holder.findViewById(R.id.detail_shop_recyclerView) ?: return
        goodsRecyclerView.visibility = View.VISIBLE
        if (goodsRecyclerView.layoutManager == null) {
            goodsRecyclerView.layoutManager = GridLayoutManager(context, SHOP_GOODS_SPAN_COUNT)
        }
        if (goodsRecyclerView.adapter == null) {
            goodsRecyclerView.adapter = HiAdapter(context)
        }
        val dataItem = mutableListOf<GoodsItem>()
        flowGoods.forEach {
            dataItem.add(ShopGoodsItem(it))
        }
        val adapter = goodsRecyclerView.adapter as HiAdapter
        adapter.cleatItems()
        adapter.addItems(dataItem, true)
    }

    private inner class ShopGoodsItem(goodsModel: GoodsModel) : GoodsItem(goodsModel, false) {

        override fun getItemLayoutRes(): Int {
            return R.layout.layout_detail_item_shop_goods_item
        }

        override fun onViewAttachedWindow(holder: HiViewHolder) {
            super.onViewAttachedWindow(holder)
            val viewParent: ViewGroup = holder.itemView.parent as ViewGroup
            val availableWidth =
                viewParent.measuredWidth - viewParent.paddingLeft - viewParent.paddingRight
            val itemWidth = availableWidth / SHOP_GOODS_SPAN_COUNT
            val itemImage = holder.findViewById<ImageView>(R.id.item_image)!!
            val params = itemImage.layoutParams
            params.width = itemWidth
            params.height = itemWidth
            itemImage.layoutParams = params
        }
    }

    /**
     * 将 String 文本转成 SpannableString
     * @param context Context
     * @param name String 描述相符
     * @param value String 高
     * @return CharSequence
     */
    private fun spanTag(context: Context, name: String, value: String): CharSequence {
        val ss = SpannableString(value)
        val ssb = SpannableStringBuilder()
        ss.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_c61)),
            0,
            ss.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            BackgroundColorSpan(ContextCompat.getColor(context, R.color.color_f8e)),
            0,
            ss.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ssb.append(name)
        ssb.append(ss)
        return ssb
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_shop
    }
}