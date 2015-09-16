package com.camera;

import android.util.Log;

/**
 * 日志输出类，使用单例模式
 * Created by 廖金龙 on 2015/8/25.
 */
public class AzLog {

    public static AzLog log = new AzLog();

    public final String TAG = "AzCamera";

    private boolean ifNeedOut = true;

    public boolean isIfNeedOut() {
        return ifNeedOut;
    }

    public void setIfNeedOut(boolean ifNeedOut) {
        this.ifNeedOut = ifNeedOut;
    }

    private AzLog() {
        // empty body
    }

    public static AzLog getInstance(){
        return log;
    }

    public void log(String s) {
        log("Null", s);
    }

    public void log(String tag, String s) {
        if(isIfNeedOut()) {
            Log.d(TAG, "[" + tag + "]" + "[" + s + "]");
        }
    }

}
