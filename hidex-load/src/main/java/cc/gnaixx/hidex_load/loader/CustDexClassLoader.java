package cc.gnaixx.hidex_load.loader;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import cc.gnaixx.hidex_load.tool.Constant;
import cc.gnaixx.hidex_load.tool.RefInvoke;
import dalvik.system.DexFile;

import static cc.gnaixx.hidex_load.tool.Constant.TAG;
import static cc.gnaixx.hidex_load.tool.RefInvoke.hasMethod;

/**
 * 名称: CustDexClassLoader
 * 描述: 自定义的 DexClassLoader
 *
 * @author xiangqing.xue
 * @date 2017/2/20
 */

public class CustDexClassLoader {
    private Context mContext;
    private Object mCookie;

    public CustDexClassLoader(Context context, byte[] dexBytes){
        this.mContext = context;

        //DexFile.openDexFile(byte[]) 在4.0-4.2存在，后续版本被google删了
        boolean hasOpenDexFile = hasMethod(
                DexFile.class.getName(),
                "openDexFile",
                new Class[] {byte[].class});
        Log.i(TAG, "VERSION:" + Build.VERSION.RELEASE + ", API:" + Build.VERSION.SDK_INT);

        Object cookie = null;
        if(hasOpenDexFile){ //4.0-4.2处理方式
            cookie = RefInvoke.invokeDeclaredStaticMethod(
                    DexFile.class.getName(),
                    "openDexFile",
                    new Class[] {byte[].class},
                    new Object[] {dexBytes});
        }else{

        }

        if(cookie == null){ //没有获取到cookie
            Log.d(TAG, "Cookie is null");
        }
    }

    //获取类
    public Class findClass(String name) throws ClassNotFoundException {
        Log.i(TAG, "findClass: " + name);
        Class clazz = null;
        String classNameList[] = getClassNameList((int)this.mCookie);

        for(int i=0; i<classNameList.length; i++){
            Log.i(TAG, "className:" + classNameList[i]);
            Class cla = defineClass(classNameList[i].replace('.','/'), mContext.getClassLoader(), (int)this.mCookie);
            if(classNameList[i].equals(name)){
                clazz = cla;
            }
        }
        return clazz;
    }

    //获取所有类
    private String[] getClassNameList(int cookie){
        String classNameList[] = (String[]) RefInvoke.invokeDeclaredStaticMethod(
                DexFile.class.getName(),
                "getClassNameList",
                new Class[]{int.class},
                new Object[]{cookie});
        return classNameList;
    }

    //定义类
    private Class defineClass(String className, ClassLoader loader, int cookie){
        Log.i(TAG, "defineClass:" + className);
        Class clazz = (Class) RefInvoke.invokeDeclaredStaticMethod(
                DexFile.class.getName(),
                "defineClass",
                new Class[] {String.class, ClassLoader.class, int.class},
                new Object[]{className, loader, cookie});
        return clazz;
    }
}
