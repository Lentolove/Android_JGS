package com.tsp.android.hilibrary.flow

interface ITaskCreator {
    fun createTask(taskName: String): Task
}