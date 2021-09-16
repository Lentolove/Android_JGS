package com.tsp.android.hilibrary.restful

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

/**
 *     author : shengping.tian
 *     time   : 2021/09/08
 *     desc   : 创建 HiRestful 接口实现代理类
 *     version: 1.0
 */
class HiRestful constructor(private val baseUrl: String, callFactory: HiCall.Factory) {
    private val interceptors = mutableListOf<HiInterceptor>()
    private val methodServiceCache: ConcurrentHashMap<Method, MethodParse> = ConcurrentHashMap()
    private var scheduler: Scheduler = Scheduler(callFactory, interceptors)

    fun addInterceptors(interceptor: HiInterceptor) {
        interceptors.add(interceptor)
    }

    /**
     * interface ApiService {
     *  @Headers("auth-token:token", "accountId:123456")
     *  @BaseUrl("https://api.devio.org/as/")
     *  @POST("/cities/{province}")
     *  @GET("/cities")
     * fun listCities(@Path("province") province: Int,@Filed("page") page: Int): HiCall<JsonObject>
     * }
     */
    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service)
        ) { proxy, method, args -> //先从缓存中获取
            var methodParse = methodServiceCache[method]
            if (methodParse == null) {
                methodParse = MethodParse.parse(baseUrl, method, args)
                methodServiceCache[method] = methodParse
            }
            //bugFix：此处 应当考虑到 methodParser复用，每次调用都应当解析入参
            val request = methodParse.newRequest(method, args)
            scheduler.newCall(request)
        } as T
    }

}