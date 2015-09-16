package zgljl2012.camera_02_light;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    // 传感器精度
    private TextView mTvAccuracy;
    // 传感器值
    private TextView mTvValue;
    // 屏幕亮度
    private TextView mTvBrightness;

    // 传感器管理器
    private SensorManager mSensorManager;
    // 传感器
    private Sensor mSensorLight;
    // 默认屏幕亮度
    private int mDefaultBrightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvAccuracy = (TextView) findViewById(R.id.tv_accuracy);
        mTvValue = (TextView)findViewById(R.id.tv_value);
        mTvBrightness = (TextView)findViewById(R.id.tv_brightness);

        // 所有的传感器都归于一种服务
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // 获取默认屏幕亮度
        mDefaultBrightness = getBrightness();
        mTvBrightness.setText(""+mDefaultBrightness);
    }

    /**
     *
     * @param brightness    亮度值：0-255
     */
    protected void setBrightness(int brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness/255.0f;
        getWindow().setAttributes(lp);
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    /**
     * 获取屏幕亮度值
     * @return
     */
    protected int getBrightness(){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int brightness =  Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,0);
        return brightness;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册监听器
        mSensorManager.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String s = ""+event.values[0];
        mTvValue.setText(s);
        if(event.values[0] <= 10){
            setBrightness(255);
            mTvBrightness.setText("255");
        }
        else {
            setBrightness(mDefaultBrightness);
            mTvBrightness.setText(""+mDefaultBrightness);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        mTvAccuracy.setText(""+accuracy);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setBrightness(mDefaultBrightness);
    }
}
