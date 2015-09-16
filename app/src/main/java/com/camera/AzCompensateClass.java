package com.camera;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 *
 * Created by 廖金龙 on 2015/9/11.
 */
public class AzCompensateClass implements AzCompensateLight {

    private int mLowerLight;
    private int mHigherLight;
    private int mAnimationTime;
    private double mWidthRatio;
    private double mHeightRatio;

    // 相机默认的大小
    private float mAzCameraWidth;
    private float mAzCameraHeight;

    private Context mContext;

    @Override
    public void setAzCamera(AzCamera azCamera) {
        this.azCamera = azCamera;
        if(azCamera != null) {
            mAzCameraWidth = azCamera.getPreviewWidth();
            mAzCameraHeight = azCamera.getPreviewHeight();
        }
    }

    private AzCamera azCamera;

    private enum Status {
        COMPENSATE, NORMAL
    }

    private Status status;

    private AzLog log = AzLog.getInstance();

    private View view;

    ScaleAnimation coAnim =new ScaleAnimation(1.0f, 0.6f, 1.0f, 0.6f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    ScaleAnimation reAnim =new ScaleAnimation(0.6f, 1f, 0.6f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private OnLightChangeListener onLightChangeListener = new OnLightChangeListener() {
        @Override
        public void onLightChanged(float value, AzLightClass azLight) {
            if ((value < mLowerLight || value > mHigherLight)) {
                if(status == Status.NORMAL){
                    // 开启补光
                    compensate();
                    // 屏幕亮度开至最大
                    azLight.setBrightness(AzLightClass.MAX_SCREEN_LIGHT);
                    log.log("补光");
                }
            }else{
                if(status == Status.COMPENSATE) {
                    // 恢复
                    resume();
                    // 屏幕亮度恢复默认
                    azLight.setBrightness(azLight.getDefaultBrightness());
                    log.log("恢复默认");
                }
            }
        }
    };

    public AzCompensateClass(Context context, View view, AzCamera azCamera){
        this.azCamera = azCamera;
        if(azCamera != null) {
            mAzCameraWidth = azCamera.getPreviewWidth();
            mAzCameraHeight = azCamera.getPreviewHeight();
        }
        init(context,view);
    }

    // 初始化函数
    private void init(Context context, View view) {
        this.view = view;
        mContext = context;
        setRange(0.5, 0.5);
        setAnimationTime(1500);
        setHigherLight(25);
        setLowerLight(5);
        status = Status.NORMAL;
        // 设置动画完成后不恢复
        coAnim.setFillAfter(true);
        reAnim.setFillAfter(true);
    }

    @Override
    public void compensate() {
        status = Status.COMPENSATE;
        view.startAnimation(coAnim);
        // 改变相机大小
        if(azCamera != null) {
            azCamera.setPreviewWidth((int)(this.mAzCameraWidth * mWidthRatio));
            azCamera.setPreviewHeight((int) (this.mAzCameraHeight * mHeightRatio));
        }
    }

    @Override
    public void resume() {
        status = Status.NORMAL;
        view.startAnimation(reAnim);
        // 恢复相机大小
        if(azCamera != null) {
            azCamera.setPreviewWidth((int)(this.mAzCameraWidth));
            azCamera.setPreviewHeight((int)(this.mAzCameraHeight));
        }
    }

    @Override
    public void registerAzLight(AzLight azLight) {
        if(azLight != null) {
            azLight.setOnLightChangeListener(onLightChangeListener);
        }
    }

    @Override
    public void setLowerLight(int lower) {
        if(lower <= 0) {
            lower = 1;
        } else if(lower > 20) {
            lower = 20;
        }
        mLowerLight = lower;
    }

    @Override
    public int getLowerLight() {
        return mLowerLight;
    }

    @Override
    public void setHigherLight(int higher) {
        if(higher < 15) {
            higher = 15;
        } else if(higher > 30) {
            higher = 30;
        }
        mHigherLight = higher;
    }

    @Override
    public int getHigherLight() {
        return mHigherLight;
    }

    @Override
    public void setAnimationTime(int times) {
        if(times <= 0) {
            times = 1;
        } else if(times > 2000) {
            times = 2000;
        }
        mAnimationTime = times;
        coAnim.setDuration(times);
        reAnim.setDuration(times);
    }

    @Override
    public int getAnimationTime() {
        return mAnimationTime;
    }

    @Override
    public void setRange(double widthRatio, double heightRatio) {
        mWidthRatio = widthRatio;
        mHeightRatio = heightRatio;
        coAnim =new ScaleAnimation(1.0f, (float)widthRatio, 1.0f, (float)heightRatio,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        reAnim =new ScaleAnimation((float)widthRatio, 1f, (float)heightRatio, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        coAnim.setDuration(this.getAnimationTime());
        reAnim.setDuration(this.getAnimationTime());
        // 设置动画完成后不恢复
        coAnim.setFillAfter(true);
        reAnim.setFillAfter(true);
    }
}
