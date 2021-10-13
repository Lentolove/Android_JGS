package com.tsp.android.lib.home

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.tsp.android.common.base.BaseActivity
import com.tsp.android.lib.home.databinding.ActivityMainBinding

/**
 *     author : shengping.tian
 *     time   : 2021/09/16
 *     desc   : 首页
 *     version: 1.0
 */

@Route(path = "/activity/main")
class MainActivity : BaseActivity() {

    private lateinit var mMainLogic: MainActivityLogic

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ARouter.getInstance().inject(this)

        mMainLogic = MainActivityLogic(mBinding, supportFragmentManager)
        mMainLogic.initTabBottom()
    }


}