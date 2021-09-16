package com.tsp.android.hilibrary.restful

import android.util.Log
import androidx.annotation.IntDef
import com.tsp.android.hilibrary.log.HiLog
import com.tsp.android.hilibrary.restful.anation.CacheStrategy
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.net.URLEncoder

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   : 请求类
 *     version: 1.0
 */
open class HiRequest {

    companion object{
        const val TAG = "HiRequest"
    }

    @IntDef(value = [METHOD.GET, METHOD.POST])
    annotation class METHOD {
        companion object {
            const val GET = 0
            const val POST = 1
            const val PUT = 2
            const val DELETE = 3
        }
    }

    //请求缓存的 key
    private var cacheStrategyKe: String = ""

    //缓存策略
    var cacheStrategy: Int = CacheStrategy.NET_ONLY

    //请求方法
    @METHOD
    var httpMethod: Int = 0

    //请求头
    var headers: MutableMap<String, String>? = null

    //请求体的参数
    var parameters: MutableMap<String, String>? = null

    //base uri
    var domainUrl: String? = null

    //相对路径
    var relativeUrl: String? = null

    //返回类型
    var returnType: Type? = null

    //是否表单提交
    var formPost: Boolean = false


    //返回的是请求的完整的url
    /**
     * //scheme-host-port:443
    //https://api.devio.org/v1/ ---relativeUrl: user/login===>https://api.devio.org/v1/user/login
    //可能存在别的域名的场景
    //https://api.devio.org/v2/
    //https://api.devio.org/v1/ ---relativeUrl: /v2/user/login===>https://api.devio.org/v2/user/login
     */
    fun endPointUrl(): String {
        if (relativeUrl == null) throw IllegalStateException("relative url must not be null")
        //默认baseurl以 / 结尾, 相对路径中不以 / 开头的话就直接添加
        if (!relativeUrl!!.startsWith("/")) {
            return domainUrl + relativeUrl;
        }
        //相对路径中以 / 开头的话
        val indexOf = domainUrl!!.indexOf("/")
        return domainUrl!!.substring(0, indexOf) + relativeUrl
    }

    /**
     * 添加请求头
     */
    fun addHeader(name: String, value: String) {
        if (headers == null) {
            headers = mutableMapOf()
        }
        headers!![name] = value
    }

    /**
     * 将 url 和 请求参数作为 key
     */
    fun getCacheKey(): String {
        if (cacheStrategyKe.isNotEmpty()) return cacheStrategyKe
        val builder = StringBuilder()
        val endUrl = endPointUrl()
        builder.append(endUrl)
        if (endUrl.indexOf("?") > 0 || endUrl.indexOf("&") > 0) {
            builder.append("&")
        } else {
            builder.append("?")
        }
        cacheStrategyKe = if (parameters != null) {
            for ((key, value) in parameters!!) {
                try {
                    val encodeValue = URLEncoder.encode(value, "UTF-8")
                    builder.append(key).append("=").append(encodeValue).append("&")
                } catch (e: Exception) {
                    Log.e(TAG,"getCacheKey error $e")
                }
            }
            builder.deleteCharAt(builder.length - 1)
            builder.toString()
        } else {
            endUrl
        }
        return cacheStrategyKe
    }
}