package com.tsp.test.slider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsp.test.model.Subcategory
import com.tsp.test.model.TabCategory
import kotlin.random.Random

/**
 *     author : shengping.tian
 *     time   : 2021/09/10
 *     desc   :
 *     version: 1.0
 */
class SliderViewModel : ViewModel() {

    fun queryCategoryList(): LiveData<List<TabCategory>?> {
        val tabCategoryData = MutableLiveData<List<TabCategory>?>()
        var list = ArrayList<TabCategory>()
        for (i in 1..30) {
            val category = TabCategory(
                i.toString(),
                "分类:$i",
                Random(5).nextInt().toString()
            )
            list.add(category)
        }
        tabCategoryData.postValue(list)
        return tabCategoryData
    }

    /**
     * {
    "subcategoryId": "1",
    "groupName": null,
    "categoryId": "1",
    "subcategoryName": "限时秒杀",
    "subcategoryIcon": "https://o.devio.org/images/as/images/2018-05-16/26c916947489c6b2ddd188ecdb54fd8d.png",
    "showType": "1"
    }
     * @param categoryId String
     * @return LiveData<List<Subcategory>?>
     */
    fun querySubcategoryList(categoryId: String): LiveData<List<Subcategory>?> {
        val subcategoryListData = MutableLiveData<List<Subcategory>?>()
        var list = ArrayList<Subcategory>()
        for (i in 1..50) {
            var categoryId = 0
            var groupName = ""
            var url = ""
            var showType = ""
            var subcategoryName = ""
            when (i) {
                in 1..10 -> {
                    groupName = "美食"
                    categoryId = 1
                    url = food[i % food.size]
                    showType = "1"
                    subcategoryName = "商品:$i"
                }
                in 11..18 -> {
                    groupName = "厨具用品"
                    categoryId = 2
                    url = kitchen[i % kitchen.size]
                    showType = "2"
                    subcategoryName = "商品:$i"
                }
                in 19..25 -> {
                    groupName = "洗漱用品"
                    categoryId = 3
                    url = bathrooms[i % bathrooms.size]
                    showType = "3"
                    subcategoryName = "商品:$i"
                }
                in 26..34 -> {
                    groupName = "家居用品"
                    categoryId = 4
                    url = cloth[i % cloth.size]
                    showType = "4"
                    subcategoryName = "商品:$i"
                }
                in 35..43 -> {
                    groupName = "热门游戏类"
                    categoryId = 5
                    url = game[i % game.size]
                    showType = "5"
                    subcategoryName = "商品:$i"
                }
                else -> {
                    groupName = "其他类目"
                    categoryId = 6
                    showType = "6"
                    url = other[i % other.size]
                    subcategoryName = "商品:$i"
                }
            }
            val subcategory = Subcategory(
                categoryId.toString(),
                groupName,
                showType,
                url,
                i.toString(),
                subcategoryName
            )
            list.add(subcategory)
        }
        subcategoryListData.postValue(list)
        return subcategoryListData
    }
















    private val food = listOf(
        "http://yanxuan.nosdn.127.net/3972963a4b6f9588262d2a667f4c1c73.png",
        "http://yanxuan.nosdn.127.net/418f86049f957108a31ad55cec42c349.png",
        "http://yanxuan.nosdn.127.net/80db363e0687b1a65edc6e75c1b99726.png",
        "http://yanxuan.nosdn.127.net/1e3d8f65c7c7811baccdfda6711cbfd5.png",
        "http://yanxuan.nosdn.127.net/c12cf29b574c7e9d1fcff6a57a12eea2.png",
        "http://yanxuan.nosdn.127.net/66ea1d6ad602a8e441af7cada93bdc7a.png",
        "http://yanxuan.nosdn.127.net/cfeb623929f3936cc882ffc6a9a2e927.png",
        "http://yanxuan.nosdn.127.net/4cdbf6ae196671cca154fe16e152d8d4.png",
        "http://yanxuan.nosdn.127.net/94480324b376a51af47cf92df70d1ade.png",
        "http://yanxuan.nosdn.127.net/13d58949a8c72ec914b5ef63ac726a43.png"
    )

    private val kitchen = listOf(
        "http://yanxuan.nosdn.127.net/d2db0d1d0622c621a8aa5a7c06b0fc6d.png",
        "http://yanxuan.nosdn.127.net/695ed861a63d8c0fc51a51f42a5a993b.png",
        "http://yanxuan.nosdn.127.net/3a40faaef0a52627357d98ceed7a3c45.png",
        "http://yanxuan.nosdn.127.net/ec53828a3814171079178a59fb2593da.png",
        "http://yanxuan.nosdn.127.net/04cd632e1589adcc4345e40e8ad75d2b.png",
        "http://yanxuan.nosdn.127.net/5b94463017437467a93ae4af17c2ba4f.png",
        "http://yanxuan.nosdn.127.net/be3ba4056e274e311d1c23bd2931018d.png",
        "http://yanxuan.nosdn.127.net/555afbfe05dab48c1a3b90dcaf89b4f2.png",
        "http://yanxuan.nosdn.127.net/c09d784ba592e4fadabbaef6b2e95a95.png"
    )


    private val bathrooms = listOf(
        "http://yanxuan.nosdn.127.net/c53d2dd5ba6b1cfb55bd42ea0783f051.png",
        "http://yanxuan.nosdn.127.net/729638bb13997f9c4c435b41ce6ed910.png",
        "http://yanxuan.nosdn.127.net/d6a7b9a2eb6af92d709429798a4ca3ea.png",
        "http://yanxuan.nosdn.127.net/398375d0e39574c6e87273d328316186.png",
        "http://yanxuan.nosdn.127.net/fc7764ff8e12d18f6c5881a32318ed16.png",
        "http://yanxuan.nosdn.127.net/babf6573f8acd53f21205a7577ec03e1.png",
        "http://yanxuan.nosdn.127.net/4e3aebbd7ffef5bb250d19f13cb85620.png"
    )

    private val cloth = listOf(
        "http://yanxuan.nosdn.127.net/2e2fb4f2856a021bbcd1b4c8400f2b06.png",
        "http://yanxuan.nosdn.127.net/b43ef7cececebe6292d2f7f590522e05.png",
        "http://yanxuan.nosdn.127.net/81f671bd36bce05d5f57827e5c88dd1b.png",
        "http://yanxuan.nosdn.127.net/f702dc399d14d4e1509d5ed6e57acd19.png",
        "http://yanxuan.nosdn.127.net/f702dc399d14d4e1509d5ed6e57acd19.png",
        "http://yanxuan.nosdn.127.net/d6e0e84961032fc70fd52a8d4d0fb514.png",
        "http://yanxuan.nosdn.127.net/79275db76b5865e6167b0fbd141f2d7e.png",
        "http://yanxuan.nosdn.127.net/d5d41841136182bf49c1f99f5c452dd6.png",
    )


    private val game = listOf(
        "http://yanxuan.nosdn.127.net/becfba90e8a5c95d403b8a6b9bb77825.png",
        "http://yanxuan.nosdn.127.net/b5af3f6bfcbeb459d6c448ba87f8cc35.png",
        "http://yanxuan.nosdn.127.net/a562f05bf38f5ee478fefb81856aad3d.png",
        "http://yanxuan.nosdn.127.net/1e19e948de63a1d0895a8620250c441f.png",
        "http://yanxuan.nosdn.127.net/7394ce778791ae8242013d6c974f47e0.png",
        "http://yanxuan.nosdn.127.net/ff1e28fb7151008f8dc46bbf8b357f63.png",
        "http://yanxuan.nosdn.127.net/36711325781ca50fdfe234489fca973e.png",
        "http://yanxuan.nosdn.127.net/36711325781ca50fdfe234489fca973e.png"
    )

    private val other = listOf(
        "http://yanxuan.nosdn.127.net/833476fc3ecc30a7446279b787328775.png",
        "http://yanxuan.nosdn.127.net/11d9700da759f2c962c2f6d9412ac2a1.png",
        "http://yanxuan.nosdn.127.net/2a62f6c53f4ff089fa6a210c7a0c2e63.png",
        "http://yanxuan.nosdn.127.net/fd1de05d274222f1e56d057d2f2c20c6.png",
        "http://yanxuan.nosdn.127.net/589da0f02917b8393197a43175764381.png",
        "http://yanxuan.nosdn.127.net/e074795f61a83292d0f20eb7d124e2ac.png",
        "http://yanxuan.nosdn.127.net/81e18c6970a7809ee0d86f0545428aa4.png",
        "http://yanxuan.nosdn.127.net/bbb6f0ab4f6321121250c12583b0ff9a.png",
        "http://yanxuan.nosdn.127.net/c25fb420ccb6f692a2d16f1740b60d21.png",
        "http://yanxuan.nosdn.127.net/552e943e585a999169fdbc57b59524d6.png"
    )

}