package cc.gnaixx.tools.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 名称: FileUtil
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/22
 */

public class FileUtil {
    //
    public static void write(String path, byte[] data) {
        File file = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    public static byte[] read(String path) {
        File file = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            int len = fis.available();
            byte entry[] = new byte[len];
            int count = fis.read(entry);
            fis.close();
            return entry;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
