package zgljl2012.camera_04_face_detection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    // 图片资源数组
    private int[] images = {
            R.mipmap.p1,
            R.mipmap.p2,
            R.mipmap.p3,
            R.mipmap.p4,
            R.mipmap.p5,
            R.mipmap.p6
    };

    // 设置最大的人脸检测数量
    private int FACES_MAX_COUNT = 1;

    // 当前图片资源ID
    private int current_image = 0;

    private MyFaceView mFaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFaceView = (MyFaceView) findViewById(R.id.faceView);
        mFaceView.setBitmap(getBitmap(images[current_image]));
        mFaceView.invalidate();
    }

    /**
     * 界面按钮响应方法
     * @param v 传入的View
     */
    public void onClick(View v){
        Log.d("img", ""+current_image);
        switch(v.getId()){
            case R.id.last:
                current_image = (--current_image+images.length)%images.length;
                mFaceView.setBitmap(getBitmap(images[current_image]));
                break;
            case R.id.face_detection:
                mFaceView.setDetected(true);
                RectF[] r = faceDeection(getRGB_356Bitmap(getBitmap(images[current_image])));
                if(r[0] == null) {
                    Toast.makeText(this, "检测不出来",Toast.LENGTH_SHORT).show();
                }
                mFaceView.setFaces(r);
                break;
            case R.id.next:
                current_image = (++current_image)%images.length;
                mFaceView.setBitmap(getBitmap(images[current_image]));
                break;
        }
        mFaceView.invalidate();
    }

    private RectF[] faceDeection(Bitmap inputBitmap) {

        FaceDetector faceDet = new FaceDetector(inputBitmap.getWidth(),
                inputBitmap.getHeight(), FACES_MAX_COUNT);
        FaceDetector.Face[] faceList = new FaceDetector.Face[FACES_MAX_COUNT];
        // faceDet从inputBitmap里找到人脸，并放入faceList中
        faceDet.findFaces(inputBitmap, faceList);

        RectF[] rects = new RectF[FACES_MAX_COUNT];
        for (int i=0; i < faceList.length; i++) {
            FaceDetector.Face face = faceList[i];
            Log.d("FaceDet", "Face [" + face + "]");
            if (face != null) {
                Log.d("FaceDet", "Face ["+i+"] - Confidence ["+face.confidence()+"]");
                PointF pf = new PointF();
                // Sets the position of the mid-point between the eyes.
                face.getMidPoint(pf);
                Log.d("FaceDet", "\t Eyes distance ["+face.eyesDistance()+"] - Face midpoint ["+pf.x+"&"+pf.y+"]");
                RectF r = new RectF();
                r.left = pf.x - face.eyesDistance() / 2;
                r.right = pf.x + face.eyesDistance() / 2;
                r.top = pf.y - face.eyesDistance() / 2;
                r.bottom = pf.y + face.eyesDistance() / 2;
                rects[i] = r;
            }
        }
        return rects;
    }

    /**
     * 根据资源ID获取图片
     * @param resource 资源ID
     * @return 返回的图片
     */
    public Bitmap getBitmap(int resource) {
        return BitmapFactory.decodeResource(getResources(), resource);
    }

    /**
     * 将Bitmap转换成RGB_565格式的Bitmap
     * @param inputBitmap   待转换的Bitmap
     * @return  转换好的Bitmap
     */
    public Bitmap getRGB_356Bitmap(Bitmap inputBitmap) {
        return inputBitmap.copy(Bitmap.Config.RGB_565, true);
    }

    private void log(String log) {
        Log.d("FaceDet", log);
    }

}
