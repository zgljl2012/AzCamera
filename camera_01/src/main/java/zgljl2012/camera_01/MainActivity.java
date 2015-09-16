package zgljl2012.camera_01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private SurfaceView sView;
    private SurfaceHolder surfaceHolder;

    // 定义系统使用的照相机
    private Camera camera;
    // 是否在预览中
    private boolean isPreview = false;

    // 屏幕宽高
    private int screenHeight;
    private int screenWidth;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //*/
        setContentView(R.layout.activity_main);
        // 获取窗口管理器
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        // 获取屏幕的宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        this.screenHeight = displayMetrics.heightPixels;
        this.screenWidth  = displayMetrics.widthPixels;

        sView = (SurfaceView) findViewById(R.id.surfaceView);
        // 设置该Surface不需要自己维护缓冲区
        sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 获取SurfaceHolder
        surfaceHolder = sView.getHolder();
        // 为Surface添加一个回调监听器
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // 打开摄像头
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // 如果camera不为null，释放摄像头
                if( camera != null){
                    if(isPreview)
                        camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
    }

    private void log(String s){
        Log.d("MyCamera", s);
    }

    private void initCamera(){
        if( !isPreview){
            // 获取设备上的摄像头数量
            int cameraCount = Camera.getNumberOfCameras();
            // 创建一个空的CameraInfo对象，用于获取摄像头信息
            Camera.CameraInfo info = new Camera.CameraInfo();
            for( int camIdx = 0; camIdx < cameraCount; camIdx++ ){
                // 获取第camIdx摄像头的信息
                Camera.getCameraInfo(camIdx, info);
                log(""+camIdx+":"+info.facing);
                log(""+camIdx+":"+info.orientation);
            }

            log(""+cameraCount);
            // 此默认打开后置摄像头
            // 通过传入参数可以打开前置摄像头
            // 后置 0-90
            // 前置 1-
            camera = Camera.open(1);
            camera.setDisplayOrientation(90);
        }
        if( camera != null && !isPreview){
            try{
                Camera.Parameters parameters = camera.getParameters();
                // 设置预览照片的大小
                parameters.setPreviewSize(screenWidth, screenHeight);
                // 设置预览照片时每秒显示多少帧的最小值和最大值
                parameters.setPreviewFpsRange(4, 10);
                // 设置图片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                // 设置JPG照片的质量
                parameters.set("jpeg-quality", 85);
                // 设置照片的大小
                parameters.setPictureSize(screenWidth, screenHeight);
                // 通过SurfaceView显示取景画面
                camera.setPreviewDisplay(surfaceHolder);
                // 开始预览
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    public void capture(View source){
        if( camera != null){
            // 控制摄像头自动对焦后才拍照
            // camera.autoFocus(autoFocusCallback);
            // 前置摄像头有可能没有自动对焦功能
            camera.takePicture(null, null, myJpegCallback);
        }
    }

    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if( success ){
                // takePicture()方法需要传入三个监听器参数
                // 第一个监听器：当用户按下快门时激发该监听器
                // 第二个监听器：当相机获取原始照片时激发该监听器
                // 第三个监听器：当相机获取JPG照片时激发该监听器
                camera.takePicture(null, null, myJpegCallback);
            }
        }
    };

    private Camera.PictureCallback myJpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 根据拍照所得的数据创建位图
            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            // 加载/layout/save.xml 文件对应的布局资源
            View saveDialog = getLayoutInflater().inflate(R.layout.save,
                    null);
            ImageView show = (ImageView)saveDialog.findViewById(R.id.img);
            show.setImageBitmap(bm);
            final EditText edit = (EditText) saveDialog.findViewById(R.id.photo_name);
            new AlertDialog.Builder(MainActivity.this).setView(saveDialog)
                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 创建一个位于SD卡上的文件
                            File file = new File(Environment.getExternalStorageDirectory(),
                                    edit.getText().toString()+".jpg");
                            log(edit.getText().toString()+".jpg");
                            FileOutputStream out = null;
                            try{
                                // 打开指定文件输出流
                                out = new FileOutputStream(file);
                                // 将位图输出到指定文件
                                bm.compress(Bitmap.CompressFormat.JPEG, 100,
                                        out);
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                .setNegativeButton("取消", null).show();
            // 重新浏览
            camera.stopPreview();
            camera.startPreview();
            isPreview = true;
        }
    };

    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ok:
                capture(view);
                break;
        }
    }
}
