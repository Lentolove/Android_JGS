package com.tsp.android.detail.item

import com.tsp.android.detail.R
import com.tsp.android.hiui.item.HiDataItem
import com.tsp.android.hiui.item.HiViewHolder

/**
 *     author : shengping.tian
 *     time   : 2021/09/15
 *     desc   : 相似商品 dataItem
 *     version: 1.0
 */
class SimilarTitleItem() : HiDataItem<Any, HiViewHolder>() {

    override fun onBindData(holder: HiViewHolder, position: Int) {

    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_similar_title
    }
}