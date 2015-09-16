package com.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 实现SharedPreferences文件存储
 * Created by 李建华 on 2015/9/14.
 */
public class MySharedPreferences implements MySPInterface{

    private SharedPreferences mSP;
    private SharedPreferences.Editor editor;

    public MySharedPreferences(Context context) {
        mSP = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public String getValue(String key) {
        return mSP.getString(key, "");
    }

    @Override
    public void setValue(String key, String object) {
        editor = mSP.edit();
        editor.putString(key, object);
        editor.apply();
    }
}
