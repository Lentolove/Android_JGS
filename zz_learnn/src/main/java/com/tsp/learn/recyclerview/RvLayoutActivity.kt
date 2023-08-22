package com.tsp.learn.recyclerview

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.learn.R

class RvLayoutActivity : AppCompatActivity() {

    val TAG = "TSP-->"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_layout_activity)
        initRv()
    }


    private fun initRv(){
        val recyclerView = findViewById<RecyclerView>(R.id.first_layout)

        val marginAdapter = MarginAdapter()
        val horizonSpace = (HiDisplayUtil.getDisplayWidthInPx(this) - HiDisplayUtil.dp2px(62F) * 5) / 4
        Log.d(TAG, "horizonSpace =  $horizonSpace")
        recyclerView.apply {
            adapter = marginAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position != 0){
                        outRect.left = horizonSpace
                    }
                }
            })
        }
        marginAdapter.refresh(listOf(
            1,2,3,4,5
        ))
    }





    class MarginAdapter : RecyclerView.Adapter<Holder>(){


        private val list = ArrayList<Int>()

        @SuppressLint("NotifyDataSetChanged")
        fun refresh(data : List<Int>){
            list.clear()
            list.addAll(data)
            notifyDataSetChanged()
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
           return Holder(LayoutInflater.from(parent.context).inflate(R.layout.rv_item_view, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {

        }

    }



    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)


}