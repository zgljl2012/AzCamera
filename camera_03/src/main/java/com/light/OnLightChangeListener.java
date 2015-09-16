package com.light;

/**
 * 光照强度改变的监听器接口
 * Created by 廖金龙 on 2015/9/7.
 */
public interface OnLightChangeListener {
    void onLightChanged(float value, AzLight azLight);
}