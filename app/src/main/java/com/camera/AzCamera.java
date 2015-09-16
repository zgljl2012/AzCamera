package com.camera;

/**
 * 照相模块接口类
 * Created by 廖金龙 on 2015/9/6.
 */
public interface AzCamera {
    // 打开前置摄像头，开始预览
    void start();

    void restart();

    // 拍照
    void capture();

    // 设置拍照回调函数
    void setOnPictureCallback(OnPictureCallback callback);

    // 获取拍照回调函数
    OnPictureCallback getOnPictureCallback();

    // 关闭摄像头
    void close();

    // 以JPG格式保存数据
    void savePictureAsJPG(byte[] data);

    /**
     * 属性修改器
     */
    void setPictureWidth(int width);
    void setPictureHeight(int height);
    void setPreviewWidth(int width);
    void setPreviewHeight(int height);
    void setMaxFrame(int max);
    void setMinFrame(int min);

    /**
     * 属性访问器
     * @return
     */
    int getPictureWidth();
    int getPictureHeight();
    int getPreviewWidth();
    int getPreviewHeight();
    int getMaxFrame();
    int getMinFrame();

}


