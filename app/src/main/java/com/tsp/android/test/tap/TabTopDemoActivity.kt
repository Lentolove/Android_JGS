package com.tsp.android.test.tap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.tsp.android.hiui.tab.top.HiTabTopInfo
import com.tsp.android.hiui.tab.top.HiTabTopLayout
import com.tsp.android.jgs.R

class TabTopDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_top)

        val tabTopLayout = findViewById<HiTabTopLayout>(R.id.hi_top_layout)
        val defaultColor:Int = ContextCompat.getColor(this, R.color.color_333)
        val selectColor:Int = ContextCompat.getColor(this, R.color.color_dd2)
        val list = ArrayList<HiTabTopInfo<Int>>()
        for (i in 0..20) {
            var item = HiTabTopInfo("条目:${i}",  defaultColor, selectColor)
            list.add(item)
        }
        tabTopLayout.inflateInfo(list as List<HiTabTopInfo<*>>)
        tabTopLayout.defaultSelected(list[0])
        tabTopLayout.addTabSelectedChangeListener { index, prevInfo, nextInfo ->
            //点击之后选中的那个下标
            Toast.makeText(this,"点击了${index}", Toast.LENGTH_SHORT).show()
        }


    }
}