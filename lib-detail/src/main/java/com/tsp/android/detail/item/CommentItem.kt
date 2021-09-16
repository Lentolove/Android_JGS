package com.tsp.android.detail.item

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tsp.android.common.ext.loadCircle
import com.tsp.android.common.ext.loadCircleBorder
import com.tsp.android.common.ext.loadUrl
import com.tsp.android.detail.R
import com.tsp.android.detail.model.CommentItemModel
import com.tsp.android.hilibrary.utils.HiDisplayUtil
import com.tsp.android.hiui.item.HiDataItem
import com.tsp.android.hiui.item.HiViewHolder
import kotlin.math.min

/**
 *     author : shengping.tian
 *     time   : 2021/09/14
 *     desc   : 商品评论模块 dataItem
 *     version: 1.0
 */
class CommentItem(
    private val model: CommentItemModel
) : HiDataItem<CommentItemModel, HiViewHolder>() {

    private val chipRadius = HiDisplayUtil.dp2px(25f).toFloat()

    override fun onBindData(holder: HiViewHolder, position: Int) {
        val context = holder.itemView.context ?: return
        holder.findViewById<TextView>(R.id.comment_title)?.text = model.commentCountTitle
        val tagArray: List<String>? = model.commentTags
        //商品评价标签，用 Chip 来实现瀑布流的效果
        if (!tagArray.isNullOrEmpty()) {
            val chipGrout: ChipGroup = holder.findViewById(R.id.chipGroup_comment)!!
            chipGrout.visibility = View.VISIBLE
            val colorStateList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_faf0))
            val textColor = ContextCompat.getColor(context, R.color.color_999)

            //当RecyclerView 在上下滑动的时候会存在 Chip 的复用问题，所以需要考虑
            for (index in tagArray.indices) {
                val chip: Chip = if (index < chipGrout.childCount) {
                    //从chipGrout中拿到直接复用
                    chipGrout.getChildAt(index) as Chip
                } else {
                    //否则重新创建
                    val chip = Chip(context).apply {
                        chipCornerRadius = chipRadius
                        chipBackgroundColor = colorStateList
                        setTextColor(textColor)
                        textSize = 14f
                        gravity = Gravity.CENTER
                        isCheckedIconVisible = false
                        isCheckable = false
                        isChipIconVisible = false
                    }
                    chipGrout.addView(chip)
                    chip
                }
                chip.text = tagArray[index]
            }
        }
        //商品评价列表
        model.commentModels?.let {
            val commentContainer = holder.findViewById<LinearLayout>(R.id.ll_comment_container)!!
            commentContainer.visibility = View.VISIBLE
            val layoutInflater = LayoutInflater.from(context)
            //最多只显示5条评论
            for (index in 0..min(it.size - 1, 5)) {
                val comment = it[index]
                val itemView = if (index < commentContainer.childCount) {
                    commentContainer.getChildAt(index)
                } else {
                    val view = layoutInflater.inflate(
                        R.layout.layout_detail_item_comment_item,
                        commentContainer,
                        false
                    )
                    commentContainer.addView(view)
                    view
                }
                itemView.findViewById<ImageView>(R.id.user_avatar).loadCircle(comment.avatar)
                itemView.findViewById<TextView>(R.id.user_name).text = comment.nickName
                itemView.findViewById<TextView>(R.id.comment_content).text = comment.content
            }
        }
    }

    override fun getItemLayoutRes(): Int {
        return R.layout.layout_detail_item_comment
    }
}