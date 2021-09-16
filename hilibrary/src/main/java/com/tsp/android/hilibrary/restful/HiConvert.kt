package com.tsp.android.hilibrary.restful

import java.lang.reflect.Type
/**
 *     author : shengping.tian
 *     time   : 2021/07/22
 *     desc   : 数据格式转换
 *     version: 1.0
 */
interface HiConvert {
    fun <T> convert(rawData: String, dataType: Type):HiResponse<T>
}