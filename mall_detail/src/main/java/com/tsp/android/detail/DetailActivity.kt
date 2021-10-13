package com.tsp.android.detail

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsp.android.detail.databinding.ActivityDeatilBinding
import com.tsp.android.detail.item.*
import com.tsp.android.detail.model.CommentItemModel
import com.tsp.android.hilibrary.utils.HiStatusBar
import com.tsp.android.hiui.item.HiAdapter
import com.tsp.android.detail.model.DetailViewModel
import com.tsp.android.hilibrary.utils.HiRes
import com.tsp.android.hiui.item.HiDataItem

class DetailActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDeatilBinding

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置沉浸式状态栏
        HiStatusBar.setStatusBar(this, true, Color.TRANSPARENT, true)
        mBinding = ActivityDeatilBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        initView()
        preBindData()
        bindData()
    }

    private fun initView() {
        mRecyclerView = mBinding.recyclerDetail
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mRecyclerView.adapter = HiAdapter(this)

        //设置 navigationBar
        mBinding.detailNavBar.apply {
            setNavBackListener { onBackPressed() }
            addRightTextButton(
                HiRes.getString(R.string.if_share),
                R.integer.id_if_share
            ).setOnClickListener {
                Toast.makeText(
                    this@DetailActivity,
                    "share,not support for now.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //为 navigationBar 设置背景渐变颜色
        mRecyclerView.addOnScrollListener(TitleScrollListener(callback = {
            mBinding.detailNavBar.setBackgroundColor(it)
        }))
    }

    private fun preBindData() {
        val adapter = mRecyclerView.adapter as HiAdapter
        val dataItems = mutableListOf<HiDataItem<*, *>>()
        val headItem = viewModel.createHeaderItem()
        val commentModel: CommentItemModel = viewModel.createCommentItemModel()
        val detailViewModel = viewModel.createDetailModel()

        //头部banner
        dataItems.add(headItem)
        //商品评论条目
        dataItems.add(CommentItem(commentModel))
        //添加店铺条目
        dataItems.add(ShopItem(detailViewModel))

        //商品描述
        dataItems.add(GoodAttrItem(detailViewModel))

        detailViewModel.gallery?.forEach {
            dataItems.add(GalleryItem(it.url))
        }

        detailViewModel.similarGoods?.let {
            dataItems.add(SimilarTitleItem())
            it.forEach { goodsModel ->
                dataItems.add(GoodsItem(goodsModel, false))
            }
        }

        adapter.cleatItems()
        adapter.addItems(dataItems, true)
    }

    private fun bindData() {

    }


}