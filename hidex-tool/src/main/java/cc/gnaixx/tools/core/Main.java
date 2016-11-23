package cc.gnaixx.tools.core;

import java.io.File;

import cc.gnaixx.tools.model.dex.DexFile;
import cc.gnaixx.tools.tools.FileUtil;

import static cc.gnaixx.tools.tools.Log.log;

public class Main {
    private static final String DEX_NAME = "samp.dex";
    private static final String HIDEX_NAME = "hidex.dex";
    private static final String OUTPUT_DIR = "output";
    private static String userDir;
    private static String inputFile;
    private static String outputFile;

    static {
        userDir = System.getProperty("user.dir");
        inputFile = userDir + File.separator + OUTPUT_DIR + File.separator + DEX_NAME;
        outputFile = userDir + File.separator + OUTPUT_DIR + File.separator + HIDEX_NAME;
    }


    public static void main(String[] args) {
        //input
        byte[] dexbs = FileUtil.read(inputFile);
        int dexLen = dexbs.length;
        log("dexlen -> " + dexLen);

        DexFile dexFile = new DexFile();
        dexFile.read(dexbs);
        log(dexFile.toJsonStr());


        //修复校验
        //signature(dexbs, (2 + 1 + 5) * 4, dexLen - (2 + 1 + 5) * 4);
        //checksum(dexbs, (2 + 1) * 4, dexLen - (2 + 1) * 4);

        //output
        //FileUtil.write(outputFile, dexbs);
    }

}
