package cc.gnaixx.tools.core;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import cc.gnaixx.tools.util.FileUtil;

import static cc.gnaixx.tools.util.Constants.CONFIG_FILE;
import static cc.gnaixx.tools.util.Constants.HIDEX_FILE;
import static cc.gnaixx.tools.util.Constants.REDEX_FILE;
import static cc.gnaixx.tools.util.Constants.INPUT_FILE;
import static cc.gnaixx.tools.util.Encrypt.checksum;
import static cc.gnaixx.tools.util.Encrypt.signature;
import static cc.gnaixx.tools.util.FileUtil.addPrefix;
import static cc.gnaixx.tools.util.FileUtil.checkFile;
import static cc.gnaixx.tools.util.Trans.binToHex;

/**
 * 名称: Main
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/1/18
 */

public class Main {
    private static final String ACTION_HIDEX = "hidex";
    private static final String ACTION_REDEX = "redex";

    //0.action 1.input 2.config
    public static void main(String[] args) {
        String action = ACTION_HIDEX;
        if (args != null && args.length != 0) {
            action = args[0];
            //check action
            if(!action.equals(ACTION_HIDEX) && !action.equals(ACTION_REDEX)){
                System.out.println("Action only hidex|redex");
                System.exit(1);
            }
            //check input file
            if(args[1]==null || !checkFile(args[1])){
                System.out.println("Please enter the correct dex file");
                System.exit(1);
            }else{
                INPUT_FILE = args[1];
            }
            //generate output file and check config
            if (action.equals(ACTION_HIDEX)) {
                HIDEX_FILE = addPrefix(INPUT_FILE, ACTION_HIDEX);
                if(args[2]==null || !checkFile(args[2])){
                    System.out.println("Please enter the correct config file");
                    System.exit(1);
                }else{
                    CONFIG_FILE = args[2];
                }
            } else if (action.equals(ACTION_REDEX)) {
                REDEX_FILE = addPrefix(INPUT_FILE, ACTION_REDEX);
            }
        } else {
            System.out.print("input action(hidex|redex):");
            Scanner scanner = new Scanner(System.in);
            action = scanner.nextLine();
        }

        if (action.equals(ACTION_HIDEX)) {
            new Main().hidex();
        } else {
            new Main().redex();
        }
    }

    //加密
    private void hidex() {
        byte[] dexBuff = FileUtil.read(INPUT_FILE);
        Map<String, List<String>> config = FileUtil.readConfig(CONFIG_FILE);

        //hide dex
        HidexHandle handle = new HidexHandle(dexBuff, config);
        dexBuff = handle.hidex();

        //output
        FileUtil.write(HIDEX_FILE, dexBuff);
    }

    //解密
    private void redex() {
        //input
        byte[] dexBuff = FileUtil.read(HIDEX_FILE);

        //restore dex
        RedexHandle handle = new RedexHandle(dexBuff);
        dexBuff = handle.redex();

        //output
        FileUtil.write(REDEX_FILE, dexBuff);
    }
}
