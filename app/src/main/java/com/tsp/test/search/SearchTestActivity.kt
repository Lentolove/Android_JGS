package com.tsp.test.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.tsp.android.jgs.R
import com.tsp.android.jgs.databinding.ActivitySearchTestBinding
import com.tsp.android.jgs.databinding.ActivityTestBinding

/**
 *     author : shengping.tian
 *     time   : 2021/09/17
 *     desc   :
 *     version: 1.0
 */

@Route(path = "/mall/search")
class SearchTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        val binding = ActivitySearchTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.searchView.setClearIconClickListener {


        }
    }
}