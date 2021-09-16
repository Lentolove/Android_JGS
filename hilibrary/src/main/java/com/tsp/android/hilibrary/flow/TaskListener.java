package com.tsp.android.hilibrary.flow;

public interface TaskListener {

    void onStart(Task task);

    void onRunning(Task task);

    void onFinish(Task task);
}

