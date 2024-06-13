package com.tsp.learn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.DoKit
import com.tsp.learn.anim.XiuxianAnimActivity
import com.tsp.learn.coordinatorlayout.CoordinatorLayoutActivity
import com.tsp.learn.databinding.ActivityMainBinding
import com.tsp.learn.font.FontTestActivity
import com.tsp.learn.memory.MemoryActivity
import com.tsp.learn.recyclerview.RecyclerActivity
import com.tsp.learn.thread.ThreadInfoActivity
import com.tsp.learn.viewpager.ViewPagerActivity
import java.lang.Exception


class MainActivity2 : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initData()
    }

    private fun initData() {
        mBinding.btnViewpager.setOnClickListener {
            startActivity(Intent(this, ViewPagerActivity::class.java))
        }
        mBinding.btnRecyclerview.setOnClickListener {
            startActivity(Intent(this, RecyclerActivity::class.java))
        }
        mBinding.progressTest.setOnClickListener {
            startActivity(Intent(this, ProgressBarActivity::class.java))
        }
        mBinding.animTest.setOnClickListener {
            startActivity(Intent(this, XiuxianAnimActivity::class.java))
        }

        mBinding.btnMemory.setOnClickListener {
            startActivity(Intent(this, MemoryActivity::class.java))
        }
        mBinding.cameraBtn.setOnClickListener {
        }
        mBinding.autoScrollerView.apply {
        }

        mBinding.autoBtn.setOnClickListener {
            runTime()
        }

        mBinding.fontTest.setOnClickListener {
            FontTestActivity.go(this)
        }
        mBinding.threadInfo.setOnClickListener {
            startActivity(Intent(this, ThreadInfoActivity::class.java))
        }

        mBinding.coordinatorBtn.setOnClickListener {
            startActivity(Intent(this, CoordinatorLayoutActivity::class.java))
        }

    }

    private fun runTime (){
        try {
            val starTime = System.currentTimeMillis()
            while (true){
                Log.d("tsp--->", "runTime: ")
               if (System.currentTimeMillis() - starTime > 2000){
                   break
               }
            }

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}