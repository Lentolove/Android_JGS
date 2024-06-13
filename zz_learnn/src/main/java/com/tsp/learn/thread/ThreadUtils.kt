package com.tsp.learn.thread

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * @author tsp
 * Date: 2024/2/5
 * desc:
 */
object ThreadUtils {

    const val LOG_TAG = "ThreadUtils_tsp"

    /**
     * 获取所有线程信息
     */
    fun getThreadInfoList(): List<ThreadInfo> {
        //获取伪文件所有的线程信息文件
        val file = File("/proc/self/task")
        if (!file.isDirectory) return emptyList()

        val listFile = file.listFiles()
        if (listFile == null || listFile.isEmpty()) return emptyList()
        //遍历task文件目录下
        var hitFlag = 0
        val list = ArrayList<ThreadInfo>()
        for (threadDir in listFile) {
            //读取每个目录下的status文件获取单个线程信息
            val statusFile = File(threadDir, "status")
            if (statusFile.exists()) {
                val threadInfo = ThreadInfo()
                try {
                    BufferedReader(InputStreamReader(FileInputStream(statusFile))).use { reader ->
                        var line: String
                        hitFlag = 0
                        while (reader.readLine().also { line = it } != null) {
                            if (hitFlag > 2) {
                                break
                            }
                            //解析线程名
                            if (line.startsWith("Name")) {
                                val name =
                                    line.substring("Name".length + 1).trim { it <= ' ' }
                                threadInfo.name = name
                                hitFlag++
                                continue
                            }
                            //解析线程Pid
                            if (line.startsWith("Pid")) {
                                val pid =
                                    line.substring("Pid".length + 1).trim { it <= ' ' }
                                threadInfo.id = pid
                                hitFlag++
                                continue
                            }
                            //解析线程状态
                            if (line.startsWith("State")) {
                                val state =
                                    line.substring("State".length + 1).trim { it <= ' ' }
                                threadInfo.status = state
                                hitFlag++
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, e.toString())
                }
                list.add(threadInfo)
            }
        }
        return list
    }

}

class ThreadInfo{
    var name : String = ""
    var id : String = ""
    var status : String = ""

    override fun toString(): String {
        return "name = $name id = $id status = $status"
    }
}