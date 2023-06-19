package com.tsp.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tsp.learn.databinding.ActivityMainBinding
import com.tsp.learn.recyclerview.RecyclerActivity
import com.tsp.learn.viewpager.ViewPagerActivity

class MainActivity : AppCompatActivity() {

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
    }
}