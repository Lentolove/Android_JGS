package com.tsp.learn.log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.DiskLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.tsp.learn.databinding.LoggerTestActivityBinding

/**
 * @author tsp
 * Date: 2024/8/20
 * desc:
 */
class LoggerTestActivity : AppCompatActivity() {

    private lateinit var mBinding: LoggerTestActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = LoggerTestActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initLogger()
        mBinding.loggerBtn.setOnClickListener {
            Logger.log(Logger.ASSERT, "tag", "log  onclick", null)
        }

        mBinding.loggerNormalBtn.setOnClickListener {
            Logger.d("log d onclick")
            Logger.i("log i onclick")
            Logger.e("log e onclick")
            Logger.w("log w onclick")
        }
    }


    private fun initLogger(){
        Logger.addLogAdapter(AndroidLogAdapter())
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) // (Optional) Whether to show thread info or not. Default true
            .methodCount(0) // (Optional) How many method line to show. Default 2
            .methodOffset(3) // (Optional) Skips some method invokes in stack trace. Default 5
            //        .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
            .tag("WpLogger") // (Optional) Custom tag for each log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        Logger.addLogAdapter(DiskLogAdapter(application))

    }

}