package cc.gnaixx.hidex_load.tool;

/**
 * 名称: NativeTool
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/2/20
 */

public class NativeTool {
    static {
        System.loadLibrary("hidex-load-lib");
    }

    public static native int custOpenDexFile(byte[] dexBytes, int dexLen);
}
