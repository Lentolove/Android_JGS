package com.tsp.android.detail.item

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.widget.ImageView
import android.widget.TextView
import com.tsp.android.common.ext.loadUrl
import com.tsp.android.detail.R
import com.tsp.android.hiui.banner.HiBanner
import com.tsp.android.hiui.banner.core.HiBannerAdapter
import com.tsp.android.hiui.banner.core.HiBannerMo
import com.tsp.android.hiui.banner.indicator.HiNumIndicator
import com.tsp.android.hiui.item.HiDataItem
import com.tsp.android.hiui.item.HiViewHolder

/**
 *     author : shengping.tian
 *     time   : 2021/09/13
 *     desc   : 详情页头部
 *     version: 1.0
 */
class HeaderItem(
    private val imageList: List<String>,//header 轮播图
    private val price: String,//价格 ""
    private val completedNumText: String,//已拼数量
    private var goodsName: String //商品名称
) : HiDataItem<Any, HiViewHolder>() {


    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context ?: return
        //构建 banner 数据模型
        val bannerList = createBannerMo(imageList)
        holder.findViewById<HiBanner>(R.id.hi_banner)?.apply {
            setHiIndicator(HiNumIndicator(context))
            setBannerData(bannerList)
            setBindAdapter{ viewHolder: HiBannerAdapter.HiBannerViewHolder?, mo: HiBannerMo?, position: Int ->
                val imageView = viewHolder?.rootView as? ImageView
                mo?.let {
                    imageView?.loadUrl(it.url)
                }
            }
        }
        holder.findViewById<TextView>(R.id.price)?.text =  spanPrice(price)
        holder.findViewById<TextView>(R.id.sale_desc)?.text =  completedNumText
        holder.findViewById<TextView>(R.id.title)?.text =  goodsName
    }

    /**
     * 转成富文本的形式
     * @param price String?
     * @return CharSequence
     */
    private fun spanPrice(price: String?): CharSequence {
        if (TextUtils.isEmpty(price)) return ""
        val ss = SpannableString(price)
        ss.setSpan(AbsoluteSizeSpan(18, true), 1, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ss
    }

    private fun createBannerMo(list: List<String>): List<HiBannerMo> {
        val bannerItems = arrayListOf<HiBannerMo>()
        list.forEach {
            val bannerMo = object : HiBannerMo() {}
            bannerMo.url = it
            bannerItems.add(bannerMo)
        }
        return bannerItems
    }


    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_header
    }
}