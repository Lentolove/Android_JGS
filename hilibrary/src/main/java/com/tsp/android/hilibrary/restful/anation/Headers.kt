package com.tsp.android.hilibrary.restful.anation

/**
 * @Headers({"connection:keep-alive","auth-token:token"})
 *fun test(@Filed("province") int provinceId)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Headers(vararg val value: String)