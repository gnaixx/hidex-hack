package cc.gnaixx.tools.core;


import java.io.File;
import java.util.List;
import java.util.Map;

import cc.gnaixx.tools.tools.FileUtil;

public class Main {
    private static final String DEX_NAME    = "samp.dex";
    private static final String HIDEX_NAME  = "hidex.dex";
    private static final String CONFIG_NAME = "hidex.conf";
    private static final String OUTPUT_DIR  = "output";

    private static String userDir;
    private static String inputFile;
    private static String outputFile;
    private static String configFile;

    static {
        userDir = System.getProperty("user.dir");
        inputFile  = userDir + File.separator + OUTPUT_DIR + File.separator + DEX_NAME;
        outputFile = userDir + File.separator + OUTPUT_DIR + File.separator + HIDEX_NAME;
        configFile = userDir + File.separator + OUTPUT_DIR + File.separator + CONFIG_NAME;
    }


    public static void main(String[] args) {
        //input
        byte[] dexBuff = FileUtil.read(inputFile);
        Map<String, List<String>> config = FileUtil.readConfig(configFile);

        //hide dex
        Handle handle = new Handle(dexBuff, config);
        dexBuff = handle.hidex();

        //output
        FileUtil.write(outputFile, dexBuff);
    }
}
