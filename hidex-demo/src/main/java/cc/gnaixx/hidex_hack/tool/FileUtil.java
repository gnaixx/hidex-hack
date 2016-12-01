package cc.gnaixx.hidex_hack.tool;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 名称: FileUtil
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/29
 */

public class FileUtil {

    public static void copyToCache(Context context, String filename) {
        String cachePath = context.getFilesDir().getAbsolutePath();
        try {
            InputStream is = context.getResources().getAssets().open(filename);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            is.close();

            String dexPath = cachePath + File.separator + new String(filename);
            FileOutputStream fos = new FileOutputStream(dexPath);
            BufferedOutputStream outputStream = new BufferedOutputStream(fos);
            outputStream.write(data);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
