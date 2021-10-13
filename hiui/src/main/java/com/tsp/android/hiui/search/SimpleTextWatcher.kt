package com.tsp.android.hiui.search

import android.text.Editable
import android.text.TextWatcher

/**
 *     author : shengping.tian
 *     time   : 2021/09/17
 *     desc   :  TextWatcher 接口
 *     version: 1.0
 */
open class SimpleTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {

    }
}