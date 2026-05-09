package com.tsp.learn.recyclerview.diff

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsp.learn.R

/**
 * 使用 ListAdapter 的通用模板。
 * ListAdapter 内部封装了 AsyncListDiffer，省去手动管理 mDiffer 的样板代码。
 */
class StudentAdapter : ListAdapter<StudentBean, StudentAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private val TAG = "DiffUtil"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.d(TAG, "[create] 创建 ViewHolder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return MyViewHolder(view)
    }

    /** 完整绑定 — DiffUtil 判定「ID 不同」或「payload 为空」时调用 */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val bean = getItem(position)
        Log.d(TAG, "[bind   ] position=$position  id=${bean.id}  name=${bean.name}")
        holder.bind(bean, position)
    }

    /** 局部刷新 — getChangePayload 返回非空时调用，只更新变化的字段避免全量重绘 */
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        val bean = getItem(position)
        @Suppress("UNCHECKED_CAST")
        val fields = payloads[0] as? List<String> ?: return
        Log.d(TAG, "[payload] position=$position  id=${bean.id}  仅更新字段=$fields")
        holder.partialBind(bean, fields)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val positionTv: TextView = view.findViewById(R.id.position_tv)
        private val nameTv: TextView = view.findViewById(R.id.name_tv)
        private val ageTv: TextView = view.findViewById(R.id.age_tv)
        private val idTv: TextView = view.findViewById(R.id.id_tv)
        private val genderTv: TextView = view.findViewById(R.id.gender_tv)

        fun bind(bean: StudentBean, position: Int) {
            positionTv.text = "$position"
            nameTv.text = bean.name
            ageTv.text = "${bean.age}岁"
            idTv.text = "ID: ${bean.id}"
            genderTv.text = StudentBean.genderLabel(bean.gender)
            itemView.setBackgroundColor(getGenderColor(bean.gender))
        }

        /** 局部绑定 — 只更新变化的字段 */
        fun partialBind(bean: StudentBean, fields: List<String>) {
            if ("name" in fields) nameTv.text = bean.name
            if ("age" in fields) ageTv.text = "${bean.age}岁"
            if ("gender" in fields) {
                genderTv.text = StudentBean.genderLabel(bean.gender)
                itemView.setBackgroundColor(getGenderColor(bean.gender))
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StudentBean>() {
            override fun areItemsTheSame(old: StudentBean, new: StudentBean): Boolean {
                return old.id == new.id
            }

            override fun areContentsTheSame(old: StudentBean, new: StudentBean): Boolean {
                return old == new
            }

            override fun getChangePayload(old: StudentBean, new: StudentBean): Any? {
                val fields = mutableListOf<String>()
                if (old.name != new.name) fields.add("name")
                if (old.age != new.age) fields.add("age")
                if (old.gender != new.gender) fields.add("gender")
                Log.d("DiffUtil", "[getChangePayload] id=${new.id} → $fields")
                return fields
            }
        }

        private val GIRL_COLOR = Color.parseColor("#FFD6E7")
        private val BOY_COLOR = Color.parseColor("#BAE7FF")

        private fun getGenderColor(gender: Int): Int = when (gender) {
            StudentBean.GENDER_GIRL -> GIRL_COLOR
            StudentBean.GENDER_BOY -> BOY_COLOR
            else -> Color.WHITE
        }
    }
}