package com.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 实现补光，实现在View画一个让用户校准人脸的人脸框
 * Created by 廖金龙 on 2015/9/9.
 */
public class AzCompensateImageView extends ImageView implements AzCompensateLight {

    private int mLowerLight;
    private int mHigherLight;
    private int mAnimationTime;
    private double mWidthRatio;
    private double mHeightRatio;

    private Context mContext;

    private enum Status {
        COMPENSATE, NORMAL, RESUME
    };

    private enum Mode {
        COMPENSATE, NORMAL
    };

    private Status mStatus;

    private Mode mode;

    private AzLog log = AzLog.getInstance();

    /**
     * 光照感应器当光强变化时的监听器
     */
    private OnLightChangeListener onLightChangeListener = new OnLightChangeListener() {
        @Override
        public void onLightChanged(float value, AzLightClass azLight) {
            log.log(""+mode);
            if((value < mLowerLight || value > mHigherLight)) {
                mode = Mode.COMPENSATE;
            } else if(mode == Mode.COMPENSATE){
                mode = Mode.NORMAL;
            }
            if(mode == Mode.NORMAL) {
                // 恢复
                resume();
                // 屏幕亮度恢复默认
                azLight.setBrightness(azLight.getDefaultBrightness());
                log.log("恢复默认");
            } else if(mode == Mode.COMPENSATE) {
                // 开启补光
                compensate();
                // 屏幕亮度开至最大
                azLight.setBrightness(AzLightClass.MAX_SCREEN_LIGHT);
                log.log("补光");
                mode = Mode.COMPENSATE;
            }
        }
    };

    public AzCompensateImageView(Context context) {
        super(context);
        init(context);
    }

    public AzCompensateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // 初始化函数
    private void init(Context context) {
        mContext = context;
        setAnimationTime(1000);
        setHigherLight(20);
        setLowerLight(5);
        setRange(0.5, 0.5);
        mStatus = Status.NORMAL;
        mode = Mode.NORMAL;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        log.log(getWidth()+","+getHeight());
        float width = (float) (mWidthRatio * getWidth());
        float height = (float) (mHeightRatio * getHeight());
        float x =  ((getWidth() - width) / 2.0f);
        float y =  ((getHeight() - height) / 2.0f);
        log.log(""+x+","+y+","+width+","+height);
        if(mStatus == Status.COMPENSATE) {

        } else if(mStatus == Status.RESUME) {

        }
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        //canvas.drawRect(x,y,x+width,y+height,paint);
    }

    @Override
    public void compensate() {
        mStatus = Status.COMPENSATE;
        this.invalidate();
    }

    @Override
    public void resume() {
        mStatus = Status.RESUME;
        this.invalidate();
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
    }

    @Override
    public int getAnimationTime() {
        return mAnimationTime;
    }

    @Override
    public void setRange(double widthRatio, double heightRatio) {
        mWidthRatio = widthRatio;
        mHeightRatio = heightRatio;
    }

    @Override
    public void setAzCamera(AzCamera azCamera) {

    }
}
