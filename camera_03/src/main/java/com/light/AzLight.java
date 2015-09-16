package com.light;

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
public class AzLight implements SensorEventListener{

    private Activity mContext;

    // 传感器管理器
    private SensorManager mSensorManager;
    // 传感器
    private Sensor mSensorLight;

    // 默认屏幕亮度
    private int mDefaultBrightness;

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
    public AzLight(Activity context) {
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
    public void setBrightness(int brightness) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.screenBrightness = brightness/255.0f;
        mContext.getWindow().setAttributes(lp);
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    /**
     * 获取屏幕亮度
     * @return 屏幕亮度
     */
    protected int getBrightness(){
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        int brightness =  Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,0);
        return brightness;
    }

    public void registerListener() {
        // 注册监听器
        mSensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    public OnLightChangeListener getOnLightChangeListener() {
        return onLightChangeListener;
    }

    public void setOnLightChangeListener(OnLightChangeListener onLightChangeListener) {
        this.onLightChangeListener = onLightChangeListener;
    }

    public int getDefaultBrightness() {
        return mDefaultBrightness;
    }


}
