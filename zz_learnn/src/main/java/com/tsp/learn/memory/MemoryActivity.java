package com.tsp.learn.memory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tsp.learn.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: tsp
 * Date: 2023/4/28 10:23
 * Des:
 */
public class MemoryActivity extends AppCompatActivity {


    private TextView dataTv;

    private List<Bitmap> list = new ArrayList<>();

    private List<Map> maplist = new ArrayList<>();



    private Handler handler = new Handler(Looper.getMainLooper());




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_activty);
        dataTv = findViewById(R.id.memory_analyse);

//        MemoryAnalyzeUtil.getInstance().init(this,1000, "");


        handler.postDelayed(runnable,1000);
    }


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap = Bitmap.createBitmap(3240, 4680, Bitmap.Config.ARGB_8888);
            HashMap<Object, Object> objectObjectHashMap = new HashMap<>(10000);
            maplist.add(objectObjectHashMap);
            list.add(bitmap);
             Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(memoryInfo);
             final Runtime r = Runtime.getRuntime();
            float percent = (memoryInfo.getTotalPss() * 1.0f) / (r.maxMemory() / 1024.f);

            dataTv.setText("内存占用： " + percent);
            handler.postDelayed(runnable, 20);
        }
    };

}
