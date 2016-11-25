package cc.gnaixx.tools.core;

import java.util.List;
import java.util.Map;

import cc.gnaixx.tools.model.dex.DexFile;
import cc.gnaixx.tools.model.dex.Header;
import cc.gnaixx.tools.model.dex.cladef.ClassDefs;
import cc.gnaixx.tools.tools.Constants;

import static cc.gnaixx.tools.model.DexCon.CHECKSUM_LEN;
import static cc.gnaixx.tools.model.DexCon.CHECKSUM_OFF;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_LEN;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_OFF;
import static cc.gnaixx.tools.tools.Encrypt.checksum;
import static cc.gnaixx.tools.tools.Encrypt.signature;
import static cc.gnaixx.tools.tools.Log.log;
import static cc.gnaixx.tools.tools.StreamUtil.replace;
import static cc.gnaixx.tools.tools.Trans.binToHex;
import static cc.gnaixx.tools.tools.Trans.binToHex_Lit;
import static cc.gnaixx.tools.tools.Trans.intToHex;
import static cc.gnaixx.tools.tools.Trans.pathToPackages;

/**
 * 名称: Handle
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class Handle {

    private byte[] dexBuff; //dex 二进制流
    private DexFile dexFile; //dex 对象
    private Map<String, List<String>> config;  //配置


    public Handle(byte[] dexBuff, Map<String, List<String>> config) {
        this.dexBuff = dexBuff;
        this.dexFile = new DexFile();
        this.config = config;
    }

    public byte[] hidex() {
        //创建dex 对象
        dexFile.read(dexBuff);
        log(dexFile.toJsonStr());
        log("config", config.toString());

        //hidex
        hackHeader();
        hackClassDef();

        //修复校验
        checkout();
        return dexBuff;
    }

    //修复校验
    private void checkout() {
        log("old_signature", binToHex(dexFile.header.signature));
        log("old_checksum", intToHex(dexFile.header.checksum));

        byte[] signature = signature(dexBuff, SIGNATURE_LEN + SIGNATURE_OFF);
        replace(dexBuff, signature, SIGNATURE_OFF, SIGNATURE_LEN);
        byte[] checksum = checksum(dexBuff, CHECKSUM_LEN + CHECKSUM_OFF);
        replace(dexBuff, checksum, CHECKSUM_OFF, CHECKSUM_LEN);

        log("new_signature", binToHex(signature));
        log("new_checksum", binToHex_Lit(checksum));
    }

    //修改header
    private void hackHeader() {
        Header header = dexFile.header;
        header.hack(dexBuff);
    }

    private void hackClassDef() {
        ClassDefs classDefs = dexFile.classDefs;
        ClassDefs.ClassDef classDefItem[] = classDefs.classDefs;
        List<String> hackClass = config.get(Constants.HIDE_STATIC_VAL);

        for (int i = 0; i < classDefItem.length; i++) {
            String dexName = dexFile.typeIds.getString(dexFile, classDefItem[i].classIdx);
            dexName = pathToPackages(dexName);
            for (int j = 0; j < hackClass.size(); j++) {
                String confName = hackClass.get(j);
                if (dexName.equals(confName)) {
                    classDefItem[i].staticValueOff = 0;
                    log("hack", hackClass.get(j));
                }
            }
        }
        classDefs.hack(dexBuff);
    }
}
