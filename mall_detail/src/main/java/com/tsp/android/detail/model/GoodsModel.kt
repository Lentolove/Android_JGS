package com.tsp.android.detail.model

import android.os.Parcelable
import android.text.TextUtils
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 *     author : shengping.tian
 *     time   : 2021/09/13
 *     desc   :
 *     version: 1.0
 */

@Keep
@Parcelize
data class GoodsModel(
    val categoryId: String?,
    val completedNumText: String?,
    val createTime: String?,
    val goodsId: String?,
    val goodsName: String?,
    val groupPrice: String?,
    val hot: Boolean?,
    val joinedAvatars: List<SliderImage>?,
    val marketPrice: String?,
    val sliderImage: String?,
    val sliderImages: List<SliderImage>?,
    val tags: String?
) : Parcelable

@Keep
@Parcelize
data class SliderImage(
    val type: Int,
    val url: String
) : Parcelable

fun selectPrice(groupPrice: String?, marketPrice: String?): String? {
    var price: String? = if (TextUtils.isEmpty(marketPrice)) groupPrice else marketPrice
    if (price?.startsWith("¥") != true) {
        price = "¥".plus(price)
    }
    return price
}