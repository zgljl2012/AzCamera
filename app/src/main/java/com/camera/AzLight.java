package com.camera;

/**
 * 光线感应器模块接口
 * Created by 廖金龙 on 2015/9/7.
 */
public interface AzLight {

    void setBrightness(int brightness);

    int getBrightness();

    void registerListener();

    void unregisterListener();

    int getDefaultBrightness();

    void setOnLightChangeListener(OnLightChangeListener listener);

    OnLightChangeListener getOnLightChangeListener();
}
