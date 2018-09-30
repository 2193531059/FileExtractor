package com.slive.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/9/30.
 */

public class FileUtil {
    public static String getFilePath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "FILE_EXTRACTOR" + File.separator;
    }

    /**
     * 创建文件或文件夹
     *
     * @param fileName 文件名或问文件夹名
     */
    public static void createFile(String fileName) {
        File file = new File(getFilePath() + fileName);
        if (fileName.contains(".")) {
            // 说明包含，即使创建文件, 返回值为-1就说明不包含.,即使文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            // 创建文件夹
            file.mkdirs();
        }

    }
}
