package com.tsp.android.common.http

import com.tsp.android.hilibrary.restful.HiCall
import com.tsp.android.hilibrary.restful.HiCallback
import com.tsp.android.hilibrary.restful.HiRequest
import com.tsp.android.hilibrary.restful.HiResponse
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.lang.IllegalStateException

/**
 *     author : shengping.tian
 *     time   : 2021/09/11
 *     desc   : 为 HiRestful 添加 retrofit 适配
 *     version: 1.0
 */
class RetrofitCallFactory(private val baseUrl: String) : HiCall.Factory {

    private var hiConvert: GsonConvert
    private var apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()
        apiService = retrofit.create(ApiService::class.java)
        hiConvert = GsonConvert()
    }

    override fun newCall(request: HiRequest): HiCall<Any> {
        return RetrofitCall(request)
    }

    inner class RetrofitCall<T>(private val request: HiRequest) : HiCall<T> {

        override fun execute(): HiResponse<T> {
            val realCall: Call<ResponseBody> = createRealCall(request)
            val response: Response<ResponseBody> = realCall.execute()
            return parseResponse(response)
        }

        override fun enqueue(callback: HiCallback<T>) {
            val realCall: Call<ResponseBody> = createRealCall(request)
            realCall.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callback.onFailed(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val response: HiResponse<T> = parseResponse(response)
                    callback.onSuccess(response)
                }
            })
        }

        /**
         * 根据自定义的 http 请求类型，由 retrofit 来真正实现
         * @param request HiRequest
         * @return Call<ResponseBody> 返回 retrofit 的 call 对象
         */
        private fun createRealCall(request: HiRequest): Call<ResponseBody> {
            when (request.httpMethod) {
                HiRequest.METHOD.GET -> {
                    return apiService.get(
                        request.headers,
                        request.endPointUrl(),
                        request.parameters
                    )
                }
                HiRequest.METHOD.POST -> {
                    val requestBody: RequestBody = buildRequestBody(request)
                    return apiService.post(request.headers, request.endPointUrl(), requestBody)
                }
                HiRequest.METHOD.PUT -> {
                    val requestBody: RequestBody = buildRequestBody(request)
                    return apiService.put(request.headers, request.endPointUrl(), requestBody)
                }
                HiRequest.METHOD.DELETE -> {
                    return apiService.delete(request.headers, request.endPointUrl())
                }
                else -> {
                    throw IllegalStateException("hirestful only support GET POST for now ,url= ${request.endPointUrl()}")
                }
            }
        }

        /**
         * 将 retrofit 返回的 response 转换成我们自定义的Response
         * @param response  retrofit 原生数据
         * @return HiResponse<T> 返回自定义的 response
         */
        private fun parseResponse(response: Response<ResponseBody>): HiResponse<T> {
            var rawData: String? = null
            if (response.isSuccessful) {
                val body: ResponseBody? = response.body()
                if (body != null) {
                    rawData = body.string()
                }
            } else {
                val body: ResponseBody? = response.errorBody()
                if (body != null) {
                    rawData = body.string()
                }
            }
            return hiConvert.convert(rawData!!, request.returnType!!)
        }

        /**
         * 构建 RequestBody
         * @param request
         * @return RequestBody
         */
        private fun buildRequestBody(request: HiRequest): RequestBody {
            val parameters: MutableMap<String, String>? = request.parameters
            val builder = FormBody.Builder()
            val requestBody: RequestBody
            val jsonObject = JSONObject()
            if (parameters != null) {
                for ((key, value) in parameters) {
                    if (request.formPost) {
                        builder.add(key, value)
                    } else {
                        jsonObject.put(key, value)
                    }
                }
            }
            requestBody = if (request.formPost) {
                builder.build()
            } else {
                //bugfix: reuqest-header   content-type="application/json; charset=utf-8"
                RequestBody.create(
                    MediaType.parse("application/json;charset=utf-8"),
                    jsonObject.toString()
                )
            }
            return requestBody
        }
    }

    interface ApiService {

        @GET
        fun get(
            @HeaderMap headers: MutableMap<String, String>?, @Url url: String,
            @QueryMap(encoded = true) params: MutableMap<String, String>?
        ): Call<ResponseBody>

        @POST
        fun post(
            @HeaderMap headers: MutableMap<String, String>?, @Url url: String,
            @Body body: RequestBody?
        ): Call<ResponseBody>

        @PUT
        fun put(
            @HeaderMap headers: MutableMap<String, String>?,
            @Url url: String,
            @Body body: RequestBody?
        ): Call<ResponseBody>

        //不可以携带requestBody
        @DELETE
        fun delete(
            @HeaderMap headers: MutableMap<String, String>?,
            @Url url: String
        ): Call<ResponseBody>
    }
}