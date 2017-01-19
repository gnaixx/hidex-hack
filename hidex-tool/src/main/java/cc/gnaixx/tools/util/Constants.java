package cc.gnaixx.tools.util;

import java.io.File;

/**
 * 名称: Constants
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/25
 */

public class Constants {
    /************************ 文件路径 ************************/
    private static final String DEX_NAME    = "samp.dex";
    private static final String HIDEX_NAME  = "hidex-samp.dex";
    private static final String REDEX_NAME  = "redex-hidex-samp.dex";
    private static final String CONFIG_NAME = "hidex.conf";
    private static final String OUTPUT_DIR  = "output";

    public static String USER_DIR;
    public static String INPUT_FILE;
    public static String HIDEX_FILE;
    public static String REDEX_FILE;
    public static String CONFIG_FILE;

    static {
        USER_DIR    = System.getProperty("user.dir");
        INPUT_FILE  = USER_DIR + File.separator + OUTPUT_DIR + File.separator + DEX_NAME;
        HIDEX_FILE  = USER_DIR + File.separator + OUTPUT_DIR + File.separator + HIDEX_NAME;
        REDEX_FILE  = USER_DIR + File.separator + OUTPUT_DIR + File.separator + REDEX_NAME;
        CONFIG_FILE = USER_DIR + File.separator + OUTPUT_DIR + File.separator + CONFIG_NAME;
        Log.log("path", USER_DIR);
    }

    /************************ 配置参数 ************************/
    public static final String HACK_CLASS      = "hack_class";         //隐藏成员函数
    public static final String HACK_SF_VAL     = "hack_sf_val";        //隐藏静态变量值
    public static final String HACK_ME_SIZE    = "hack_me_size";       //隐藏成员函数
    public static final String HACK_ME_DEF     = "hack_me_def";        //重复成员函数定义



}
