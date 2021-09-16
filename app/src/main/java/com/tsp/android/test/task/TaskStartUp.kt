package com.tsp.android.test.task

import android.util.Log
import com.tsp.android.MyApp
import com.tsp.android.hilibrary.flow.*

/**
 *     author : shengping.tian
 *     time   : 2021/08/23
 *     desc   :
 *     version: 1.0
 */
object TaskStartUp {
    const val TASK_BLOCK_1 = "block_task_1"
    const val TASK_BLOCK_2 = "block_task_2"
    const val TASK_BLOCK_3 = "block_task_3"

    const val TASK_ASYNC_1 = "async_task_1"
    const val TASK_ASYNC_2 = "async_task_2"
    const val TASK_ASYNC_3 = "async_task_3"

    @JvmStatic
    fun start() {
        //apt annotationprocesstool 编译时去收集 项目中所有的task信息，
        /**
         * @TaskStragtegy(isAysnc=true,taskId='InitPushTask',delayMills=0)
         * class InitPushTask extends Task{
         *
         *    void run(String name){
         *
         *    }
         * }
         *
         * javapoet ---生成 TaskStartUp类
         */

        val loadTask = LoadTask("my task", true)
        loadTask.addTaskListener(object : TaskListener{
            override fun onStart(task: Task?) {
                Log.e("TaskStartUp", "loadTask onStart")
            }

            override fun onRunning(task: Task?) {
                Log.e("TaskStartUp", "loadTask onRunning")

            }

            override fun onFinish(task: Task?) {
                Log.e("TaskStartUp", "loadTask onFinish")
            }

        })


        Log.e("TaskStartUp", "start")
        val project = Project.Builder("TaskStartUp", createTaskCreator())
                .add(TASK_BLOCK_1)
                .add(TASK_BLOCK_2)
                .add(TASK_BLOCK_3)
                .add(TASK_ASYNC_1).dependOn(TASK_BLOCK_1)
                .add(TASK_ASYNC_2).dependOn(TASK_BLOCK_2)
                .add(TASK_ASYNC_3).dependOn(TASK_BLOCK_3)
                .add(loadTask).dependOn(TASK_BLOCK_3)
                .build()


        TaskFlowManager
                .debuggable(true)
                .addBlockTask(TASK_BLOCK_1)
                .addBlockTask(TASK_BLOCK_2)
                .addBlockTask(TASK_BLOCK_3)
                .start(project)

        Log.e("TaskStartUp", "end")
    }

    private fun createTaskCreator(): ITaskCreator {
        return object : ITaskCreator {
            override fun createTask(taskName: String): Task {
                when (taskName) {
                    TASK_ASYNC_1 -> return createTask(taskName, true)
                    TASK_ASYNC_2 -> return createTask(taskName, true)
                    TASK_ASYNC_3 -> return createTask(taskName, true)

                    TASK_BLOCK_1 -> return createTask(taskName, false)
                    TASK_BLOCK_2 -> return createTask(taskName, false)
                    TASK_BLOCK_3 -> return createTask(taskName, false)
                }
                return createTask("default", false)
            }
        }
    }

    fun createTask(taskName: String, isAsync: Boolean): Task {
        return object : Task(taskName, isAsync) {
            override fun run(name: String) {
                Thread.sleep(if (isAsync) 2000 else 1000)
                Log.e("TaskStartUp", "task $taskName, $isAsync,finished")
            }
        }
    }


    class LoadTask(id: String,isAsyncTask :Boolean) :Task(id,isAsyncTask){
        override fun run(name: String) {
           Log.i("TaskStartUp"," my task start")
            Thread.sleep(3000)
            Log.i("TaskStartUp"," my task end")

        }

    }
}