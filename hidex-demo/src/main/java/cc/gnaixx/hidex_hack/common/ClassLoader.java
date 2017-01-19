package cc.gnaixx.hidex_hack.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;

import cc.gnaixx.hidex_libs.inter.Entrance;
import dalvik.system.DexClassLoader;

import static cc.gnaixx.hidex_hack.config.Constant.ENTRANCE;
import static cc.gnaixx.hidex_hack.config.Constant.TAG;
import static cc.gnaixx.hidex_hack.common.ToolKit.copyToCache;

/**
 * 名称: LoadHelper
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/1/19
 */

public class ClassLoader {

    private Context context;

    public ClassLoader(Context context) {
        this.context = context;
    }

    //dex还原
    public void redex(String source, String target) {
        String path = context.getFilesDir().getAbsolutePath();
        AssetManager assetMgr = context.getAssets();
        int result = JniBridge.redex(assetMgr, source, path, target);
        Log.d(TAG, "Redex result: " + result);
    }

    //
    public Entrance load(String filename) {
        File dex = new File(context.getFilesDir().getAbsolutePath(), filename);
        if (dex.exists()) {
            DexClassLoader loader = new DexClassLoader(
                    dex.getPath(),
                    context.getCacheDir().getAbsolutePath(),
                    null,
                    context.getClassLoader());
            try {
                Class clazz = loader.loadClass(ENTRANCE);
                Entrance entrance = (Entrance) clazz.newInstance();
                Log.i(TAG, entrance.getStaticFields());
                return entrance;
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
