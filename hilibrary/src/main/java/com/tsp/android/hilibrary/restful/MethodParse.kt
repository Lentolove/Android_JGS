package com.tsp.android.hilibrary.restful

import com.tsp.android.hilibrary.restful.anation.*
import java.lang.reflect.*
import kotlin.IllegalStateException

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   :
 *     version: 1.0
 */
class MethodParse(private val baseUrl: String, method: Method, args: Array<out Any>?) {

    private var replaceRelativeUrl: String? = null
    private var cacheStrategy: Int = CacheStrategy.NET_ONLY
    private var domainUrl: String? = null
    private var formPost: Boolean = true
    private var httpMethod: Int = -1
    private lateinit var relativeUrl: String
    private lateinit var returnType: Type
    private var headers: MutableMap<String, String> = mutableMapOf()
    private var parameters: MutableMap<String, String> = mutableMapOf()

    init {
        //parse method annotations such as get,headers,post baseUrl
        parseMethodAnnotations(method)

        //parse method generic return type
        parseMethodReturnType(method)

        //parse method parameters such as path,filed
        parseMethodParameters(method, args)
    }

    /**
     * 解析方法上的注解
     */
    private fun parseMethodAnnotations(method: Method) {
        val annotations = method.annotations
        for (annotation in annotations) {
            when (annotation) {
                is GET -> {
                    relativeUrl = annotation.value
                    httpMethod = HiRequest.METHOD.GET
                }
                is POST -> {
                    relativeUrl = annotation.value
                    httpMethod = HiRequest.METHOD.POST
                }
                is PUT -> {
                    formPost = annotation.formPost
                    httpMethod = HiRequest.METHOD.PUT
                    relativeUrl = annotation.value
                }
                is DELETE -> {
                    httpMethod = HiRequest.METHOD.DELETE
                    relativeUrl = annotation.value
                }
                is Headers -> {
                    //解析头节点参数 @Headers({"connection:keep-alive","auth-token:token"})
                    val headersArray = annotation.value
                    for (header in headersArray) {
                        val colIndex = header.indexOf(":")
                        check(!(colIndex == 0 || colIndex == -1)) {
                            "@headers value must be in the form [name:value] ,but found $header"
                        }
                        val name = header.substring(0, colIndex)
                        val value = header.substring(colIndex + 1).trim()
                        headers[name] = value
                    }
                }
                is BaseUrl -> {
                    domainUrl = annotation.value
                }
                is CacheStrategy -> {
                    cacheStrategy = annotation.value
                }
                else -> {
                    throw IllegalStateException("cannot handle method annotation: ${annotation.javaClass}")
                }
            }
        }
        require(
            (httpMethod == HiRequest.METHOD.GET)
                    || (httpMethod == HiRequest.METHOD.POST
                    || (httpMethod == HiRequest.METHOD.PUT)
                    || (httpMethod == HiRequest.METHOD.DELETE))
        ) { "method %s must has one of GET,POST,PUT,DELETE ${method.name}" }
        if (domainUrl == null) {
            domainUrl = baseUrl
        }
    }

    /**
     * 解析方法中参数注解
     * @Path("province") province: Int,@Filed("page") page: Int
     */
    private fun parseMethodParameters(method: Method, args: Array<out Any>?) {
        if (args.isNullOrEmpty()) return
        //每次调用api接口时  应该吧上一次解析到的参数清理掉，因为methodParser存在复用
        parameters.clear()
        val parameterAnnotations = method.parameterAnnotations
        val equals = parameterAnnotations.size == args.size
        require(equals) {
            "arguments annotations count ${parameterAnnotations.size} don't match expect count ${args.size}"
        }
        for (index in args.indices) {
            //获取注解
            val annotations = parameterAnnotations[index]
            require(annotations.size <= 1) {
                "filed can only has one annotation :index =$index"
            }
            //获取方法入参 province: Int
            val value = args[index]
            //目前只支持 8 中基本类型的参数
            require(isPrimitive(value)) { "8 basic types are supported for now,index=$index" }
            val annotation = annotations[0]
            if (annotation is Filed) {
                val key = annotation.value
                val value = args[index]
                parameters[key] = value.toString()
            } else if (annotation is Path) {
                val replaceName = annotation.value
                val replacement = value.toString()
                //relativeUrl = home/{categroyId}
                if (replaceName != null && replaceName != null) {
                    replaceRelativeUrl = relativeUrl.replace("{$replaceName}", replacement)
                }
            } else if (annotation is CacheStrategy) {
                cacheStrategy = value as Int
            } else {
                throw  IllegalStateException("cannot handle parameter annotation :${annotation.javaClass}")
            }
        }
    }

    /**
     * 解析返回值类型
     * interface ApiService {
     *  @Headers("auth-token:token", "accountId:123456")
     *  @BaseUrl("https://api.devio.org/as/")
     *  @POST("/cities/{province}")
     *  @GET("/cities")
     * fun listCities(@Path("province") province: Int,@Filed("page") page: Int): HiCall<JsonObject>
     * }
     */
    private fun parseMethodReturnType(method: Method) {
        if (method.returnType != HiCall::class.java) {
            throw IllegalStateException("method ${method.name} must be type of HiCall.class")
        }
        //获取返回值类型
        val genericReturnType = method.genericReturnType
        if (genericReturnType is ParameterizedType) {
            val actualTypeArguments = genericReturnType.actualTypeArguments
            require(actualTypeArguments.size == 1) { "method ${method.name} can only has one generic return type" }
            val type = actualTypeArguments[0]
            require(validateGenericType(type)) { "method ${method.name} generic return type must not be an unknown type. " }
            returnType = type
        } else {
            throw  IllegalStateException("method ${method.name} must has one generic return type")
        }
    }

    /**
     * 检查泛型参数的合理性
     *  wrong
     *  fun test():HiCall<Any>
     *  fun test():HiCall<List<*>>
     *  fun test():HiCall<ApiInterface>
     *  expect
     *  fun test():HiCall<User>
     */
    private fun validateGenericType(type: Type): Boolean {
        //1.如果指定泛型是集合类型，则还需要继续检查集合中的泛型类型
        if (type is GenericArrayType) {
            return validateGenericType(type.genericComponentType)
        }
        //如果指定的泛型是一个接口，也不行
        if (type is TypeVariable<*>) return false
        //如果指定的泛型是一个通配符 ？extends Number 也不行
        if (type is WildcardType) return false
        return true
    }

    /**
     * 判断是否是基本数据类型
     */
    private fun isPrimitive(value: Any): Boolean {
        if (value.javaClass == String::class.java) return true
        //通过反射获取当前数据的基本类型
        try {
            //int byte short long boolean char double float
            val field = value.javaClass.getField("TYPE")
            val clazz = field[null] as Class<*>
            if (clazz.isPrimitive) return true
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 从缓存中获取的方法可能入参被改变了
     */
    fun newRequest(method: Method, args: Array<out Any>?):HiRequest {
        val arguments: Array<Any> = args as Array<Any>? ?: arrayOf()
        parseMethodParameters(method, arguments)
        val request = HiRequest()
        request.domainUrl = domainUrl
        request.returnType = returnType
        request.relativeUrl = replaceRelativeUrl ?: relativeUrl
        request.parameters = parameters
        request.headers = headers
        request.httpMethod = httpMethod
        request.formPost = formPost
        request.cacheStrategy = cacheStrategy
        return request
    }

    companion object {
        fun parse(baseUrl: String, method: Method, args: Array<out Any>?): MethodParse {
            return MethodParse(baseUrl, method, args)
        }
    }
}