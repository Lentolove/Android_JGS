package com.tsp.test.http.flutter

import retrofit2.Retrofit

/**
 *     author : shengping.tian
 *     time   : 2021/09/15
 *     desc   :
 *     version: 1.0
 */
object MallFactory {

    const val BASE_URL = "http://10.0.2.2:8080/wx/"


    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()


}