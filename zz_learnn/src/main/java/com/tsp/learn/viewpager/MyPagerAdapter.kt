package com.tsp.learn.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

/**
 *     author : shengping.tian
 *     time   : 2021/10/13
 *     desc   : ViewPager 视图切换
 *     version: 1.0
 */
class MyPagerAdapter constructor(
    fragmentManager: FragmentManager,
    val list: List<Fragment>
) :

    FragmentPagerAdapter(fragmentManager) {

    private val titleList = ArrayList<String>()

    init {
        for (i in 0..list.size) {
            titleList.add("标题：$i")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

}