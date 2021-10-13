package com.tsp.android.detail.model

import androidx.lifecycle.ViewModel
import com.tsp.android.detail.item.HeaderItem
import java.util.*
import kotlin.collections.HashMap

/**
 *     author : shengping.tian
 *     time   : 2021/09/13
 *     desc   :
 *     version: 1.0
 */
class DetailViewModel : ViewModel() {

    private val imageList = listOf(
        "https://wanandroid.com/blogimgs/5d362c2a-2e9e-4448-8ee4-75470c8c7533.png",
        "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png",
        "https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png",
        "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png",
    )

    private val list = listOf(
        "简单好用",
        "便宜实惠",
        "做工精良",
        "良心推荐"
    )


    fun createHeaderItem(): HeaderItem {
        return HeaderItem(
            createImageList(),
            "¥100",
            "已拼100+件",
            "耐克休闲鞋"
        )
    }

    /**
     *
     * @return DetailModel
     */
    fun createCommentItemModel(): CommentItemModel {
        val commentList = ArrayList<CommentModel>()
        for (index in 0..6) {
            val item = CommentModel(
                "https://upload-images.jianshu.io/upload_images/14979287-730129426ae612f9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240",
                "这是来自：$index 的评论",
                "我是：$index"
            )
            commentList.add(item)
        }
        val tagList = ArrayList<String>()
        for (index in 1..10) {
            val item = "${list[index % list.size]}($index)"
            tagList.add(item)
        }
        return CommentItemModel(
            "商品评价(7898)",
            tagList,
            commentList
        )
    }

    /**
     * 创建 banner 图片数组
     * @return List<String>
     */
    private fun createImageList(): List<String> {
        val list = ArrayList<String>()
        for (i in 0..7) {
            list.add(imageList[((0..4).random()) % imageList.size])
        }
        return list
    }


     fun createDetailModel(): DetailModel {
        val shop = Shop(
            "2.1万件",
            "描述相符 高 服务态度 高 物流服务 低",
            "721",
            "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg",
            "海南之家"
        )
        return DetailModel(
            "123",
            "热门评论",
            createCommentModel(),
            list,
            "已拼(1000)件",
            "2020-02-02",
            createGoodsModel(),
            createSliderImage(),
            createAttr(),
            "这是一款非常好的商品...",
            "343434",
            "耐克",
            true,
            "¥59.1",
            true,
            "43.4",
            shop,
            createGoodsModel(),
            "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg",
            createSliderImage(),
            "商品服务 高 快递速度 快 产品质量 好"
        )
    }

    private fun createAttr():List<MutableMap<String,String>>{
        val list = ArrayList<MutableMap<String,String>>()
        for (i in 1..7){
            val item1 = HashMap<String,String>()
            item1["描述"] = "商品详情：$i "
            list.add(item1)
        }
        return list
    }


    fun createCommentModel(): List<CommentModel> {
        var list = ArrayList<CommentModel>()
        for (i in 1..6) {
            val commentModel =
                CommentModel(
                    "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg",
                    "这个东西很好$i",
                    "小琪琪$i"
                )
            list.add(commentModel)
        }
        return list
    }

    fun createGoodsModel(): List<GoodsModel> {
        val list = ArrayList<GoodsModel>()
        for (i in 1..10) {
            val goodsModel = GoodsModel(
                "123:$i",
                "已拼2001+",
                "2021-08-02",
                "73437123:$i",
                "耐克休闲$i",
                "¥59.$i",
                true,
                createSliderImage(),
                "50.$i",
                "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg",
                createSliderImage(),
                "简单好用 便宜实惠 做工精良 良心推荐"
            )
            list.add(goodsModel)
        }
        return list
    }

    fun createSliderImage(): List<SliderImage> {
        val list = ArrayList<SliderImage>()
        for (i in 1..10) {
            val sliderImage = SliderImage(
                i,
                "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg"
            )
            list.add(sliderImage)
        }
        return list
    }
}