package com.tsp.learn.font

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tsp.learn.databinding.FontTestActivityBinding

/**
 * @author tsp
 * Date: 2023/9/26
 * desc:
 */
class FontTestActivity : AppCompatActivity() {

    private lateinit var mBinding : FontTestActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = FontTestActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initView()
    }

    private fun initView(){
        val textView2 : TextView = mBinding.test2
        val textView3 : TextView = mBinding.test3
        val textView4 : TextView = mBinding.test4
        FontStyleUtil.get().setBananaPieRegularFontStyle(textView2)
        FontStyleUtil.get().setBananaPieMediumFontStyle(textView3)
        FontStyleUtil.get().setBananaBoldFontStyle(textView4)
    }



    companion object{

        fun go(context: Context){
            context.startActivity(Intent(context, FontTestActivity::class.java))
        }
    }


}