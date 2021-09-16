package com.tsp.android.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.tsp.android.detail.DetailActivity
import com.tsp.android.test.tap.TestMainActivity
import com.tsp.android.jgs.databinding.ActivityTestBinding
import com.tsp.android.lib.home.MainActivity
import com.tsp.android.test.tap.TabTopDemoActivity
import com.tsp.android.test.refresh.RefreshDemoActivity
import com.tsp.test.http.HiRestfulActivity
import com.tsp.test.slider.SliderTestActivity
import com.tsp.test.view.ViewShowActivity

class TestActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }
    private fun initView() {
        mBinding.btnBottomTest.setOnClickListener {
            val intent = Intent(this, TestMainActivity::class.java)
            startActivity(intent)
        }
        mBinding.btnTopTest.setOnClickListener {
            val intent = Intent(this, TabTopDemoActivity::class.java)
            startActivity(intent)
        }
        mBinding.btnChildCount.setOnClickListener {
            val intent = Intent(this, RefreshDemoActivity::class.java)
            startActivity(intent)
        }
        mBinding.btnHiSlider.setOnClickListener {
            startActivity(Intent(this, SliderTestActivity::class.java))
        }
        mBinding.btnHttp.setOnClickListener {
            startActivity(Intent(this, HiRestfulActivity::class.java))
        }
        mBinding.btnView.setOnClickListener {
            startActivity(Intent(this, ViewShowActivity::class.java))

        }
        mBinding.btnDetail.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        mBinding.btnHome.setOnClickListener {
//            ARouter.getInstance().build("/activity/main").navigation()
            startActivity(Intent(this, MainActivity::class.java))

        }
    }

}