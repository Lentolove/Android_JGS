package com.tsp.mall.search

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.tsp.android.common.base.BaseActivity
import com.tsp.android.hilibrary.utils.HiStatusBar
import com.tsp.mall.search.databinding.ActivitySearchBinding


@Route(path = "/mall/search")
class SearchActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置沉浸式状态栏
        HiStatusBar.setStatusBar(this, true, translucent = true)
        mBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}