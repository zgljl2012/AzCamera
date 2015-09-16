package com.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;

/**
 * 负责一些图片转换的操作
 * Created by 廖金龙 on 2015/9/9.
 */
public class ImageUtil {

    /**
     * 使用Android里的矩阵旋转图像
     * @param bm
     * @param radius
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bm, int radius) {
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        matrix.postRotate(radius);
        return Bitmap.createBitmap(bm, 0, 0,
                bm.getWidth() , bm.getHeight(), matrix, true);
    }

    /**
     * 使用Android矩阵缩放图像
     * @param bm
     * @param widthRatio
     * @param heightRatio
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bm, float widthRatio, float heightRatio) {
        Matrix matrix = new Matrix();
        matrix.postScale(widthRatio, heightRatio);
        return Bitmap.createBitmap(bm, 0, 0,
                bm.getWidth() , bm.getHeight(), matrix, true);
    }

    /**
     * 使用JavaCV翻转图像
     * @param bitmap    待翻转图像
     * @param filpMode  翻转模式，0沿x轴翻转，1沿y轴翻转，-1沿原点翻转
     * @return
     */
    public static Bitmap filpBitmap(Bitmap bitmap, int filpMode) {
        IplImage iplImage = ImageUtil.bitmapToIplImage(bitmap);
        IplImage img = cvCreateImage(cvSize(iplImage.width(),iplImage.height()),iplImage.depth(),
                iplImage.nChannels());
        cvFlip(iplImage, img, filpMode);
        return IplImageToBitmap(img);
    }

    /**
     * IplImage转化为Bitmap
     * @param iplImage
     * @return
     */
    public static Bitmap IplImageToBitmap(IplImage iplImage) {
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(iplImage.width(), iplImage.height(),
                Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(iplImage.getByteBuffer());
        return bitmap;
    }

    /**
     * Bitmap转化为IplImage
     * @param bitmap
     * @return
     */
    public static IplImage bitmapToIplImage(Bitmap bitmap) {
        IplImage iplImage;
        iplImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(),
                IPL_DEPTH_8U, 4);
        bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
        return iplImage;
    }

    /**
     * 裁剪图片
     * @param bitmap
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public static Bitmap cutBitmap(Bitmap bitmap, int x, int y, int width,
                            int height)  {
        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    /**
     * 将图片保存存在磁盘上
     * @param data
     */
    public static void savePictureAsJPG(byte[] data, String path, String filename) {
        final Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
                data.length);
        savePictureAsJPG(bm, path, filename);
    }

    public static void savePictureAsJPG(Bitmap bm, String path, String filename) {
        // 创建AzCamera文件夹
        path = Environment.getExternalStorageDirectory()+path;
        File pathFile = new File(path);
        if(!pathFile.exists()) {
            pathFile.mkdirs();
        }

        // 创建一个位于SD卡上的文件
        File file = new File(path,filename);
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

}
