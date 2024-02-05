package com.tsp.learn

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.DoKit
import com.tsp.learn.anim.AnimActivity
import com.tsp.learn.anim.XiuxianAnimActivity
import com.tsp.learn.camera.CameraActivity
import com.tsp.learn.databinding.ActivityMainBinding
import com.tsp.learn.font.FontTestActivity
import com.tsp.learn.memory.MemoryActivity
import com.tsp.learn.recyclerview.RecyclerActivity
import com.tsp.learn.viewpager.ViewPagerActivity
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initData()

        initTest()

        DoKit.Builder(this.application)
            .productId("")
            .build()
    }

    private fun initData() {
        mBinding.btnViewpager.setOnClickListener {
//            startActivity(Intent(this, ViewPagerActivity::class.java))
            mBinding.frameView.setCornersRadius(tlRadius = 100f)
        }
        mBinding.btnRecyclerview.setOnClickListener {
            mBinding.frameView.setAllCornerRadius(100f)

//            startActivity(Intent(this, RecyclerActivity::class.java))
        }
        mBinding.progressTest.setOnClickListener {
            startActivity(Intent(this, ProgressBarActivity::class.java))
        }
        mBinding.animTest.setOnClickListener {
            startActivity(Intent(this, XiuxianAnimActivity::class.java))
        }

        mBinding.btnMemory.setOnClickListener {
            startActivity(Intent(this, MemoryActivity::class.java))
        }
        mBinding.cameraBtn.setOnClickListener {
            changeLogo("com.tsp.learn.default")
        }
        mBinding.autoScrollerView.apply {
        }

        mBinding.autoBtn.setOnClickListener {
            changeLogo("com.tsp.learn.new")
        }


        mBinding.fontTest.setOnClickListener {
            FontTestActivity.go(this)
        }

    }

    fun changeLogo(name: String) {
        if (TextUtils.equals(name, componentName.className)) return
        val pm = packageManager
        pm.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            ComponentName(this, name),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        reStartApp(pm)
    }

    private fun reStartApp(pm: PackageManager) {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfos) {
            if (resolveInfo.activityInfo != null) {
                am.killBackgroundProcesses(resolveInfo.activityInfo.packageName)
            }
        }
    }



    private fun initTest() {
        val am: ActivityManager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = am.runningAppProcesses
        runningAppProcesses.forEach {
            Log.d("tsp===>", it.processName)
        }

    }
}