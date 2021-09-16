package com.tsp.android.hilibrary.restful

import java.io.IOException
import kotlin.jvm.Throws

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   : 请求接口统一回调
 *     version: 1.0
 */
interface HiCall<T> {

    /**
     * 同步请求
     */
    @Throws(IOException::class)
    fun execute(): HiResponse<T>

    /**
     * 异步请求
     */
    fun enqueue(callback: HiCallback<T>)

    /**
     * 创建真实的 call 请求数据
     */
    interface Factory {
        fun newCall(request: HiRequest): HiCall<*>
    }
}