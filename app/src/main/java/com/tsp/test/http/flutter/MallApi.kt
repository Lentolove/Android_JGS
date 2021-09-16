package com.tsp.test.http.flutter

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET


/**
 *     author : shengping.tian
 *     time   : 2021/09/15
 *     desc   :
 *     version: 1.0
 */
interface MallApi {

//
//    @GET("home/index")
//    fun getHomeData():Call<ResponseBody>
//
//    @GET("home/banner")
//    fun getHomeBanner():Call<ResponseBody>


    @GET("topic/list")
    fun getTopList(): Call<ResponseBody>
}