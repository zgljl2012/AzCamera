package com.data;

import android.graphics.Bitmap;

import com.camera.AzLog;

import java.io.File;

/**
 * 负责应用的数据管理
 * Created by 廖金龙 on 2015/9/9.
 */
public class DataManager {

    private static DataManager data = new DataManager();

    private AzLog log = AzLog.getInstance();

    private final String compensateRatioKey = "compensateRatio";

    private DataManager() {

    }

    public static DataManager getInstance() {
        return data;
    }

    // 补光区域宽度比例
    public float compensateWidthRatio  = 0.5f;
    // 补光区域高度比例
    public float compensateHeightRatio = 0.5f;

    /******************************************
     * 待保存文件get和set方法
     ******************************************/
    public void setCompensateRatio(MySPInterface sp, String ratio) {
        sp.setValue(this.compensateRatioKey, ratio);
    }



    /*****************************************/

    /**
     * 当前拍好的照片
     */
    public Bitmap currentBmp;

    /**
     * 当前拍好的照片的名字
     */
    public String currentBmpName;

    /**
     * 当前切好的人脸的名字
     */
    public String currentFaceName;

    /**
     * 人脸图片保存路径（sd_crad为根目录）
     */
    public String faceSavePath = "/AzCamera/face";

    /**
     * 拍的照片自动保存路径
     */
    public String imgSavePath = "/AzCamera/image";


    /**
     * 删除文件
     * @param pathname 文件路径（包含文件名）
     * @return 删除成功返回true
     */
    public boolean deleteFile(String pathname) {
        log.log("Delete", "删除文件"+pathname);
        File pathFile = new File(pathname);
        if(!pathFile.exists()) {
            log.log("Delete", pathname+"不存在！");
            return false;
        }
        pathFile.delete();
        log.log("Delete", "删除文件"+pathname+"成功！");
        return true;
    }

}
