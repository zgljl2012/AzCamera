package com.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * 这个类实现了设置屏幕亮度以及光线传感器的感应
 * Created by 廖金龙 on 2015/8/25.
 */
public class AzLightClass implements SensorEventListener,AzLight{

    private Activity mContext;

    // 传感器管理器
    private SensorManager mSensorManager;
    // 传感器
    private Sensor mSensorLight;

    // 默认屏幕亮度
    private int mDefaultBrightness;

    public static int MAX_SCREEN_LIGHT = 255;
    public static int MIN_SCREEN_LIGHT = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        if( onLightChangeListener != null) {
            onLightChangeListener.onLightChanged(value, this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // empty body
    }

    // 光照强度改变的监听接口
    private OnLightChangeListener onLightChangeListener;

    /**
     * 构造器
     * @param context Activity
     */
    public AzLightClass(Activity context) {
        init(context);
    }

    /**
     * 初始化方法
     * @param context   Activity
     */
    private void init(Activity context) {
        mContext = context;
        // 所有的传感器都归于一种服务
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // 初始化默认屏幕亮度
        mDefaultBrightness = getBrightness();
    }

    /**
     * 设置屏幕亮度
     * @param brightness    亮度值：0-255
     */
    @Override
    public void setBrightness(int brightness) {
        if(brightness < 0) {
            brightness = 0;
        } else if (brightness > 255) {
            brightness = 255;
        }
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.screenBrightness = brightness/255.0f;
        mContext.getWindow().setAttributes(lp);
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    /**
     * 获取屏幕亮度
     * @return 屏幕亮度
     */
    @Override
    public int getBrightness(){
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        int brightness =  Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,0);
        return brightness;
    }

    @Override
    public void registerListener() {
        // 注册监听器
        mSensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public OnLightChangeListener getOnLightChangeListener() {
        return onLightChangeListener;
    }

    @Override
    public void setOnLightChangeListener(OnLightChangeListener onLightChangeListener) {
        this.onLightChangeListener = onLightChangeListener;
    }

    @Override
    public int getDefaultBrightness() {
        return mDefaultBrightness;
    }


}
