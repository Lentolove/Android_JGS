package com.tsp.android.hilibrary.crash

import com.tsp.android.hilibrary.utils.AppGlobals
import org.devio.`as`.proj.libbreakpad.NativeCrashHandler
import java.io.File

/**
 *     author : shengping.tian
 *     time   : 2021/08/24
 *     desc   : crash 统一监控管理
 *     version: 1.0
 */
object CrashMgr {

    private const val CRASH_DIR_JAVA = "java_crash"
    private const val CRASH_DIR_NATIVE = "crash_native"

    fun init(){
        val javaCrashDir = getJavaCrashDir()
        val nativeCrashDir = getNativeCrashDir()
        CrashHandler.init(javaCrashDir.absolutePath)
        //native crash 监控
        NativeCrashHandler.init(nativeCrashDir.absolutePath)
    }

    private fun getJavaCrashDir(): File {
        val javaCrashFile = File(AppGlobals.get()!!.cacheDir, CRASH_DIR_JAVA)
        if (!javaCrashFile.exists()) {
            javaCrashFile.mkdirs()
        }
        return javaCrashFile
    }

    private fun getNativeCrashDir(): File {
        val nativeCrashFile = File(AppGlobals.get()!!.cacheDir, CRASH_DIR_NATIVE)
        if (!nativeCrashFile.exists()) {
            nativeCrashFile.mkdirs()
        }
        return nativeCrashFile
    }

    fun crashFiles(): Array<File> {
        return getJavaCrashDir().listFiles() + getNativeCrashDir().listFiles()
    }
}