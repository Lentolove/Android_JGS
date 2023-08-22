package com.tsp.android.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Testservice extends Service {

    public Testservice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}