package com.tsp.android.test.refresh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.hiui.refresh.HiRefresh
import com.tsp.android.hiui.refresh.HiRefreshLayout
import com.tsp.android.hiui.refresh.HiTextOverView
import com.tsp.android.jgs.R

class RefreshDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refresh_demo)

        val refreshLayout = findViewById<HiRefreshLayout>(R.id.hiRefresh)
        val xOverView = HiTextOverView(this)

//        val lottieOverView = HiLottieOverView(this)

        refreshLayout.setRefreshOverView(xOverView)
        refreshLayout.setRefreshListener(object : HiRefresh.HiRefreshListener {
            override fun onRefresh() {
                Handler(Looper.getMainLooper()).postDelayed({
                    refreshLayout.refreshFinished()
                }, 1000)
            }

            override fun enableRefresh(): Boolean {
                return true
            }
        })
        refreshLayout.setDisableRefreshScroll(false)

        initView()
    }

    private fun initView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val data = arrayOf("RefreshItem", "RefreshItem", "RefreshItem", "RefreshItem", "RefreshItem", "RefreshItem", "RefreshItem")
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val mAdapter = MyAdapter(data)
        recyclerView.adapter = mAdapter
    }


    class MyAdapter(private val mDataset: Array<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            // each data item is just a string in this case
            var textView: TextView

            init {
                textView = v.findViewById(R.id.tv_title)
            }
        }

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): MyViewHolder {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = mDataset[position]
            holder.itemView.setOnClickListener {
                Log.d("MyAdapter","position:$position")
            }
        }

        override fun getItemCount(): Int {
            return mDataset.size
        }

    }
}