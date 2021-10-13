package com.tsp.android.lib.home

import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.tsp.android.common.tab.TabViewAdapter
import com.tsp.android.hilibrary.utils.HiViewUtil
import com.tsp.android.hiui.tab.bottom.HiTabBottomInfo
import com.tsp.android.lib.home.category.CategoryFragment
import com.tsp.android.lib.home.databinding.ActivityMainBinding
import com.tsp.android.lib.home.home.HomeFragment
import com.tsp.android.lib.home.mine.MineFragment
import com.tsp.android.lib.home.shop.ShopFragment
import java.util.*

/**
 *     author : shengping.tian
 *     time   : 2021/09/16
 *     desc   : 将 HomeActivity 的一些逻辑内聚在这里,让 HomeActivity 更加清爽
 *     version: 1.0
 */
class MainActivityLogic(
    private val binding: ActivityMainBinding,
    private val fragmentManager: FragmentManager
) {

    private val mContext = binding.root.context

    private val homeContainer = binding.homeContainer

    private var infoList = ArrayList<HiTabBottomInfo<*>>()

    private var mCurrentItemIndex = 0

    /**
     * 构建 tabBottom 首页 分类 购物车 我的
     */
    fun initTabBottom() {
        val homeTabBottomLayout = binding.homeTabBottomLayout
        val defaultColor = mContext.resources.getColor(R.color.tabBottomDefaultColor)
        val tintColor = mContext.resources.getColor(R.color.tabBottomTintColor)
        homeTabBottomLayout.setTabAlpha(if (HiViewUtil.lightMode()) 0.85f else 0f)

        val homeInfo = HiTabBottomInfo(
            "首页",
            "fonts/iconfont.ttf",
            mContext.getString(R.string.if_home),
            null,
            defaultColor,
            tintColor
        )
        homeInfo.fragment = HomeFragment::class.java


        val category = HiTabBottomInfo(
            "分类",
            "fonts/iconfont.ttf",
            mContext.getString(R.string.if_category),
            null,
            defaultColor,
            tintColor
        )
        category.fragment = CategoryFragment::class.java


        val shop = HiTabBottomInfo(
            "购物车",
            "fonts/iconfont.ttf",
            mContext.getString(R.string.if_favorite),
            null,
            defaultColor,
            tintColor
        )
        shop.fragment = ShopFragment::class.java


        val mine = HiTabBottomInfo(
            "我的",
            "fonts/iconfont.ttf",
            mContext.getString(R.string.if_profile),
            null,
            defaultColor,
            tintColor
        )
        mine.fragment = MineFragment::class.java

        infoList.apply {
            add(homeInfo)
            add(category)
            add(shop)
            add(mine)
        }
        homeTabBottomLayout.inflateInfo(infoList)

        initFragmentTabView()
        homeTabBottomLayout.addTabSelectedChangeListener { index, prevInfo, nextInfo ->
            this.mCurrentItemIndex = index
            homeContainer.setCurrentItem(index)
        }
        homeTabBottomLayout.defaultSelected(infoList[mCurrentItemIndex])

    }

    /**
     * 初始化 fragment
     */
    private fun initFragmentTabView() {
        val adapter = TabViewAdapter(infoList, fragmentManager)
        homeContainer.adapter = adapter
    }

}