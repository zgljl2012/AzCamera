package zgljl2012.camera_03;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.camera.AzCamera;
import com.light.AzChangeSizeByLight;
import com.light.AzLight;
import com.log.AzLog;

public class MainActivity extends ActionBarActivity {

    private LinearLayout surfaceLayout;

    private SurfaceView sView;
    private AzCamera azCamera;

    private AzLight azLight;

    private int lastState = -1;

    private AzLog log = AzLog.getInstance();

    private ImageView preview;

    private AzChangeSizeByLight azChangeSizeByLight;

    private TextView tvLightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sView = (SurfaceView) findViewById(R.id.surfaceView);
        preview = (ImageView) findViewById(R.id.preview);
        // 设置该Surface不需要自己维护缓冲区
        sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        try {
            azCamera = AzCamera.getInstance(this, sView, AzCamera.CAMERA_TYPE_FRONT);
            if(azCamera != null) {
                azCamera.start();
                azCamera.setAzPictureCallback(myJpegCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        azLight = new AzLight(this);
        tvLightView = (TextView) findViewById(R.id.lightValue);

        surfaceLayout = (LinearLayout) findViewById(R.id.linear);
        try {
            azChangeSizeByLight = new AzChangeSizeByLight(surfaceLayout);
            azChangeSizeByLight.setLightValueChangeListener(new AzChangeSizeByLight.OnLightValueChangeListener() {
                @Override
                public void lightValueChangeListener(float value) {
                    tvLightView.setText("光照强度："+value);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ok:
                azCamera.capture();
                break;
            case R.id.setting:

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        azLight.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        azLight.unregisterListener();
        azLight.setBrightness(azLight.getDefaultBrightness());
    }

    /**
     * 照片拍好后的回调函数
     */
    private AzCamera.AzPictureCallback myJpegCallback = new AzCamera.AzPictureCallback() {
        @Override
        public void onPictureTaken(byte[] data) {
            // 根据拍照所得的数据创建位图
            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
                    data.length);
            preview.setImageBitmap(bm);
            /*
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
            */
        }
    };
}
