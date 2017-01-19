package cc.gnaixx.tools.util;

/**
 * 名称: Log
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/21
 */

public class Log {

    public static void log(String tag, Object msg) {
        System.out.println(String.format("%-15s ---> %s", tag, msg));
    }

    public static void log(Object msg){
        System.out.println(msg);
    }
}
