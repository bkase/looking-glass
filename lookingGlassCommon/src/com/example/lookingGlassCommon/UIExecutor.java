package com.example.lookingGlassCommon;

import android.os.Handler;

import java.util.concurrent.Executor;

public class UIExecutor implements Executor {

    private Handler mHandler;

    public UIExecutor(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }
}
