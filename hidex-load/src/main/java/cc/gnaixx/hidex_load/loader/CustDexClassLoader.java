package cc.gnaixx.hidex_load.loader;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

import cc.gnaixx.hidex_load.tool.NativeTool;
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
    private Class mCookieType;

    public CustDexClassLoader(Context context, byte[] dexBytes) {
        this.mContext = context;

        Method[] methods = DexFile.class.getDeclaredMethods();
        for (Method method : methods) {
            Class[] paramTypes = method.getParameterTypes();
            String paramStr = "";
            for (Class paramType : paramTypes) {
                paramStr += "," + paramType.getSimpleName();
            }
            paramStr = paramStr.length() == 0 ? paramStr : paramStr.substring(1);
            Log.d(TAG, method.getReturnType().getSimpleName() + " " + method.getName() + "(" + paramStr + ")");
            Log.d(TAG, "---------------------------------");
        }
        Object cookie = null;

        /*//DexFile.openDexFile(byte[]) 在4.0-4.3存在，后续版本被google删了
        boolean hasOpenDexFile = hasMethod(
                DexFile.class.getName(),
                "openDexFile",
                new Class[]{byte[].class});
        Log.i(TAG, "VERSION:" + Build.VERSION.RELEASE + ", API:" + Build.VERSION.SDK_INT);
        Log.i(TAG, "has openDexFile(byte[]):" + hasOpenDexFile);

        if (hasOpenDexFile) { //4.0-4.2处理方式
            cookie = RefInvoke.invokeDeclaredStaticMethod(
                    DexFile.class.getName(),
                    "openDexFile",
                    new Class[]{byte[].class},
                    new Object[]{dexBytes});
        } else {
            *//*2.3以下系统通过openDexFile(String, String, int)
            *4.2-4.4系统通过 jni 调用 Dalvik_dalvik_system_DexFile_openDexFile
            *5.0 以上系统未找到实现方式，目前反射调用 openDexFile(String, String, int)
             *//*
            cookie = NativeTool.custOpenDexFile(context, dexBytes, dexBytes.length);//调用native方法
        }*/

        cookie = NativeTool.custOpenDexFile(context, dexBytes, dexBytes.length);
        if (cookie == null) { //没有获取到cookie
            Log.d(TAG, "Cookie is null");
        } else {
            Log.d(TAG, "Cookie is:" + cookie);
            if(cookie instanceof Integer){
                this.mCookieType = int.class;
            }else if(cookie instanceof Long){
                this.mCookieType = long.class;
            }else{
                this.mCookieType = Object.class;
            }
            this.mCookie = cookie;
        }
    }

    //获取类
    public Class findClass(String name) throws ClassNotFoundException {
        Log.i(TAG, "findClass: " + name);
        Class clazz = null;
        String classNameList[] = getClassNameList();
        for (int i = 0; i < classNameList.length; i++) {
            Log.i(TAG, "className:" + classNameList[i]);
            Class cla = defineClass(classNameList[i].replace('.', '/'), mContext.getClassLoader());
            if (classNameList[i].equals(name)) {
                clazz = cla;
            }
        }
        return clazz;
    }

    //获取所有类
    private String[] getClassNameList() {
        String classNameList[] = (String[]) RefInvoke.invokeDeclaredStaticMethod(
                DexFile.class.getName(),
                "getClassNameList",
                new Class[]{this.mCookieType},
                new Object[]{this.mCookie});
        return classNameList;
    }

    //定义类
    private Class defineClass(String className, ClassLoader loader) {
        Log.i(TAG, "defineClass:" + className);

        boolean hasDefineClass = RefInvoke.hasMethod(
                DexFile.class.getName(),
                "defineClass",
                new Class[]{String.class, ClassLoader.class, int.class});

        Class clazz = null;
        if (hasDefineClass) {
            //4.3以前为defineClass(String, ClassLoader, int)
            clazz = (Class) RefInvoke.invokeDeclaredStaticMethod(
                    DexFile.class.getName(),
                    "defineClass",
                    new Class[]{String.class, ClassLoader.class, int.class},
                    new Object[]{className, loader, this.mCookie});
        } else {
            //4.4 为 defineClassNative(String, ClassLoader, int)
            //5.0-5.1 为 defineClassNative(String, ClassLoader, long)
            //6.0+ 为 defineClassNative(String, ClassLoader, Object)
            clazz = (Class) RefInvoke.invokeDeclaredStaticMethod(
                    DexFile.class.getName(),
                    "defineClassNative",
                    new Class[]{String.class, ClassLoader.class, this.mCookieType},
                    new Object[]{className, loader, this.mCookie});
        }
        return clazz;
    }
}
