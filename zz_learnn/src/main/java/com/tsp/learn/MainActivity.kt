package com.tsp.learn

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import com.tsp.android.hilibrary.utils.HiDataBus
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import com.tsp.learn.anim.AnimActivity
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
    }

    private fun initData() {
        mBinding.btnViewpager.setOnClickListener {
            startActivity(Intent(this, ViewPagerActivity::class.java))
        }
        mBinding.btnRecyclerview.setOnClickListener {
            startActivity(Intent(this, RecyclerActivity::class.java))
        }
        mBinding.progressTest.setOnClickListener {
            startActivity(Intent(this, ProgressBarActivity::class.java))
        }
        mBinding.animTest.setOnClickListener {
//            startActivity(Intent(this, AnimActivity::class.java))
            startActivity(Intent(this, LiveDataActivity::class.java))
        }
        HiDataBus.with<String>("test_data").observe(this) {
            mBinding.animTest.text = it
        }
        mBinding.btnMemory.setOnClickListener {
            startActivity(Intent(this, MemoryActivity::class.java))
        }
        mBinding.cameraBtn.setOnClickListener {
            InputTextDialogFragment.Builder().showNow(supportFragmentManager)
//            startActivity(Intent(this,CameraActivity::class.java))
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            val outFile: File = File(
//                Environment.getExternalStorageDirectory().getAbsolutePath()
//                    .toString() + "/000/" + System.currentTimeMillis() + ".jpg"
//            )
//            if (!outFile.exists()){
//                outFile.mkdirs()
//            }
//            val uri = FileProvider.getUriForFile(
//                this,
//                "$packageName.fileProvider",
//                outFile
//            )
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            startActivity(intent)

        }
        mBinding.autoScrollerView.apply {
//            setInAnimation(this@MainActivity, R.anim.show_anim)
//            setOutAnimation(this@MainActivity, R.anim.hide_anim)
            setText("这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试这是测试")
        }

        mBinding.autoBtn.setOnClickListener {
//            mBinding.autoScrollerView.setText("这是测试这是测试\n这是测试这是测试")
            mBinding.autoScrollerView.next();
        }


        mBinding.fontTest.setOnClickListener {
            FontTestActivity.go(this)
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