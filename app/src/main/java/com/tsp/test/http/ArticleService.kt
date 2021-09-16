package com.tsp.test.http

import com.tsp.android.hilibrary.restful.HiCall
import com.tsp.android.hilibrary.restful.anation.Filed
import com.tsp.android.hilibrary.restful.anation.GET
import com.tsp.android.hilibrary.restful.anation.POST
import com.tsp.android.hilibrary.restful.anation.Path

/**
 *     author : shengping.tian
 *     time   : 2021/09/11
 *     desc   : 请求 wanandroid 接口数据
 *     version: 1.0
 */
interface ArticleService {

    @GET("article/listproject/{page}/json")
    fun getProjectList(@Path("page") page: Int): HiCall<Pagination>


    @POST("article/query/{page}/json")
    fun search(@Filed ("k") keywords: String,
               @Path("page") page: Int):HiCall<Pagination>
}