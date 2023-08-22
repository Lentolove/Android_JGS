package com.tsp.learn.recyclerview.diff

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tsp.learn.R
import com.tsp.learn.recyclerview.diff.StudentBean.GENDER_GRIL.GENDER_BOY

class StudentAdapter(context: Context) : RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {

    private val girlColor = "#FFD6E7"
    private val boyColor = "#BAE7FF"

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    //1. 声明 DiffUtil.ItemCallback 回调
    private val itemCallback = object : DiffUtil.ItemCallback<StudentBean>() {
        override fun areItemsTheSame(oldItem: StudentBean, newItem: StudentBean): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StudentBean, newItem: StudentBean): Boolean {
            return oldItem.name == newItem.name && oldItem.age == newItem.age
        }
    }

    //2. 创建 AsyncListDiff 对象
    private val mDiffer = AsyncListDiffer<StudentBean>(this, itemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAdapter.MyViewHolder {
        return MyViewHolder(layoutInflater.inflate(R.layout.item_student, parent, false))
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    fun submitList(studentList: List<StudentBean>) {
        //3. 提交新数据列表
        mDiffer.submitList(studentList)
    }

    override fun onBindViewHolder(holder: StudentAdapter.MyViewHolder, position: Int) {
        //4. 从新数据列表中获取最新数据
        val studentBean = mDiffer.currentList[position]

        holder.nameTv.text = studentBean.name
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val nameTv: TextView = view.findViewById(R.id.name_tv)
    }
}
