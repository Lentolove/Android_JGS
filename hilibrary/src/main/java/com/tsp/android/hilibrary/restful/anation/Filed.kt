package com.tsp.android.hilibrary.restful.anation

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   :  @BaseUrl("https://api.devio.org/as/")
 *              fun test(@Filed("province") int provinceId)
 *     version: 1.0
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Filed(val value: String)