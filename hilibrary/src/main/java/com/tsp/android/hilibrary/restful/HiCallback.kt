package com.tsp.android.hilibrary.restful

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   : 异步请求结果回调
 *     version: 1.0
 */
interface HiCallback<T> {

    fun onSuccess(response: HiResponse<T>)

    fun onFailed(throwable: Throwable)

}