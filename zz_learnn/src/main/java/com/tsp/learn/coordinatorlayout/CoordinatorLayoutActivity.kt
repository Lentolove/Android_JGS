package com.tsp.learn.coordinatorlayout

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsp.learn.R
import com.tsp.learn.recyclerview.DiffItemAdapter

/**
 * @author tsp
 * Date: 2024/6/13
 * desc:
 */
class CoordinatorLayoutActivity : AppCompatActivity() {

    private lateinit var rv :  RecyclerView

    private val mDatas = ArrayList<String>()


    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinator_layout)
        rv = findViewById(R.id.recyclerView)

        generateDatas()
        //线性布局
        //线性布局
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        rv.setLayoutManager(linearLayoutManager)
        val adapter = DiffItemAdapter(this, mDatas)
        rv.setAdapter(adapter)
    }

    private fun generateDatas() {
        for (i in 0..199) {
            mDatas.add("第 $i 个item")
        }
    }
}