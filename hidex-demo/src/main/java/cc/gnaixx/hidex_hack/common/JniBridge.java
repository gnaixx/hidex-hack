package cc.gnaixx.hidex_hack.common;

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

    public static native int redex(AssetManager asset, String source, String path, String target);
}
