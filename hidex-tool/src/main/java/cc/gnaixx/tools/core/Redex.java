package cc.gnaixx.tools.core;

import cc.gnaixx.tools.tools.FileUtil;

import static cc.gnaixx.tools.tools.Constants.HIDEX_FILE;
import static cc.gnaixx.tools.tools.Constants.REDEX_FILE;

/**
 * 名称: Redex
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/29
 */

public class Redex {

    public static void main(String[] args) {
        //input
        byte[] dexBuff = FileUtil.read(HIDEX_FILE);

        //hide dex
        RedexHandle handle = new RedexHandle(dexBuff);
        dexBuff = handle.redex();

        //output
        FileUtil.write(REDEX_FILE, dexBuff);
    }
}
