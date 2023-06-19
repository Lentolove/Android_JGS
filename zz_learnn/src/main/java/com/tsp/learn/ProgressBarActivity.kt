package com.tsp.learn

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.tsp.learn.databinding.ActivityProgressBarBinding

/**
 * author: tsp
 * Date: 2023/6/17 20:35
 * Des: 进度条样式
 */
class ProgressBarActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityProgressBarBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProgressBarBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initData()
    }

    var  progressIndex = 0
    fun initData(){
        mBinding.progressBar.progress  = 1
        mBinding.progressBar2.progress  = 1
        val countDownTimer = object :CountDownTimer(100000L, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                progressIndex+=5
                mBinding.progressBar.progress  = progressIndex
                mBinding.progressBar2.progress  = progressIndex
            }

            override fun onFinish() {

            }

        }
//        countDownTimer.start()
    }

}