package com.tsp.android.common.http

import com.tsp.android.hilibrary.restful.HiInterceptor

/**
 *     author : shengping.tian
 *     time   : 2021/09/09
 *     desc   : 根据自己的业务逻辑接口，添加一些必要的请求头等，并对 response 对一些处理
 *     version: 1.0
 */
class BizInterceptor:HiInterceptor {

    override fun interceptor(chain: HiInterceptor.Chain): Boolean {

        return false
    }
}