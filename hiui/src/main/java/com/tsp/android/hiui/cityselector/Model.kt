package com.tsp.android.hiui.cityselector

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

const val TYPE_PROVINCE = "1"
const val TYPE_CITY = "2"
const val TYPE_DISTRICT = "3"

/**
 * 城市选择器数据模型
 * @property cities ArrayList<City>
 * @property selectCity City? //
 * @property selectDistrict District?
 */
@Parcelize
open class Province : District(), Parcelable, Serializable {
    //该省份下面所有的市
    val cities = ArrayList<City>()

    //选择的市
    var selectCity: City? = null

    //选择的区
    var selectDistrict: District? = null


}

@Parcelize
open class City : District(), Parcelable, Serializable {
    var districts = ArrayList<District>()
}

//城市的类型，type .0是国，1是省，2是市，3是区
//districtName 地区名
//pid 父级id
@Parcelize
open class District : Parcelable, Serializable {
    var districtName: String? = null
    var id: String? = null
    var pid: String? = null
    var type: String? = null
}

