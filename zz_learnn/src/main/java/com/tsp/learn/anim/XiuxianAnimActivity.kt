package com.tsp.learn.anim

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tsp.learn.databinding.XiuxianAnimActivityBinding

/**
 * @author tsp
 * Date: 2023/11/18
 * desc:
 */
class XiuxianAnimActivity : AppCompatActivity() {


    private lateinit var mBinding : XiuxianAnimActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = XiuxianAnimActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.startBtn1.setOnClickListener {
            mBinding.xiuxianView.startHideAnim()
        }
        mBinding.pkStart.setOnClickListener {
            mBinding.xiuxianView.startPkAnim()
        }
    }

}