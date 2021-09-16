package com.tsp.android.hilibrary.restful.anation

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   : DELETE 请求
 *     version: 1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DELETE(val value: String)