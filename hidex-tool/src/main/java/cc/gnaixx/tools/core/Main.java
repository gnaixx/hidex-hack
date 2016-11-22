package cc.gnaixx.tools.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import cc.gnaixx.tools.model.DexFile;

import static cc.gnaixx.tools.Util.checksum;
import static cc.gnaixx.tools.Util.getUint;
import static cc.gnaixx.tools.Util.getUleb;
import static cc.gnaixx.tools.Util.log;
import static cc.gnaixx.tools.Util.signature;
import static cc.gnaixx.tools.Util.subdex;
import static cc.gnaixx.tools.tools.Encrypt.checksum;
import static cc.gnaixx.tools.tools.Encrypt.signature;
import static cc.gnaixx.tools.tools.Log.log;

public class Main {

    public static void main(String[] args){
        String path = System.getProperty("user.dir");
        String filename = path + File.separator + "Code.dex";
        File dexfile = new File(filename);

        try {
            FileInputStream fis = new FileInputStream(dexfile);
            int len = fis.available();
            byte dexEntry[] = new byte[len];
            int count = fis.read(dexEntry);
            fis.close();
            log("len -> " + count);

            DexFile dexFile = new DexFile();
            readHeader(dexFile, dexEntry);
            readStringIds(dexFile, dexEntry);

            //修复校验
            signature(dexEntry, (2+1+5)*4, count-(2+1+5)*4);
            checksum(dexEntry, (2+1)*4, count-(2+1)*4);

            //输出
            FileOutputStream fos = new FileOutputStream(dexfile);
            fos.write(dexEntry);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取header
    public static void readHeader(DexFile dexFile, byte[] dexEntry){
        dexFile.header.magic = subdex(dexEntry, 0, 8);
        dexFile.header.checksum = getUint(dexEntry, 8);
        dexFile.header.signature = subdex(dexEntry, 12, 20);
        dexFile.header.fileSize = getUint(dexEntry, 32);
        dexFile.header.headerSize = getUint(dexEntry, 36);
        dexFile.header.endianTag = getUint(dexEntry, 40);
        dexFile.header.linkSize = getUint(dexEntry, 44);
        dexFile.header.linkOff = getUint(dexEntry, 48);
        dexFile.header.mapOff = getUint(dexEntry, 52);
        dexFile.header.stringIdsSize = getUint(dexEntry, 56);
        dexFile.header.stringIdsOff = getUint(dexEntry, 60);
        dexFile.header.typeIdsSize = getUint(dexEntry, 64);
        dexFile.header.typeIdsOff = getUint(dexEntry, 68);
        dexFile.header.protoIdsSize = getUint(dexEntry, 72);
        dexFile.header.protoIdsOff = getUint(dexEntry, 76);
        dexFile.header.fieldIdsSize = getUint(dexEntry, 80);
        dexFile.header.fieldIdsOff = getUint(dexEntry, 84);
        dexFile.header.methodIdsSize = getUint(dexEntry, 88);
        dexFile.header.methodIdsOff = getUint(dexEntry, 92);
        dexFile.header.classDefsSize = getUint(dexEntry, 96);
        dexFile.header.classDefsOff = getUint(dexEntry, 100);
        dexFile.header.dataSize = getUint(dexEntry, 104);
        dexFile.header.dataOff = getUint(dexEntry, 108);
    }

    //读取String 表
    public static void readStringIds(DexFile dexFile, byte[] dexEntry){
        int offset = dexFile.header.stringIdsOff;

        for(int i=0; i<dexFile.header.stringIdsSize; i++){
            int stringOff = getUint(dexEntry, offset);
            int[] value = getUleb(dexEntry, stringOff);
            byte[] data = subdex(dexEntry, stringOff + value[1], value[0]);
            StringData stringData = new StringData(value[0], data);
            StringIds stringIds = new StringIds(stringOff, stringData);
            dexFile.stringIds.add(stringIds);
            offset += 4;
        }
    }
}
