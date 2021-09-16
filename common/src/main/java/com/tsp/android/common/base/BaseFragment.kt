package com.tsp.android.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 *     author : shengping.tian
 *     time   : 2021/09/16
 *     desc   : fragment 基类
 *     version: 1.0
 */
abstract class BaseFragment : Fragment() {


    lateinit var layoutView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutView = inflater.inflate(getLayoutResId(), container, false)
        return layoutView
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int


    /**
     * 当前 fragment 宿主是否还存活
     * @return Boolean
     */
    fun isAlive(): Boolean {
        if (isRemoving || isDetached || activity == null) return false
        return true
    }

    /**
     * 弹吐司
     * @param message String
     */
    fun showToast(message: String) {
        if (message.isNullOrEmpty()) return
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}