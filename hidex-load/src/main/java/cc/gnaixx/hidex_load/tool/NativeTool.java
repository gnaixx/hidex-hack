package cc.gnaixx.hidex_load.tool;

import android.content.Context;

/**
 * 名称: NativeTool
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/2/20
 */

public class NativeTool {
    static {
        System.loadLibrary("hidex_load");
    }

    public static native Object custOpenDexFile(Context context, byte[] dexBytes, int dexLen);
}
