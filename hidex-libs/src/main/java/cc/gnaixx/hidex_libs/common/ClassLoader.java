package cc.gnaixx.hidex_libs.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;

import dalvik.system.DexClassLoader;


/**
 * 名称: LoadHelper
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/1/19
 */

public class ClassLoader {

    private static final String TAG = "HIDEX";

    private Context context;
    private DexClassLoader classLoader;

    public ClassLoader(Context context) {
        this.context = context;
    }

    //dex还原
    public void redexFromAssets(String sourceName, String targetPath, String targetName) {
        AssetManager assetMgr = context.getAssets();
        int result = JniBridge.redexFromAssets(assetMgr, sourceName, targetPath, targetName);
        Log.d(TAG, "Redex result: " + result);
    }

    //dex还原
    public void redexFromAssets(String sourceName, String targetName) {
        String filesPath = context.getFilesDir().getAbsolutePath();
        redexFromAssets(sourceName, filesPath, targetName);
    }

    //dex还原
    public void redexFromFile(String sourcePath, String sourceName, String targetPath, String targetName) {
        int result = JniBridge.redexFromFile(sourcePath, sourceName, targetPath, targetName);
        Log.d(TAG, "Redex result: " + result);
    }

    //
    public <T> T load(String path, String fileName, String className) {

        //先检测是否已经加载过 5.0 以前系统重复加载会造成crash
        if (classLoader != null) {
            try {
                Class clazz = classLoader.loadClass(className);
                T entrance = (T) clazz.newInstance();
                return entrance;
            } catch (Exception e) {
                Log.e(TAG, "Check " + fileName + "have class:" + className);
                e.printStackTrace();
                return null;
            }
        }else {
            try {
                String tempPath = context.getCacheDir().getAbsolutePath() + "/hidex";
                File file = new File(tempPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File dexFile = new File(path, fileName);
                if (dexFile.exists()) {
                    classLoader = new DexClassLoader(
                            dexFile.getPath(),
                            tempPath,
                            null,
                            context.getClassLoader());

                    Class clazz = classLoader.loadClass(className);
                    T entrance = (T) clazz.newInstance();
                    return entrance;
                }
                return null;
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
