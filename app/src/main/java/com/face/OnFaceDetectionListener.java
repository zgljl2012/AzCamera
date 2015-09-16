package com.face;

import static com.googlecode.javacv.cpp.opencv_core.*;

public interface OnFaceDetectionListener {
	
	void handlerResult(CvSeq objects, IplImage img, double detectime, double scale);
}
