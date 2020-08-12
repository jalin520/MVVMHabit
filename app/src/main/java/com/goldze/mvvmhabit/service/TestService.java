package com.goldze.mvvmhabit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


import com.goldze.mvvmhabit.TestAidlInterface;
import com.goldze.mvvmhabit.TestAidlListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2020/8/12.
 */
public class TestService extends Service {
    public static final String TAG = TestService.class.getSimpleName();
    int count;
    Timer timer;
    TestAidlListener listener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        // 开了一个定时器，修改count
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                count++;
                try {
                    if (listener != null) {
                        listener.onCountAdd(count);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        }, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TestBind();
    }

    public class TestBind extends TestAidlInterface.Stub {
        public int getCount() {
            return count;
        }

        @Override
        public void registerListener(TestAidlListener listener1) {
            listener = listener1;
        }
    }


}
