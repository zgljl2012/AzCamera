package com.light;

import android.widget.LinearLayout;

import com.log.AzLog;

/**
 * 实现补光功能
 * Created by 廖金龙 on 2015/8/26.
 */
public class AzChangeSizeByLight implements  AzLight.OnLightChangeListener {

    private LinearLayout layout;

    // 补光范围，10最大，1最小（即不补光）
    private int range = 4;

    private int lastState = 1;

    // 光照强度分为弱光、中等光、强光，下面是两个阀值的设置
    private float weakLight = 6;
    private float strongLight = 15;

    private AzLog log = AzLog.getInstance();

    private OnLightValueChangeListener lightValueChangeListener;

    // 内部监听器，可监听光强变化
    public interface OnLightValueChangeListener {
        void lightValueChangeListener(float value);
    }

    public AzChangeSizeByLight(LinearLayout v) throws Exception {
        if( v == null ) {
            throw new Exception("the Layout is null");
        }
        this.layout = v;
    }

    @Override
    public void onLightChanged(float value, AzLight azLight) {
        if(value <= weakLight || value >= strongLight){
            azLight.setBrightness(255);
            if( lastState == 1 ) {
                lastState = -1;
                // 改变相机大小
                log.log("弱光缩小相机");
                int width = layout.getWidth();
                int height = layout.getHeight();
                log.log("Width:"+width +"  "+"Height:"+height);
                int left = width / range;
                int right = left;
                int top = height / range;
                int bottom = top;
                setLayoutMargin(left, top, right, bottom);

            }
        }
        else {
            azLight.setBrightness(azLight.getDefaultBrightness());
            if( lastState == -1 ) {
                lastState = 1;
                log.log("强光放大相机");
                // 改变相机大小
                setLayoutMargin(0, 0, 0, 0);
            }
        }
        if(lightValueChangeListener != null) {
            lightValueChangeListener.lightValueChangeListener(value);
        }
    }

    /**
     * 设置View的外边距
     * @param left      左边距
     * @param top       上边距
     * @param right     右边距
     * @param bottom    底边距
     */
    private void setLayoutMargin(int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params;
        params = (LinearLayout.LayoutParams)layout.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        layout.setLayoutParams(params);
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        range = (range < 1 ?1 : (range > 10? 10 : range));
        this.range = range;
    }

    public float getWeakLight() {
        return weakLight;
    }

    public void setWeakLight(float weakLight) {
        this.weakLight = (weakLight < 0 ? 0:weakLight);
    }

    public float getStrongLight() {
        return strongLight;
    }

    public void setStrongLight(float strongLight) {
        this.weakLight = (weakLight < 0 ? 30:weakLight);
    }

    public OnLightValueChangeListener getLightValueChangeListener() {
        return lightValueChangeListener;
    }

    public void setLightValueChangeListener(OnLightValueChangeListener lightValueChangeListener) {
        this.lightValueChangeListener = lightValueChangeListener;
    }
}
