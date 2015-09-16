package zgljl2012.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.camera.AzLog;
import com.data.DataManager;
import com.face.AzFaceDetection;
import com.face.AzImageView;
import com.face.OnFaceDetectionListener;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core;
import com.util.ImageUtil;

import java.io.IOException;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

/**
 *
 * Created by 廖金龙 on 2015/9/9.
 */
public class PreviewActivity extends Activity {

    Handler handler = new Handler();

    private AzFaceDetection detection;
    private AzImageView mImageView;
    private DataManager dataManager = DataManager.getInstance();
    private String path = "/lib/haarcascade_frontalface_alt.xml";
    private AzLog log = AzLog.getInstance();
    private String FaceTag = "face";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview);
        mImageView = (AzImageView) findViewById(R.id.img_preview);
        if(dataManager.currentBmp != null) {
           mImageView.setImageBitmap(dataManager.currentBmp);
            Bitmap bitmap = dataManager.currentBmp;
            opencv_core.IplImage iplImage = this.bitmapToIplImage(bitmap);
            try {
                log.log(FaceTag ,"开始实现人脸检测...");
                detection = new AzFaceDetection(this, path);
                detection.setListener(new OnFaceDetectionListener(){

                    @Override
                    public void handlerResult(opencv_core.CvSeq objects, opencv_core.IplImage pic,
                                              double detectime, final double scale) {
                        if(objects == null) {
                            log.log(FaceTag ,"人脸检测失败!");
                            return;
                        }
                        if(objects.total() == 0) {
                            log.log(FaceTag ,"未检测到人脸");
                            Toast.makeText(getBaseContext(), "未检测到人脸", Toast.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i< objects.total(); ++i)
                        {
                            Pointer p = cvGetSeqElem(objects, i);
                            final opencv_core.CvRect r = new opencv_core.CvRect(p);
                            mImageView.drawRect(r.x(),r.y(),detection.cvRound(r.width()),
                                    detection.cvRound(r.height()*scale));
                            final String filename = "az_jpg_"+System.currentTimeMillis()+".jpg";
                            dataManager.currentFaceName = filename;
                            // 创建新线程去保存切出来的人脸图片
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap face = ImageUtil.cutBitmap(dataManager.currentBmp,
                                            r.x(),r.y(),detection.cvRound(r.width()),
                                            detection.cvRound(r.height()*scale));
                                    ImageUtil.savePictureAsJPG(face,dataManager.faceSavePath, filename);
                                }
                            }).start();
                        }
                    }
                });
                detection.detectIplImage(iplImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.btn_del:
                new AlertDialog.Builder(PreviewActivity.this).setTitle("删除图片")
                        .setMessage("删除将删除照片以及人脸图片，确定删除？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 删除文件
                                        String imgPath = Environment.getExternalStorageDirectory() + dataManager.imgSavePath;
                                        String facePath = Environment.getExternalStorageDirectory() + dataManager.faceSavePath;
                                        String imgName = dataManager.currentBmpName;
                                        String faceName = dataManager.currentFaceName;
                                        boolean r1 = dataManager.deleteFile(imgPath + "/" + imgName);
                                        boolean r2 = dataManager.deleteFile(facePath + "/" + faceName);
                                        if (r1 && r2) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getBaseContext(), "成功删除文件", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getBaseContext(), "删除文件出错！", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.btn_return:
                finish();
                this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                break;
        }
    }

    /**
     * IplImageת转化为Bitmap
     * @param iplImage
     * @return
     */
    public Bitmap IplImageToBitmap(opencv_core.IplImage iplImage) {
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(iplImage.width(), iplImage.height(),
                Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(iplImage.getByteBuffer());
        return bitmap;
    }

    /**
     * Bitmapת转化为IplImage
     * @param bitmap
     * @return
     */
    public opencv_core.IplImage bitmapToIplImage(Bitmap bitmap) {
        opencv_core.IplImage iplImage;
        iplImage = opencv_core.IplImage.create(bitmap.getWidth(), bitmap.getHeight(),
                IPL_DEPTH_8U, 4);
        bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
        return iplImage;
    }
    public Drawable idToDrawable(int id) {
        return this.getResources().getDrawable(id);
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable == null)
            return null;
        return ((BitmapDrawable)drawable).getBitmap();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&& event.getRepeatCount() == 0) {
            finish();
            this.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
