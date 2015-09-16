package com.example.testjavacv;

import android.content.Context;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

import java.io.File;
import java.io.IOException;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetTickCount;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_LINEAR;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

public class AzFaceDetection {
	// Haar特征分类器文件路径
	private	String cascadeFilePath;
	// 内存缓存器
	private	CvMemStorage storage;
	// 识别结果接口
	private OnFaceDetectionListener listener;
	// 分类器
	private CvHaarClassifierCascade classifier;

	public AzFaceDetection(Context context, String path) throws IOException{
		// 初始化装载器文件
		setCascadeFilePath(path);
		// 初始化
		init(context);
	}

	private void init(Context context) throws IOException {
		// Load the classifier file from Java resources.

		File classifierFile = null;
		try {
			classifierFile = Loader.extractResource(getClass(),
					this.cascadeFilePath,
					context.getCacheDir(), "classifier", ".xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (classifierFile == null || classifierFile.length() <= 0) {
			throw new IOException("Could not extract the classifier file from Java resource.");
		}

		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(com.googlecode.javacv.cpp.opencv_objdetect.class);
		classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
		classifierFile.delete();
		if (classifier.isNull()) {
			throw new IOException("Could not load the classifier file.");
		}
		storage = CvMemStorage.create();

	}

	/*
	 * 识别图片
	 */
	public boolean detectIplImage(IplImage img) {
		// 判断img是否存在
		if (img == null) {
			return false;
		}
		// 此处尺度定为1.2
		double scale = 1.2;
		// Image Preparation 
		// 创建灰度图
		IplImage gray = cvCreateImage(cvSize(img.width(), img.height()), 8, 1);
		// cvRound对一个double型数据进行四舍五入
		// 不同尺度的积分图像
		IplImage small_img = cvCreateImage(cvSize(cvRound(img.width() / scale), cvRound(img.height() / scale)), 8, 1);
		// 将img转化为灰度图，放在gray里
		cvCvtColor(img, gray, CV_BGR2GRAY);
		// 重新调整图像，使其精确匹配目标
		cvResize(gray, small_img, CV_INTER_LINEAR);
		// 直方图均衡
		cvEqualizeHist(small_img, small_img);

		// 清空内存存储块
		cvClearMemStorage(storage);
		// 识别时间
		double t = (double)cvGetTickCount();
		CvSeq objects = cvHaarDetectObjects(small_img,
				classifier,
				storage,
				1.1,
				2,
				0/*CV_HAAR_DO_CANNY_PRUNING*/
		);
		t = (double)cvGetTickCount() - t;
		if (listener != null) {
			listener.handlerResult(objects, img, t, scale);
		}
		cvReleaseImage(gray);
		cvReleaseImage(small_img);
		return true;
	}


	public int cvRound(double d) {
		// TODO Auto-generated method stub
		double r = d + 0.5;
		return (int) r;
	}

	public String getCascadeFilePath() {
		return cascadeFilePath;
	}

	public void setCascadeFilePath(String cascadeFilePath) {
		this.cascadeFilePath = cascadeFilePath;
	}

	public OnFaceDetectionListener getListener() {
		return listener;
	}

	public void setListener(OnFaceDetectionListener listener) {
		this.listener = listener;
	}

}
