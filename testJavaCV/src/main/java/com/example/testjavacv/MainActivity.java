package com.example.testjavacv;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class MainActivity extends ActionBarActivity {
	private AzImageView img ;
	private AzFaceDetection detection;
	private String path = "/com/example/testjavacv/haarcascade_frontalface_alt.xml";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ʵ��ؼ�
        img = (AzImageView) findViewById(R.id.img);
        // ����ͼ��, p4������ҵ�һ��ͼ����������Լ������һ��ͼƬ����
        Drawable drawable = idToDrawable(R.drawable.p4);
        Bitmap bitmap = this.drawableToBitmap(drawable);
        // ��Bitmapת��ΪIplImage
        IplImage iplImage = this.bitmapToIplImage(bitmap);
        img.setImageBitmap(bitmap);
        
        // �����������
        try {
			detection = new AzFaceDetection(this, path);
			detection.setListener(new OnFaceDetectionListener(){

				@Override
				public void handlerResult(CvSeq objects, IplImage pic,
						double detectime, double scale) {
					if(objects == null) return;
					// ��������
					for (int i = 0; i< objects.total(); ++i)
					{
						Pointer p = cvGetSeqElem(objects, i);
						CvRect r = new CvRect(p);
						r.x();
						img.drawRect(r.x(),r.y(),detection.cvRound(r.width()*scale),
								detection.cvRound(r.height()*scale));
						
					}
				}
	        });
	        detection.detectIplImage(iplImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * IplImageת��ΪBitmap
     * @param iplImage
     * @return
     */
    public Bitmap IplImageToBitmap(IplImage iplImage) {
    	Bitmap bitmap = null;
    	bitmap = Bitmap.createBitmap(iplImage.width(), iplImage.height(), 
    			Bitmap.Config.ARGB_8888);
    	bitmap.copyPixelsFromBuffer(iplImage.getByteBuffer());
    	return bitmap;
    }
    
    /**
     * Bitmapת��ΪIplImage
     * @param bitmap
     * @return
     */
    public IplImage bitmapToIplImage(Bitmap bitmap) {
    	IplImage iplImage;
    	iplImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(),
    			IPL_DEPTH_8U, 4);
    	bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
    	return iplImage;
    }
        
    /**
     * ����ԴIDת��ΪDrawable
     * @param id
     * @return
     */
    public Drawable idToDrawable(int id) {
        return this.getResources().getDrawable(R.drawable.p4);
    }

    /**
     * ��Drawableת��ΪBitmap
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable == null)
            return null;
        return ((BitmapDrawable)drawable).getBitmap();
    }
}
