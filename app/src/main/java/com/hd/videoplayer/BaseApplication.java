package com.hd.videoplayer;

import android.app.Application;
import android.content.Context;
import com.tencent.mmkv.MMKV;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MMKV.initialize(this);

    }

}
