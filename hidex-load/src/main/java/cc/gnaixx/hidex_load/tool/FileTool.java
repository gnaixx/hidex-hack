package cc.gnaixx.hidex_load.tool;

import android.content.Context;

import java.io.InputStream;

/**
 * 名称: FileTool
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/2/20
 */

public class FileTool {
    //获取文件流
    public static byte[] readAssets(Context context, String filename){
        try {
            InputStream is = context.getResources().getAssets().open(filename);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            is.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
