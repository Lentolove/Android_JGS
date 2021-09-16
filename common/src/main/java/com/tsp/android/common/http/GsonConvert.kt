package com.tsp.android.common.http

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsp.android.hilibrary.restful.HiConvert
import com.tsp.android.hilibrary.restful.HiResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type

/**
 *     author : shengping.tian
 *     time   : 2021/09/11
 *     desc   : GsonConvert 转换器
 *     version: 1.0
 */
class GsonConvert:HiConvert {

    private val gson = Gson()

    override fun <T> convert(rawData: String, dataType: Type): HiResponse<T> {
        val response:HiResponse<T> = HiResponse()
        try {
            val jsonObject = JSONObject(rawData)
            response.code = jsonObject.optInt("errorCode")
            response.msg = jsonObject.optString("errorMsg")
            val data = jsonObject.opt("data")
            if ((data is JSONObject) or (data is JSONArray)) {
                if (response.code == HiResponse.SUCCESS) {
                    response.data = gson.fromJson(data.toString(), dataType)
                } else {
                    response.errorData = gson.fromJson<MutableMap<String, String>>(
                        data.toString(),
                        object : TypeToken<MutableMap<String, String>>() {}.type
                    )
                }
            } else {
                response.data = data as? T
            }
        }catch (e:JSONException){
            e.printStackTrace()
            response.code = -1
            response.msg = e.message
        }
        response.rawData = rawData
        return response
    }

}