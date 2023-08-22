package com.tsp.learn.recyclerview.diff

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tsp.learn.R
import com.tsp.learn.databinding.DiffTestActivityBinding

class DiffTestActivity : AppCompatActivity() {

    private lateinit var mBinding : DiffTestActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DiffTestActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView(){

    }
}