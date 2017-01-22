package cc.gnaixx.hidex_libs.common;

import android.content.res.AssetManager;

/**
 * 名称: NativeHelper
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/12/15
 */

public class JniBridge {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("hidex");
    }

    public static native int redexFromAssets(AssetManager asset, String sourceName,
                                             String targetPath, String targetName);

    public static native int redexFromFile(String sourcePath, String sourceName,
                                           String targetPath, String targetName);
}
