package com.tsp.android.hilibrary.flow

import android.util.Log
import androidx.annotation.MainThread
import com.tsp.android.hilibrary.flow.TaskRuntime.blockTasks
import com.tsp.android.hilibrary.flow.TaskRuntime.debuggable
import com.tsp.android.hilibrary.flow.TaskRuntime.hasBlockTasks
import com.tsp.android.hilibrary.flow.TaskRuntime.hasWaitingTasks
import com.tsp.android.hilibrary.flow.TaskRuntime.openDebug
import com.tsp.android.hilibrary.flow.TaskRuntime.traversalDependencyTreeAndInit
import com.tsp.android.hilibrary.flow.TaskRuntime.tryRunWaitingTasks

object TaskFlowManager {
    private const val MAX_INIT_DURATION = 5000L

    fun debuggable(debuggable: Boolean): TaskFlowManager {
        openDebug(debuggable)
        return this
    }

    fun addBlockTask(taskId: String): TaskFlowManager {
        TaskRuntime.addBlockTask(taskId)
        return this
    }

    fun addBlockTasks(vararg taskIds: String): TaskFlowManager {
        TaskRuntime.addBlockTasks(*taskIds)
        return this
    }

    @MainThread
    @Synchronized
    fun start(task: Task) {
        Utils.assertMainThread()
        val startTask = if (task is Project) task.startTask else task
        traversalDependencyTreeAndInit(startTask)
        val logEnd = logStartWithWaitTasksInfo()
        task.start()
        val start = System.currentTimeMillis()
        while (hasBlockTasks()) {
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            while (hasWaitingTasks()) {
                tryRunWaitingTasks()
                Log.e(TaskRuntimeListener.TAG, "tryRunBlockTask" + hasWaitingTasks())
            }
            //初始化耗时不能超过5s。确保能进入应用
            val now = System.currentTimeMillis()
            if (now - start > MAX_INIT_DURATION) {
                break
            }
        }
        if (logEnd) {
            logEndWithWaitTasksInfo()
        }
    }

    /**
     * 打印锚点信息
     *
     * @return
     */
    private fun logStartWithWaitTasksInfo(): Boolean {
        if (!debuggable()) {
            return false
        }
        val stringBuilder = StringBuilder()
        val hasWaitTasks = hasBlockTasks()
        if (hasWaitTasks) {
            stringBuilder.append("has some block task！")
            stringBuilder.append("( ")
            for (taskId in blockTasks) {
                stringBuilder.append("\"$taskId\" ")
            }
            stringBuilder.append(")")
        } else {
            stringBuilder.append("has no any block task！")
        }
        if (debuggable()) {
            Log.e(TaskRuntimeListener.TAG, stringBuilder.toString())
        }
        return hasWaitTasks
    }

    /**
     * 打印锚点信息
     */
    private fun logEndWithWaitTasksInfo() {
        if (!debuggable()) {
            return
        }
        Log.e(TaskRuntimeListener.TAG, "All block tasks were released！")
    }
}