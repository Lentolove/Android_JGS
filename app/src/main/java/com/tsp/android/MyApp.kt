package com.tsp.android

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.tsp.android.hilibrary.log.*
import com.tsp.android.jgs.BuildConfig

/**
 *     author : shengping.tian
 *     time   : 2021/08/02
 *     desc   :
 *     version: 1.0
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initARouter()
        initLog()
//        TaskStartUp.start()
//        CrashMgr.init()
    }


    private fun initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    private fun initLog() {
        HiLogManager.init(
            object : HiLogConfig() {
                override fun getGlobalTag(): String {
                    return "JSG-tsp->"
                }

                override fun enable(): Boolean {
                    return true
                }

                override fun includeThread(): Boolean {
                    return true
                }

                override fun stackTraceDepth(): Int {
                    return 2
                }
            },
            HiConsolePrinter()
        )
    }

}