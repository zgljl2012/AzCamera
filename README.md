##光线自适应补光人脸识别
小学期实训任务，目前里面有一个Android Stdio工程：
* Camera_01 : 实现了根据光线自动调节亮度的自拍摄像头
* Camera_02_light : 实现了使用光线感应器感知光强并自动调节屏幕亮度
* Camera_03 ：是对上述两个工程的代码的封装，并实现了摄像头自动补光功能
* Camera_04_face_detection ：实现了Android系统自带的人脸检测功能，但效果太弱

---

###Camera_01
- 使用Android内部的Camera实现了自拍，Camera里面的open方法能指定是前置或后置摄像头，SurfaceView是照片预览时的显示，takePicture是拍照
- 使用SensorEventListener实现光线传感器光强数值改变的监听，通过onSensorChanged方法获取到现在的光强
- setBrightness和getBrightness分别用来设置屏幕亮度和改变屏幕亮度

其实原来的Camera_01只实现了自拍功能，光线感应和屏幕亮度的改变是后来加上去的。

###Camera_02_light
- 使用SensorEventListener实现光线传感器光强数值改变的监听，通过onSensorChanged方法获取到现在的光强
- setBrightness和getBrightness分别用来设置屏幕亮度和改变屏幕亮度
- 有三个文本控件分别用来显示传感器精度、传感器值和屏幕亮度值

注：屏幕亮度值范围为：0-255

###Camera_03
- com.camera.AzCamera 实现摄像头功能
- com.light.AzChangeSizeByLight 实现改变SurfaceView预览框大小的功能（即留出空白的补光功能）
- com.light.AzLight 实现光线感应功能
- com.log.AzLog 主要是Android调试阶段输出统一Tag的日志

这个Demo目前还未完全完成，Setting界面和Save界面都没有实现。

###Camera_04_face_detection
- 使用Android自带的系统API FaceDetection实现了一下人脸检测

这个人脸检测的算法是识别出人脸的眼睛，然后以两眼间的距离为边长，两只眼睛为中心画一个正方形。
正因为简单，所以效果十分不好。
