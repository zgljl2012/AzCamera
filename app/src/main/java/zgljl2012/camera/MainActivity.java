package zgljl2012.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.camera.AzCamera;
import com.camera.AzCameraClass;
import com.camera.AzCompensateClass;
import com.camera.AzCompensateImageView;
import com.camera.AzCompensateLight;
import com.camera.AzLight;
import com.camera.AzLightClass;
import com.camera.OnPictureCallback;
import com.data.DataManager;
import com.util.ImageUtil;

import java.io.ByteArrayOutputStream;

public class MainActivity extends Activity {
    // 自定义的前置摄像机
    private AzCamera camera;
    // 预览的窗口
    private SurfaceView surfaceView;
    // 负责补光的覆盖在预览窗口上的View
    private AzCompensateImageView azImageView;
    // 光线传感器、屏幕亮度改变部分
    private AzLight azLight;

    // 拍照完显示图片的小预览View
    private ImageButton btnPreview;

    private FrameLayout frameLayout;

    private long exitTime = 0;


    // 补光接口
    private AzCompensateLight azCompensateLight;
    private Animation doudong;

    private DataManager dataManager = DataManager.getInstance();

    private ImageButton btn_capture, btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        btnPreview  = (ImageButton) findViewById(R.id.btn_preview);

        azLight = new AzLightClass(this);
        azCompensateLight = new AzCompensateClass(this, frameLayout, camera);
        azCompensateLight.registerAzLight(azLight);

        // 设置该Surface不需要自己维护缓冲区
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        btn_capture = (ImageButton) findViewById(R.id.btn_capture);
        btn_setting = (ImageButton) findViewById(R.id.btn_setting);
        btn_capture.setOnTouchListener(new OnAnimationTouchListener(this,
                R.anim.scale_in, R.anim.scale_out));
        OnAnimationTouchListener l2 = new OnAnimationTouchListener(this,
                R.anim.rotate_in, R.anim.rotate_out);
        l2.setIfOut(false);
        btn_setting.setOnTouchListener(l2);

        // 抖动动画
        doudong = AnimationUtils.loadAnimation(this, R.anim.doudong);
    }

    public void onClick(View v){
        int id = v.getId();
        switch(id) {
            case R.id.btn_preview:
                if(dataManager.currentBmp != null) {
                    Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                    startActivity(intent);
                    // 推入推出动画效果
                    this.overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
                }
                break;
            case R.id.btn_capture:
                camera.capture();
                break;
            case R.id.btn_setting:{
                Intent intent = new Intent(MainActivity.this, Setting2Activity.class);
                startActivity(intent);
                // 推入推出动画效果
                this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            camera = AzCameraClass.getInstance(this, surfaceView, 1);
            camera.setPreviewHeight(400);
            camera.setPreviewWidth(200);
            camera.setOnPictureCallback(new OnPictureCallback() {

                @Override
                public void onPictureTaken(byte[] data) {
                    // 将图片显示在小预览窗口
                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    // 将摆正的图片保存
                    bm = ImageUtil.rotateBitmap(bm, -90);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    // 将当前Bitmap放到Data里
                    dataManager.currentBmp = Bitmap.createBitmap(bm);
                    camera.savePictureAsJPG(baos.toByteArray());
                    // 缩放图片后显示在Preview里
                    float wr = (float)btnPreview.getWidth() / (float)bm.getWidth();
                    float hr = (float)btnPreview.getHeight() / (float)bm.getHeight();
                    bm = ImageUtil.scaleBitmap(bm,wr,hr);
                    btnPreview.setImageBitmap(bm);
                    btnPreview.startAnimation(doudong);
                }
            });
            camera.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        azLight.registerListener();
        azCompensateLight.setAzCamera(camera);
        // 设置补光区域范围
        azCompensateLight.setRange(dataManager.compensateWidthRatio,
                dataManager.compensateHeightRatio);
        // 调用一下补光
        azCompensateLight.compensate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        azLight.unregisterListener();
        // 恢复默认屏幕亮度
        azLight.setBrightness(azLight.getDefaultBrightness());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
