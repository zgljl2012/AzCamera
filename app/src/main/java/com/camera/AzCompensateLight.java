package com.camera;

/**
 * 实现补光的接口
 * Created by 廖金龙 on 2015/9/8.
 */
public interface AzCompensateLight {
    // 开启补光
    void compensate();
    // 恢复原界面
    void resume();
    // 注册一个光线传感器
    void registerAzLight(AzLight azLight);
    // 设置低光照阀值
    void setLowerLight(int lower);
    // 获取低光照阀值
    int getLowerLight();
    // 设置高光照阀值
    void setHigherLight(int higher);
    // 获取高光照阀值
    int getHigherLight();
    // 设置补光过渡动画效果持续时间
    void setAnimationTime(int times);
    // 获取补光过渡动画效果持续时间
    int getAnimationTime();
    // 设置最后的照相预览大小比率
    void setRange(double widthRatio, double heightRatio);
    // 设置相机
    void setAzCamera(AzCamera azCamera);
}
