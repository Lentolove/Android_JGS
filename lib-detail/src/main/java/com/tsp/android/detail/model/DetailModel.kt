package com.tsp.android.detail.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 *     author : shengping.tian
 *     time   : 2021/09/13
 *     desc   : 详情页数据模型
 *     version: 1.0
 */
@Keep
@Parcelize
data class DetailModel(
    val categoryId: String,
    val commentCountTitle: String,
    val commentModels: List<CommentModel>?,
    val commentTags: List<String>?,
    val completedNumText: String,
    val createTime: String,
    val flowGoods: List<GoodsModel>?,
    val gallery: List<SliderImage>?,
    val goodAttr: List<MutableMap<String, String>>?,
    val goodDescription: String?,
    val goodsId: String,
    val goodsName: String,
    val isFavorite: Boolean,
    val groupPrice: String,
    val hot: Boolean,
    val marketPrice: String,
    val shop: Shop,
    val similarGoods: List<GoodsModel>?,
    val sliderImage: String,
    val sliderImages: List<SliderImage>?,
    val tags: String
) : Parcelable

@Keep
@Parcelize
data class CommentModel(
    val avatar: String,
    val content: String,
    val nickName: String
) : Parcelable

@Keep
@Parcelize
data class Shop(
    val completedNum: String,
    val evaluation: String,
    val goodsNum: String,
    val logo: String,
    val name: String
) : Parcelable

@Keep
data class Favorite(val goodsId: String, var isFavorite: Boolean)

/**
 * CommentItemModel 数据类型
 */
@Keep
@Parcelize
data class CommentItemModel(
    val commentCountTitle: String,
    val commentTags: List<String>?,
    val commentModels: List<CommentModel>?
) : Parcelable