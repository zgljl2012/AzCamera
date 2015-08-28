package zgljl2012.camera_04_face_detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 实现圈出人脸的View
 * Created by 廖金龙 on 2015/8/28.
 */
public class MyFaceView extends View {

    private Bitmap mBitmap;
    private RectF[] mFaces;
    private boolean detected;

    public MyFaceView(Context context) {
        super(context);
        detected = false;
    }

    public MyFaceView(Context context, AttributeSet attrs){
        super(context, attrs);
        detected = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint imgPaint = new Paint();
        if(mBitmap!=null)
        {
            int imgWidth=mBitmap.getWidth();
            int imgHeight=mBitmap.getHeight();
            Rect src = new Rect();// 图片
            src.top=0;
            src.left=0;
            src.right=src.left+imgWidth;
            src.bottom=src.top+imgHeight;
            Rect dst = new Rect();// 屏幕
            int viewWidth=this.getWidth();
            int width=0;
            int height=0;
            if(mBitmap.getWidth()>viewWidth)
            {
                width=viewWidth;
                height=(viewWidth*imgHeight)/imgWidth;
            }
            else
            {
                width=imgWidth;
                height=imgHeight;
            }
            dst.top=0;
            dst.left=0;
            dst.right=dst.left+width;
            dst.bottom=dst.top+height;

            canvas.drawBitmap(mBitmap, src, dst, imgPaint);
            Log.v("FaceView", "view width:" + this.getWidth());

            if(detected)
            {
                Paint rectPaint = new Paint();
                rectPaint.setStrokeWidth(2);
                rectPaint.setColor(Color.RED);
                rectPaint.setStyle(Paint.Style.STROKE);

                for (RectF r : mFaces) {
                    if(r == null) break;
                    Log.v("FaceView","r.top="+r.top);
                    r.top=(r.top*width)/imgWidth;
                    r.left=(r.left*width)/imgWidth;
                    r.right=(r.right*width)/imgWidth;
                    r.bottom=(r.bottom*width)/imgWidth;

                    canvas.drawRect(r, rectPaint);
                }
                detected=false;
            }
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public RectF[] getFaces() {
        return mFaces;
    }

    public void setFaces(RectF[] mFaces) {
        this.mFaces = mFaces;
    }

    public boolean isDetected() {
        return detected;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

}
