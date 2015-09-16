package com.example.testjavacv;

import static com.googlecode.javacv.cpp.opencv_core.*;

public interface OnFaceDetectionListener {
	
	public void handlerResult(CvSeq objects, IplImage img, double detectime, double scale);
}
