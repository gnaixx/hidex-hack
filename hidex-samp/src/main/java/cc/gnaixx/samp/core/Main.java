package cc.gnaixx.samp.core;

//import android.util.Log;

/**
 * 名称: Main
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/16
 */

public class Main {
    private final static String  TAG    = "GNAIXX";
    private final static boolean DEBUG  = true;
    private final static boolean DEBU   = false;
    private final static int     FLAG   = 1;
    public  final static int     COUNT  = 2;
    private final static byte[]  NAME = new byte[]{0x49, 0x48};

    public static void main(String args[]){
        //Log.d(TAG, "begin main");
        System.out.println("begin main");
        System.out.println(new String(NAME));

    }
}
