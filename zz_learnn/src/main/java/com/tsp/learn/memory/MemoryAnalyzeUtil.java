package com.tsp.learn.memory;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.WorkerThread;


import com.tsp.android.hilibrary.executor.HiExecutor;
import com.tsp.android.hilibrary.log.HiThreadFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * author: tsp
 * Date: 2023/4/26 10:19
 * Des:
 */
public class MemoryAnalyzeUtil {

    private static final String TAG = "MemoryAnalyzeUtil";

    private volatile static MemoryAnalyzeUtil instance = null;
    private ActivityManager activityManager;
    private long freq;
    private final String relativeSavePath = "sourceSummary.txt";
    private String savePath;
    private StringBuilder surveyBuffer;

    private boolean stop = false;

    private final DateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    private final Handler handler = new Handler(Looper.getMainLooper());


    private MemoryAnalyzeUtil() {
    }

    public static MemoryAnalyzeUtil getInstance() {
        if (instance == null) {
            synchronized (MemoryAnalyzeUtil.class) {
                if (instance == null) {
                    instance = new MemoryAnalyzeUtil();
                }
            }
        }
        return instance;
    }

    // freq为采样周期
    public void init(Context context, long freq, String savePath) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        this.freq = freq;
//        this.savePath = savePath + relativeSavePath;
        surveyBuffer = new StringBuilder();
        handler.post(runnable);
    }

    @WorkerThread
    private void writeDown(String data) {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter osw;
        try {
            File file = new File(savePath);
            if (!file.exists()){
                file.createNewFile();
            }

            fileOutputStream = new FileOutputStream(file, true);
            osw = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
            osw.write(data);
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final Runnable runnable = this::printMemoryInfo;

    private void printMemoryInfo(){

        String time = formatter.format(new Date());
        surveyBuffer.append("\n");
        surveyBuffer.append(time).append(" : ");

        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String javaHeap = memoryInfo.getMemoryStat("summary.java-heap");
            surveyBuffer.append("\tjavaHeap = ").append(javaHeap);
            String nativeHeap = memoryInfo.getMemoryStat("summary.native-heap");
            surveyBuffer.append("\tnativeHeap = ").append(nativeHeap);
            String code = memoryInfo.getMemoryStat("summary.code");
            surveyBuffer.append("\tcode = ").append(code);
            String stack = memoryInfo.getMemoryStat("summary.stack");
            surveyBuffer.append("\tstack = ").append(stack);
            String graphics = memoryInfo.getMemoryStat("summary.graphics");
            surveyBuffer.append("\tgraphics = ").append(graphics);
            String privateOther = memoryInfo.getMemoryStat("summary.private-other");
            surveyBuffer.append("\tprivateOther = ").append(privateOther);
            String system = memoryInfo.getMemoryStat("summary.system");
            surveyBuffer.append("\tsystem = ").append(system);
            String swap = memoryInfo.getMemoryStat("summary.total-swap");
            surveyBuffer.append("\tswap = ").append(swap);
        }

        int totalPss = memoryInfo.getTotalPss();
        int M = 1024*1024;
        Runtime r = Runtime.getRuntime();
//        Log.d(TAG,"最大可用内存:" + r.maxMemory() / M + "M");
//        Log.d(TAG, "当前可用内存:" + r.totalMemory()/ M + "M");
//        Log.d(TAG, "当前空闲内存:" + r.freeMemory() / M + "M");
        Log.d(TAG, " totalPss = " + totalPss * 1.0f / (r.maxMemory() / 1024));
//        if (surveyBuffer.length() > 1024 * 5){
//            final String data = surveyBuffer.toString();
//            surveyBuffer.delete(0, surveyBuffer.length());
//            HiExecutor.INSTANCE.execute(1, () -> {
//                writeDown(data);
//            });
//        }
        if (!stop) handler.postDelayed(runnable,freq);
    }
}
