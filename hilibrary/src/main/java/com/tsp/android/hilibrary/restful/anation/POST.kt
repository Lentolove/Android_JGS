package com.tsp.android.hilibrary.restful.anation

/**
 * @POST("/cities/{province}")
 *fun test(@Path("province") int provinceId)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class POST(val value: String, val formPost: Boolean = true)