package com.camera;

/**
 * 拍照后调用的回调函数
 */
public interface  OnPictureCallback {
    void onPictureTaken(byte[] data);
}
