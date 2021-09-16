package com.tsp.android.hilibrary.restful

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   : 拦截器
 *     version: 1.0
 */
interface HiInterceptor {

    fun interceptor(chain: Chain): Boolean

    /**
     * Chain 对象会在我们派发拦截器的时候 创建
     */
    interface Chain {

        val isRequestPeriod: Boolean get() = false

        fun request(): HiRequest

        //在网络发起之前为空
        fun response(): HiResponse<*>?
    }


}