package com.tsp.learn.thread

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tsp.learn.R
import kotlin.math.log

/**
 * @author tsp
 * Date: 2024/2/5
 * desc:
 */
class ThreadInfoActivity : AppCompatActivity() {

    private val myHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.thread_info_activity)
        myHandler.postDelayed({
            print()
        }, 1000)
    }




    private fun print(){
        val threadInfoList = ThreadUtils.getThreadInfoList()
        Log.d("tsp---->", " thread size -----------------------------------: ${threadInfoList.size}")
        threadInfoList.forEach {
            Log.e("tsp--->", "print ==== ${it}")
        }
        myHandler.postDelayed({
            print()
        }, 300)
    }

    override fun onDestroy() {
        super.onDestroy()
        myHandler.removeCallbacksAndMessages(null)
    }
}