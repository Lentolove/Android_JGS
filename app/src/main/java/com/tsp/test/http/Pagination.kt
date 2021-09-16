package com.tsp.test.http

import androidx.annotation.Keep

/**
 * Created by xiaojianjun on 2019-11-07.
 */
@Keep
data class Pagination(
    val offset: Int,
    val size: Int,
    val total: Int,
    val pageCount: Int,
    val curPage: Int,
    val over: Boolean,
    val datas: MutableList<Article>
)