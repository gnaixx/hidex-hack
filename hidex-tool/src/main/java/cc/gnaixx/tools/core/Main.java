package cc.gnaixx.tools.core;


import java.io.File;

import cc.gnaixx.tools.model.dex.DexFile;
import cc.gnaixx.tools.tools.FileUtil;

import static cc.gnaixx.tools.model.DexCon.CHECKSUM_LEN;
import static cc.gnaixx.tools.model.DexCon.CHECKSUM_OFF;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_LEN;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_OFF;
import static cc.gnaixx.tools.tools.Encrypt.binToHex;
import static cc.gnaixx.tools.tools.Encrypt.binToHex_Lit;
import static cc.gnaixx.tools.tools.Encrypt.signature;
import static cc.gnaixx.tools.tools.Encrypt.checksum;
import static cc.gnaixx.tools.tools.Log.log;
import static cc.gnaixx.tools.tools.StreamUtil.replace;

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

        DexFile dexFile = new DexFile();
        dexFile.read(dexbs);
        log(dexFile.toJsonStr());
        log("dex_len", dexLen);

        //修复校验
        byte[] signature = signature(dexbs, SIGNATURE_LEN + SIGNATURE_OFF);
        replace(dexbs, signature, SIGNATURE_OFF, SIGNATURE_LEN);
        byte[] checksum = checksum(dexbs, CHECKSUM_LEN + CHECKSUM_OFF);
        replace(dexbs, checksum, CHECKSUM_OFF, CHECKSUM_LEN);
        log("signature", binToHex(signature));
        log("checksum", binToHex_Lit(checksum));

        //output
        //FileUtil.write(outputFile, dexbs);
    }

}
