package cc.gnaixx.tools.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.gnaixx.tools.model.dex.DexFile;
import cc.gnaixx.tools.model.dex.Header;
import cc.gnaixx.tools.model.dex.cladef.ClassDefs;
import cc.gnaixx.tools.model.HackPoint;
import cc.gnaixx.tools.tools.BufferUtil;
import cc.gnaixx.tools.tools.Constants;

import static cc.gnaixx.tools.model.DexCon.CHECKSUM_LEN;
import static cc.gnaixx.tools.model.DexCon.CHECKSUM_OFF;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_LEN;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_OFF;
import static cc.gnaixx.tools.tools.Encrypt.checksum;
import static cc.gnaixx.tools.tools.Encrypt.signature;
import static cc.gnaixx.tools.tools.Log.log;
import static cc.gnaixx.tools.tools.BufferUtil.replace;
import static cc.gnaixx.tools.tools.Trans.binToHex;
import static cc.gnaixx.tools.tools.Trans.binToHex_Lit;
import static cc.gnaixx.tools.tools.Trans.hackpToBin;
import static cc.gnaixx.tools.tools.Trans.intToHex;
import static cc.gnaixx.tools.tools.Trans.pathToPackages;

/**
 * 名称: HidexHandle
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class HidexHandle {

    private Map<String, List<String>> config; //配置
    private List<HackPoint> hackPoints;       //修改的信息
    private byte[] dexBuff;                   //dex 二进制流
    private DexFile dexFile;                  //dex 对象


    public HidexHandle(byte[] dexBuff, Map<String, List<String>> config) {
        this.dexBuff = dexBuff;
        this.dexFile = new DexFile();
        this.config = config;
        this.hackPoints = new ArrayList<>();
    }

    public byte[] hidex() {
        //创建dex 对象
        dexFile.read(dexBuff);
        log(dexFile.toJsonStr());
        log("config", config.toString());

        hackClassDef();
        hackHeader();   //修改头部信息，必须放在最后
        appendHackPoint();  //添加hackpoint
        checkout(); //修复校验
        return dexBuff;
    }

    private void appendHackPoint() {
        byte[] pointsBuff = new byte[]{};
        for (int i = 0; i < hackPoints.size(); i++) {
            byte[] pointBuff = hackpToBin(hackPoints.get(i));
            pointsBuff = BufferUtil.append(pointsBuff, pointBuff, pointBuff.length);
        }
        dexBuff = BufferUtil.append(dexBuff, pointsBuff, pointsBuff.length);
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
        header.fileSize = this.dexBuff.length;  //修改文件长度
        header.hack(dexBuff);
    }

    //修改ClassDfs
    private void hackClassDef() {
        ClassDefs classDefs = dexFile.classDefs;
        ClassDefs.ClassDef classDefItem[] = classDefs.classDefs;

        List<String> confClassStaticFields = config.get(Constants.HACK_STATIC_VAL);
        List<String> confClass = config.get(Constants.HACK_CLASS);

        //hack 静态变量
        hackClassStaticFields(classDefItem, confClassStaticFields);
        //hack 成员函数
        hackClass(classDefItem, confClass);
        classDefs.hack(dexBuff);
    }

    //隐藏整个类的定义
    private void hackClass(ClassDefs.ClassDef[] classDefItem, List<String> conf) {
        if (conf == null) {
            return;
        }
        for (int i = 0; i < conf.size(); i++) {
            String confName = conf.get(i);
            boolean isDef = false;
            for (int j = 0; j < classDefItem.length; j++) {
                String dexName = dexFile.typeIds.getString(dexFile, classDefItem[j].classIdx);
                dexName = pathToPackages(dexName);
                if (dexName.equals(confName)) {
                    HackPoint point = classDefItem[j].classDataOff;
                    addHackPoint(point.type, point.offset, point.value); //添加修改点
                    classDefItem[j].classDataOff.value = 0;
                    log("hack_class", conf.get(i));
                    isDef = true;
                }
            }
            if (isDef == false) {
                log("warning", "con't find class:" + confName);
            }
        }
    }

    //隐藏静态变量初始化
    private void hackClassStaticFields(ClassDefs.ClassDef[] classDefItem, List<String> conf) {
        if (conf == null) {
            return;
        }
        for (int i = 0; i < conf.size(); i++) {
            String confName = conf.get(i);
            boolean isDef = false;
            for (int j = 0; j < classDefItem.length; j++) {
                String dexName = dexFile.typeIds.getString(dexFile, classDefItem[j].classIdx);
                dexName = pathToPackages(dexName);
                if (dexName.equals(confName)) {
                    HackPoint point = classDefItem[j].staticValueOff;
                    addHackPoint(point.type, point.offset, point.value); //添加修改点
                    classDefItem[j].staticValueOff.value = 0;
                    log("hack_field", conf.get(i));
                    isDef = true;
                }
            }
            if (isDef == false) {
                log("warning", "con't find class:" + confName);
            }
        }
    }

    //添加hackpoint
    private void addHackPoint(int type, int off, int val) {
        this.hackPoints.add(new HackPoint(type, off, val));
    }
}
