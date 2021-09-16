package com.tsp.android.common.http

import com.tsp.android.hilibrary.restful.HiInterceptor
import com.tsp.android.hilibrary.restful.HiResponse

/**
 *     author : shengping.tian
 *     time   : 2021/09/09
 *     desc   : 为 HiRestful 的请求结果添加拦截器，根据response 的 code 自动路由到相关页面
 *             比如 RC_NEED_LOGIN
 *     version: 1.0
 */
class HttpCodeInterceptor : HiInterceptor {

    override fun interceptor(chain: HiInterceptor.Chain): Boolean {
        val response = chain.response()
        //根据response的 code 对结果进行拦截自动跳转对应的界面，比如未登陆，token过期啊
        if (!chain.isRequestPeriod && response != null) {
            when (response.code) {
                HiResponse.RC_NEED_LOGIN -> {
                    // TODO: 2021/9/9 跳转到登录界面
                }
                HiResponse.RC_AUTH_TOKEN_EXPIRED, (HiResponse.RC_AUTH_TOKEN_INVALID), HiResponse.RC_USER_FORBID -> {
                    //todo 跳转到讲解界面
                }
            }

        }
        return false
    }

}