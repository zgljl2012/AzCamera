package com.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.log.AzLog;

import java.io.IOException;

/**
 * 实现相机功能，实现前后摄像头，能拍照，有输出拍到照片
 * 的回调处理器接口，以及按下快门的接口
 * Created by 廖金龙 on 2015/8/25.
 */
public class AzCamera {

    // 静态待获取对象
    private static AzCamera mAzCamera = new AzCamera();

    /**
     * 内部接口，里面的方法在拍照完成后调用
     */
    public interface AzPictureCallback {
       void onPictureTaken(byte[] data);
    }

    // 此回调函数将拍得数据返回给用户
    private AzPictureCallback mAzPictureCallback;

    // 拍照完成后的图片回调函数
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if( mAzPictureCallback != null) {
                mAzPictureCallback.onPictureTaken(data);
            }
            // 重新浏览
            camera.stopPreview();
            camera.startPreview();
            isPreview = true;
        }
    };

    // 预览相片的View，即拍照时相显示的窗口
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    // 上下文
    private Activity mContext;

    // 定义系统使用的照相机
    public Camera mCamera;
    // 是否在预览中
    private boolean isPreview = false;

    // 前置摄像头
    public final static int CAMERA_TYPE_FRONT = 1;
    // 后置摄像头
    public final static int CAMERA_TYPE_BACK = 0;

    public int getCameraType() {
        return mCameraType;
    }

    public void setCameraType(int mCameraType) {
        this.mCameraType = mCameraType;
    }

    // 摄像头类型
    public int mCameraType = CAMERA_TYPE_BACK;

    /**
     * 相机相关参数
     */
    private int mPreviewWidth;          // 预览照片宽度
    private int mPreviewHeight;         // 预览照片高度
    private int mPictureWidth;          // 预览照片宽度
    private int mPictureHeight;         // 预览照片高度
    private int mImageFormat;           // 相片格式
    private int mFrameMin;              // 每秒显示帧的最小值
    private int mFrameMax;              // 每秒显示帧的最大值
    private int mImageQuality;          // 相片质量

    // 获取日志输出器
    private AzLog azLog = AzLog.getInstance();

    /**
     * 构造器，接收参数，初始化 SurfaceHolder
     * 初始化摄像头，一个应用中不可以有多个摄像头，
     * 使用单例模式
     */
    private AzCamera() {

    }

    private void init(Activity context, SurfaceView surface) throws Exception {
        azLog.log("AzCamera","AzCamera初始化开始");
        mContext = context;
        mSurfaceView = surface;
        if(mSurfaceView == null) {
            azLog.log("AzCamera-Exception","SurfaceView为空");
            throw new Exception("SurfaceView is null in " + AzCamera.class);
        }
        // 初始化相关参数
        int width, height;
        if(mContext == null) {
            width = 400;
            height = 400;
        } else {
            // 获取窗口管理器
            WindowManager wm = mContext.getWindowManager();
            Display display = wm.getDefaultDisplay();
            // 获取屏幕的宽和高
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width  = displayMetrics.widthPixels;
        }
        mPreviewHeight = mPictureHeight = height;
        mPreviewWidth  = mPictureWidth = width;

        mImageFormat = ImageFormat.JPEG;
        mImageQuality = 85;

        mFrameMin = 4;
        mFrameMax = 10;
        azLog.log("AzCamera","AzCamera初始化完成");

        // 获取SurfaceHolder
        mSurfaceHolder = mSurfaceView.getHolder();

    }

    /**
     * 获取相机实例, 使用synchronized关键字保证线程安全
     * @param context
     * @param surface
     * @return
     * @throws Exception
     */
    public synchronized static AzCamera getInstance(Activity context, SurfaceView surface, int type) throws Exception {
        mAzCamera.init(context, surface);
        boolean is = mAzCamera.open(type);
        if(is) {
            // 初始化
            mAzCamera.setCameraType(type);
            return mAzCamera;
        }
        return null;
    }

    /**
     * 开始预览，摄像头相关参数设置需在此之前
     */
    public void start() {
        // 为Surface添加一个回调监听器
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                azLog.log("预览开始");
                // 打开摄像头
                mAzCamera.initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // 释放摄像头
                mAzCamera.close();
                azLog.log("预览结束");
            }
        });
    }

    /**
     * 打开摄像头，0为后置，1为前置
     * @param type
     * @return
     */
    private boolean open(int type) {
        if( type != 0 && type != 1) {
            return false;
        }
        if( !isPreview){

            if( type == this.CAMERA_TYPE_BACK ) {
                mCamera = Camera.open(0);
                mCamera.setDisplayOrientation(90);
                return true;
            }
            // 获取设备上的摄像头数量
            int cameraCount = Camera.getNumberOfCameras();
            if(cameraCount < 2) {
                return false;
            }
            // 此默认打开后置摄像头
            // 通过传入参数 1 可以打开前置摄像头，此前需判断是否存在前置摄像头
            mCamera = Camera.open(1);
            mCamera.setDisplayOrientation(90);
        }
        return true;
    }

    /**
     * 给摄像头设置好一系列相关参数，并开始预览
     */
    private void initCamera(){
        azLog.log("isPreView",""+isPreview);
        if( mCamera != null && !isPreview){
            try{
                Camera.Parameters parameters = mCamera.getParameters();
                // 设置预览照片的大小
                parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
                // 设置预览照片时每秒显示多少帧的最小值和最大值
                parameters.setPreviewFpsRange(mFrameMin, mFrameMax);
                // 设置图片格式
                parameters.setPictureFormat(mImageFormat);
                // 设置JPG照片的质量
                parameters.set("jpeg-quality", mImageQuality);
                // 设置照片的大小
                parameters.setPictureSize(mPictureWidth, mPictureHeight);
                // 通过SurfaceView显示取景画面
                mCamera.setPreviewDisplay(mSurfaceHolder);
                // 开始预览
                mCamera.startPreview();
                azLog.log("AzCamera", "摄像头打开");
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    /**
     * 关闭摄像头并释放资源
     */
    public void close() {
        // 如果camera不为null，释放摄像头
        if( mCamera != null){
            if(isPreview)
                mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        isPreview = false;
        azLog.log("AzCamera", "摄像头关闭");
    }

    /**
     * 按下快门一瞬间
     */
    public void capture(){
        if( mCamera != null){
            // 控制摄像头自动对焦后才拍照，仅限于后置摄像头
            if( mCameraType == 0) {
                mCamera.autoFocus(autoFocusCallback);
            }
            // 前置摄像头有可能没有自动对焦功能
            mCamera.takePicture(null, null, mPictureCallback);
        }
    }

    /**
     * 自动对焦功能的回调函数
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if( success ){
                // takePicture()方法需要传入三个监听器参数
                // 第一个监听器：当用户按下快门时激发该监听器
                // 第二个监听器：当相机获取原始照片时激发该监听器
                // 第三个监听器：当相机获取JPG照片时激发该监听器
                camera.takePicture(null, null, mPictureCallback);
            }
        }
    };

    public AzPictureCallback getAzPictureCallback() {
        return mAzPictureCallback;
    }

    public void setAzPictureCallback(AzPictureCallback mAzPictureCallback) {
        this.mAzPictureCallback = mAzPictureCallback;
    }

    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    public void setPreviewWidth(int mPreviewWidth) {
        this.mPreviewWidth = mPreviewWidth;
        Camera.Parameters parameters = mCamera.getParameters();
        // 设置预览照片的大小
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);

    }

    public int getPreviewHeight() {
        return mPreviewHeight;
    }

    public void setPreviewHeight(int mPreviewHeight) {
        this.mPreviewHeight = mPreviewHeight;
        Camera.Parameters parameters = mCamera.getParameters();
        // 设置预览照片的大小
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);

    }

    public int getPictureWidth() {
        return mPictureWidth;
    }

    public void setPictureWidth(int mPictureWidth) {
        this.mPictureWidth = mPictureWidth;
    }

    public int getPictureHeight() {
        return mPictureHeight;
    }

    public void setPictureHeight(int mPictureHeight) {
        this.mPictureHeight = mPictureHeight;
    }

    public int getImageFormat() {
        return mImageFormat;
    }

    public void setImageFormat(int mImageFormat) {
       // 此功能尚未实现
       // this.mImageFormat = mImageFormat;
    }

    public int getFrameMin() {
        return mFrameMin;
    }

    public void setFrameMin(int mFrameMin) {
        this.mFrameMin = mFrameMin;
    }

    public int getFrameMax() {
        return mFrameMax;
    }

    public void setFrameMax(int mFrameMax) {
        this.mFrameMax = mFrameMax;
    }

    public int getImageQuality() {
        return mImageQuality;
    }

    public void setImageQuality(int mImageQuality) {
        this.mImageQuality = mImageQuality;
    }
}
