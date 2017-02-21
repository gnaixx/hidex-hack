package cc.gnaixx.hidex_load.tool;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static cc.gnaixx.hidex_load.tool.Constant.TAG;

/**
 * 名称: RefInvoke
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/2/20
 */

public class RefInvoke {

    public static boolean hasMethod(String className, String methodName, Class[] paramType){
        try {
            Class clazz = Class.forName(className);
            clazz.getDeclaredMethod(methodName, paramType);
            return true;
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class not found: " + className);
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            String paramStr = "";
            for(int i=0; i<paramType.length; i++){
                paramStr += "," + paramType[i].getSimpleName();
            }
            paramStr = paramStr.substring(1);
            Log.e(TAG, "No such method: " + className +"."+methodName + "(" + paramStr+")");
            e.printStackTrace();
            return false;
        }
    }

    //反射调用公共静态方法
    public static Object invokeStaticMethod(
            String className,
            String methodName,
            Class[] paramType,
            Object[] param) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, paramType);
            Object result = method.invoke(null, param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //反射调用任意静态方法
    public static Object invokeDeclaredStaticMethod(
            String className,
            String methodName,
            Class[] paramType,
            Object[] param) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, paramType);
            method.setAccessible(true);
            Object result = method.invoke(null, param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
