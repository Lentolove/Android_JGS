package com.tsp.android.hilibrary.crash

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.text.format.Formatter
import android.util.Log
import com.tsp.android.hilibrary.BuildConfig
import com.tsp.android.hilibrary.executor.HiExecutor
import com.tsp.android.hilibrary.log.HiLog
import com.tsp.android.hilibrary.utils.ActivityManager
import com.tsp.android.hilibrary.utils.AppGlobals
import java.io.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

/**
 *     author : shengping.tian
 *     time   : 2021/08/24
 *     desc   : crash崩溃信息存储
 *     version: 1.0
 */
internal object CrashHandler {

    private var CRASH_DIR = "crash_dir"

    fun init(crashDir: String) {
        Thread.setDefaultUncaughtExceptionHandler(CaughtExceptionHandler())
        //设置 crash 异常处理类
        this.CRASH_DIR = crashDir
    }


    private class CaughtExceptionHandler : Thread.UncaughtExceptionHandler {

        private val context = AppGlobals.get()!!

        private val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm--ss", Locale.CHINA)

        //app启动时间
        private val LAUNCH_TIME = formatter.format(Date())

        //处理不了的交给默认的
        private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        override fun uncaughtException(t: Thread, e: Throwable) {
            //如果自己不处理，交给系统默认的 Handler 处理
            if (!handleException(e) && defaultExceptionHandler != null) {
                defaultExceptionHandler.uncaughtException(t, e)
            }
            //重启app
            restartApp()
        }


        private fun restartApp() {
            HiLog.e("restartApp...")
            val intent: Intent? =
                context.packageManager?.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }

        /**
         * 自己处理未捕获的异常
         */
        private fun handleException(e: Throwable?): Boolean {
            if (e == null) return false
            //记录设备基础信息
            val log = collectDeviceInfo(e)
            if (BuildConfig.DEBUG) {
                HiLog.e(log)
            }
            saveCrashInfo2File(log)
            return true
        }

        /**
         * 将设备信息存储到文件中
         */
        private fun saveCrashInfo2File(log: String) {
            val crashDir = File(CRASH_DIR)
            if (!crashDir.exists()) {
                crashDir.mkdirs()
            }
            val crashFile = File(crashDir, formatter.format(Date()) + "-crash.txt")
            crashFile.createNewFile()
            //写入
            val fos = FileOutputStream(crashFile)
            try {
                fos.write(log.toByteArray())
                fos.flush()
            } catch (e: Exception) {
                e.printStackTrace()
                HiLog.e("saveCrashInfo2File failed ${e.message}")
            } finally {
                fos.close()
            }
        }

        /**
         * 记录设备信息
         * 设备类型、OS本版、线程名、前后台、使用时长、App版本、升级渠道
         * CPU架构、内存信息、存储信息、permission权限
         */
        private fun collectDeviceInfo(e: Throwable): String {
            val sb = StringBuilder()
            sb.apply {
                append("brand = ${Build.BRAND}\n")// huawei,xiaomi
                append("rom = ${Build.MODEL}\n") //sm-G9550
                append("os = ${Build.VERSION.RELEASE}\n")//9.0
                append("sdk = ${Build.VERSION.SDK_INT}\n")//28
                append("launch_time = ${LAUNCH_TIME}\n")//启动APP的时间
                append("crash_time = ${formatter.format(Date())}\n")//crash发生的时间
                append("forground = ${ActivityManager.instance.front}\n")//应用处于前后台
                append("thread = ${Thread.currentThread().name}\n")//异常线程名
                append("cpu_arch = ${Build.CPU_ABI}\n")//armv7 armv8

                //app 信息
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                append("version_code = ${packageInfo.versionCode}\n")
                append("version_name = ${packageInfo.versionName}\n")
                append("package_name = ${packageInfo.packageName}\n")
                append("requested_permission = ${Arrays.toString(packageInfo.requestedPermissions)}\n")//已申请到那些权限

                //统计一波 存储空间的信息，
                val memInfo = android.app.ActivityManager.MemoryInfo()
                val ams =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                ams.getMemoryInfo(memInfo)
                append("availMem = ${Formatter.formatFileSize(context, memInfo.availMem)}\n")//可用内存
                append("totalMem = ${Formatter.formatFileSize(context, memInfo.totalMem)}\n")//设备总内存
                val file = Environment.getExternalStorageDirectory()
                val statFs = StatFs(file.path)
                val availableSize = statFs.availableBlocks * statFs.blockSize
                append(
                    "availStorage = ${
                        Formatter.formatFileSize(
                            context,
                            availableSize.toLong()
                        )
                    }\n"
                )//存储空间
            }
            val write: Writer = StringWriter()
            val printWriter = PrintWriter(write)
            e.printStackTrace(printWriter)
            var cause = e.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            sb.append(write.toString())
            return sb.toString()
        }
    }


}