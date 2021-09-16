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

    private val url = listOf<String>(
        "https://o.devio.org/images/as/goods/images/2018-12-21/5c3672e33377b65d5f1bef488686462b.jpeg",
        "https://o.devio.org/images/as/goods/images/2018-12-21/117a40a6d63c5bac590080733512b89d.jpeg",
        "https://o.devio.org/images/as/goods/images/2018-12-21/7d4449179b509531414365460d80a87d.jpeg",
    )

    fun queryCategoryList(): LiveData<List<TabCategory>?> {
        val tabCategoryData = MutableLiveData<List<TabCategory>?>()
        var list = ArrayList<TabCategory>()
        for (i in 1..30) {
            val category = TabCategory(
                i.toString(),
                "热门:$i",
                Random(5).nextInt().toString()
            )
            list.add(category)
        }
        tabCategoryData.postValue(list)
        return tabCategoryData
    }

    /**
     * data class Subcategory(
    val categoryId: String,
    val groupName: String,
    val showType: String,
    val subcategoryIcon: String,
    val subcategoryId: String,
    val subcategoryName: String
    ) : Serializable
     * @param categoryId String
     * @return LiveData<List<Subcategory>?>
     */
    fun querySubcategoryList(categoryId: String): LiveData<List<Subcategory>?> {
        val subcategoryListData = MutableLiveData<List<Subcategory>?>()
        var list = ArrayList<Subcategory>()
        for (i in 1..30) {
            var categoryId = 0
            var groupName = ""
            when (i) {
                in 1..5 -> {
                    groupName = "热门商品"
                    categoryId = 1
                }
                in 6..10 -> {
                    groupName =  "限时秒杀"
                    categoryId = 2
                }
                in 11..16 -> {
                    groupName =  "时尚潮流"
                    categoryId = 3
                }
                in 17..20 -> {
                    groupName =  "手机分类"
                    categoryId = 4
                }
                in 21..25 -> {
                    groupName = "电脑分类"
                    categoryId = 5
                }
                else -> {
                    groupName =  "耳机分类"
                    categoryId = 6
                }
            }
            val subcategory = Subcategory(
                categoryId.toString(),
                groupName,
                "goods",
                url[i % 3],
                i.toString(),
                "GOOD = :$i"
            )
            list.add(subcategory)
        }
        subcategoryListData.postValue(list)
        return subcategoryListData
    }
}