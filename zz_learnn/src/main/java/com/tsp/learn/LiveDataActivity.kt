package com.tsp.learn

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tsp.android.hilibrary.utils.HiDataBus

class LiveDataActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.livedata_test_activity)
        findViewById<View>(R.id.send_btn).setOnClickListener {
            HiDataBus.with<String>("test_data").postStickyData("这是测试~")
        }
    }

}