package com.tsp.android.common.http

import com.tsp.android.hilibrary.restful.HiRestful

/**
 *     author : shengping.tian
 *     time   : 2021/09/11
 *     desc   :
 *     version: 1.0
 */
object ApiFactory {

//    private const val baseUrl = "https://www.wanandroid.com/"


    private const val baseUrl = "http://127.0.0.1:8080/wx/"


    private val hiResult: HiRestful = HiRestful(baseUrl, RetrofitCallFactory(baseUrl))

    fun <T> create(service: Class<T>): T {
        return hiResult.create(service)
    }

}