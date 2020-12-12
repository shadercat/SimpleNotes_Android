package suan.chan.pzpi_17_8.util;

import android.os.Handler;
import android.os.HandlerThread;

public class mWorkingThread extends HandlerThread {

    private Handler mWorkerHandler;

    public mWorkingThread(String name) {
        super(name);
    }

    public void postTask(Runnable task) {
        mWorkerHandler.post(task);
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper());
    }
}