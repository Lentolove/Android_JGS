package com.tsp.android.test.tap

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.android.hiui.tab.bottom.HiTabBottomInfo
import com.tsp.android.hiui.tab.bottom.HiTabBottomLayout
import com.tsp.android.jgs.R

class TestMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)

        val hiBottomLayout = findViewById<HiTabBottomLayout>(R.id.hi_bottom_layout);
        hiBottomLayout.setTabAlpha(0.85f)
        val list: MutableList<HiTabBottomInfo<Int>> = ArrayList()

        val defaultColor: Int = resources.getColor(R.color.tabBottomDefaultColor)
        val tintColor: Int = resources.getColor(R.color.tabBottomTintColor)
        val homeInfo = HiTabBottomInfo(
            "首页",
            "fonts/iconfont.ttf",
            getString(R.string.if_home),
            null,
            defaultColor,
            tintColor
        )
        val infoFavorite = HiTabBottomInfo(
            "收藏",
            "fonts/iconfont.ttf",
            getString(R.string.if_favorite),
            null,
            defaultColor,
            tintColor
        )
//        val infoCategory = HiTabBottomInfo(
//            "分类",
//            "fonts/iconfont.ttf",
//            getString(R.string.if_category),
//            null,
//            defaultColor,
//            tintColor
//        )

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.fire, null)

        val infoCategory = HiTabBottomInfo<Int>("分类", bitmap, bitmap)

        val infoRecommend = HiTabBottomInfo(
            "推荐",
            "fonts/iconfont.ttf",
            getString(R.string.if_recommend),
            null,
            defaultColor,
            tintColor
        )
        val infoProfile = HiTabBottomInfo(
            "我的",
            "fonts/iconfont.ttf",
            getString(R.string.if_profile),
            null,
            defaultColor,
            tintColor
        )
        list.add(homeInfo)
        list.add(infoFavorite)
        list.add(infoCategory)
        list.add(infoRecommend)
        list.add(infoProfile)
        hiBottomLayout.inflateInfo(list as List<HiTabBottomInfo<*>>)
        hiBottomLayout.addTabSelectedChangeListener { index, prevInfo, nextInfo ->
            Toast.makeText(this,"点击了${index}",Toast.LENGTH_SHORT).show()
        }
        hiBottomLayout.defaultSelected(homeInfo)
        //        改变某个tab的高度
        val tabBottom = hiBottomLayout.findTab(list[2])
        tabBottom?.apply { resetHeight(HiDisplayUtil.dp2px(66f, resources)) }
    }
}