package com.tsp.mall.search.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.hiui.item.HiAdapter

/**
 *     author : shengping.tian
 *     time   : 2021/09/17
 *     desc   : 搜过商品结果列表
 *     version: 1.0
 */
class GoodsSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = HiAdapter(context)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }


    /**
     * 绑定数据
     */
    fun bindData(list: List<String>) {

    }


}