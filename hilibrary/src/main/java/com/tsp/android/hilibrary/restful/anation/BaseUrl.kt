package com.tsp.android.hilibrary.restful.anation

/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   : 域名地址
 *     version: 1.0
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseUrl(val value: String)