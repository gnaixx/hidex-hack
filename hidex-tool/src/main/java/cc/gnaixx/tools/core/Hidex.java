package cc.gnaixx.tools.core;


import java.util.List;
import java.util.Map;

import cc.gnaixx.tools.tools.FileUtil;

import static cc.gnaixx.tools.tools.Constants.CONFIG_FILE;
import static cc.gnaixx.tools.tools.Constants.SAMPL_FILE;
import static cc.gnaixx.tools.tools.Constants.HIDEX_FILE;

public class Hidex {

    public static void main(String[] args) {
        //input
        byte[] dexBuff = FileUtil.read(SAMPL_FILE);
        Map<String, List<String>> config = FileUtil.readConfig(CONFIG_FILE);

        //hide dex
        HidexHandle handle = new HidexHandle(dexBuff, config);
        dexBuff = handle.hidex();

        //output
        FileUtil.write(HIDEX_FILE, dexBuff);
    }
}
