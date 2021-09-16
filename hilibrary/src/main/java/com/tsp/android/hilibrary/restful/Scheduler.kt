package com.tsp.android.hilibrary.restful

import android.util.Log
import com.tsp.android.hilibrary.cache.HiStorage
import com.tsp.android.hilibrary.executor.HiExecutor
import com.tsp.android.hilibrary.restful.anation.CacheStrategy
import com.tsp.android.hilibrary.utils.MainHandler

/**
 *     author : shengping.tian
 *     time   : 2021/09/08
 *     desc   : 代理 CallFactory 创建出来的 call 的对象，从而实现连接器的派发动作
 *     version: 1.0
 */
class Scheduler(
    private val callFactory: HiCall.Factory,
    private val interceptors: MutableList<HiInterceptor>
) {

    fun newCall(request: HiRequest): HiCall<*> {
        val newCall: HiCall<*> = callFactory.newCall(request)
        return ProxyCall(newCall, request)
    }

    /**
     * 代理 callFactory 创建的 newCall 对象进行拦截器的分发
     * @delegate 被代理的call对象，需要经过拦截器转发
     */
    internal inner class ProxyCall<T>(
        private val delegate: HiCall<T>,
        private val request: HiRequest
    ) : HiCall<T> {

        /**
         * 同步任务
         */
        override fun execute(): HiResponse<T> {
            //交给拦截器去处理
            dispatchInterceptor(request, null)
            //判断缓存策略
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                val cacheResponse = readCache<T>()
                if (cacheResponse.data != null) {
                    return cacheResponse
                }
            }
            //否则直接执行同步请求
            val response = delegate.execute()
            //存储缓存
            saveCacheIfNeed(response)
            //将response进一步分发,判断是否需要拦截
            dispatchInterceptor(request, response)
            return response
        }

        override fun enqueue(callback: HiCallback<T>) {
            dispatchInterceptor(request, null)
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST) {
                HiExecutor.execute(runnable = {
                    val cacheResponse = readCache<T>()
                    if (cacheResponse.data != null) {
                        //抛到主线程中
                        MainHandler.sendAtFrontOfQueue(runnable = {
                            callback.onSuccess(cacheResponse)
                        })
                        Log.d("Scheduler", "enqueue ,cache : ${request.getCacheKey()}")
                    }
                })
            }
            //否则去执行异步网络请求 todo 是否需要在子线程中执行
            delegate.enqueue(object : HiCallback<T> {
                override fun onSuccess(response: HiResponse<T>) {
                    dispatchInterceptor(request, response)
                    saveCacheIfNeed(response)
                    callback.onSuccess(response)
                    Log.d("Scheduler", "enqueue ,remote : ${request.getCacheKey()}")
                }

                override fun onFailed(throwable: Throwable) {
                    callback.onFailed(throwable)
                }
            })
        }

        private fun dispatchInterceptor(request: HiRequest, response: HiResponse<T>?) {
            if (interceptors.size <= 0) return
            InterceptorChain(request, response).dispatch()
        }

        /**
         * 根据缓存策略将缓存存储到数据库中
         */
        private fun saveCacheIfNeed(response: HiResponse<T>) {
            if (request.cacheStrategy == CacheStrategy.CACHE_FIRST || request.cacheStrategy == CacheStrategy.NET_CACHE) {
                if (response.data != null) {
                    HiExecutor.execute(runnable = {
                        HiStorage.saveCache(request.getCacheKey(), response.data)
                    })
                }
            }
        }

        /**
         * 读取缓存:从 historage 查询缓存 需要提供一个cache key
         *  key 为： request 的 url+参数
         */
        private fun <T> readCache(): HiResponse<T> {
            val cacheKey = request.getCacheKey()
            val cacheResponse = HiResponse<T>()
            cacheResponse.data = HiStorage.getCache(cacheKey)
            cacheResponse.code = HiResponse.CACHE_SUCCESS
            cacheResponse.msg = "获取缓存成功"
            return cacheResponse
        }

        /**
         * 拦截器派发
         */
        internal inner class InterceptorChain(
            private val request: HiRequest,
            private val response: HiResponse<T>?
        ) : HiInterceptor.Chain {

            //根据拦截器的集合遍历派发
            var callIndex: Int = 0

            override val isRequestPeriod: Boolean
                get() = response == null

            override fun request(): HiRequest {
                return request
            }

            override fun response(): HiResponse<*>? {
                return response
            }

            fun dispatch() {
                val interceptor = interceptors[callIndex]
                //是否拦截
                val intercept = interceptor.interceptor(this)
                callIndex++
                if (!intercept && callIndex < interceptors.size) {
                    //继续派发
                    dispatch()
                }
            }
        }
    }
}