package com.tsp.android.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 *     author : shengping.tian
 *     time   : 2021/09/16
 *     desc   : BaseActivity 基类
 *     version: 1.0
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}