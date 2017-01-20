package cc.gnaixx.tools.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.gnaixx.tools.model.HackPoint;
import cc.gnaixx.tools.model.dex.DexFile;
import cc.gnaixx.tools.model.dex.Header;
import cc.gnaixx.tools.model.dex.cladef.ClassDefs;
import cc.gnaixx.tools.util.BufferUtil;
import cc.gnaixx.tools.util.Constants;

import static cc.gnaixx.tools.model.DexCon.CHECKSUM_LEN;
import static cc.gnaixx.tools.model.DexCon.CHECKSUM_OFF;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_LEN;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_OFF;
import static cc.gnaixx.tools.util.Encrypt.checksum_Lit;
import static cc.gnaixx.tools.util.Encrypt.signature;
import static cc.gnaixx.tools.util.Log.log;
import static cc.gnaixx.tools.util.Trans.binToHex;
import static cc.gnaixx.tools.util.Trans.hackpToBin;
import static cc.gnaixx.tools.util.Trans.intToHex;
import static cc.gnaixx.tools.util.Trans.pathToPackages;

/**
 * 名称: HidexHandle
 * 描述: write dex
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
        dexFile.read(dexBuff);  //创建dex对象
        log(dexFile.toJsonStr());
        log("config", config.toString());

        hackClassDef();
        appendHP();     //添加hackpoint
        hackHeader();   //修改头部信息，必须放在最后
        return dexBuff;
    }

    //修改ClassDfs定义
    private void hackClassDef() {
        ClassDefs classDefs = dexFile.classDefs;
        ClassDefs.ClassDef classDefItem[] = classDefs.classDefs;

        List<String> confCdOff  = config.get(Constants.HACK_CLASS);
        List<String> confSfVal  = config.get(Constants.HACK_SF_VAL);
        List<String> confMeSize = config.get(Constants.HACK_ME_SIZE);
        List<String> confMeDef  = config.get(Constants.HACK_ME_DEF);

        hackCdOff(classDefItem, confCdOff);     //write 类定义
        hackSfVal(classDefItem, confSfVal);     //write 静态变量初始化
        hackMeSize(classDefItem, confMeSize);   //write 静态变量个数
        hackMeDef(classDefItem, confMeDef);     //write 重复函数定义

        classDefs.write(dexBuff);                //将修改写回buffer
    }

    //隐藏整个类的定义
    private void hackCdOff(ClassDefs.ClassDef[] classDefItem, List<String> conf) {
        seekHP(classDefItem, conf, Constants.HACK_CLASS, new SeekCallBack() {
            @Override
            public void doHack(ClassDefs.ClassDef classDefItem, List<HackPoint> hackPoints) {
                HackPoint point = classDefItem.classDataOff.clone(); //获取类定义的偏移
                hackPoints.add(point);                       //保存原始值
                classDefItem.classDataOff.value = 0;         //修改类定义偏移为0
            }
        });
    }

    //隐藏静态变量初始化
    private void hackSfVal(ClassDefs.ClassDef[] classDefItem, List<String> conf) {
        seekHP(classDefItem, conf, Constants.HACK_SF_VAL, new SeekCallBack() {
            @Override
            public void doHack(ClassDefs.ClassDef classDefItem, List<HackPoint> hackPoints) {
                HackPoint point = classDefItem.staticValueOff.clone();  //获取静态变量数据偏移
                hackPoints.add(point);                          //添加修改点
                classDefItem.staticValueOff.value = 0;          //将静态变量的偏移改为0（隐藏赋值）
            }
        });
    }

    //隐藏函数定义
    private void hackMeSize(ClassDefs.ClassDef[] classDefItem, List<String> conf){
        seekHP(classDefItem, conf, Constants.HACK_ME_SIZE, new SeekCallBack() {
            @Override
            public void doHack(ClassDefs.ClassDef classDefItem, List<HackPoint> hackPoints) {
                HackPoint directPoint = classDefItem.classData.directMethodsSize.clone(); //同时需改虚函数和直接函数
                HackPoint virtualPoint = classDefItem.classData.virtualMethodsSize.clone();
                hackPoints.add(directPoint);
                hackPoints.add(virtualPoint);
                classDefItem.classData.directMethodsSize.value = 0;
                classDefItem.classData.virtualMethodsSize.value = 0;
            }
        });
    }

    //重复函数定义
    private void hackMeDef(ClassDefs.ClassDef[] classDefItem, List<String> conf){
        seekHP(classDefItem, conf, Constants.HACK_ME_DEF, new SeekCallBack() {
            @Override
            public void doHack(ClassDefs.ClassDef classDefItem, List<HackPoint> hackPoints) {
                /*int directMeSize = classDefItem.classData.directMethodsSize.value;
                int directMeCodeOff = 0;
                for (int i = 0; i < directMeSize; i++) {
                    if (i == 0) {
                        directMeCodeOff = classDefItem.classData.directMethods[i].codeOff.value;
                    }else{
                        HackPoint point = classDefItem.classData.directMethods[i].codeOff.clone();
                        hackPoints.add(point);
                        classDefItem.classData.directMethods[i].codeOff.value = directMeCodeOff;
                    }
                }*/

                //以第一个为默认值
                int virtualMeSize = classDefItem.classData.virtualMethodsSize.value;
                int virtualMeCodeOff = 0;
                for (int i = 0; i < virtualMeSize; i++) {
                    if (i == 0) {
                        virtualMeCodeOff = classDefItem.classData.virtualMethods[i].codeOff.value;
                    }else{
                        HackPoint point = classDefItem.classData.virtualMethods[i].codeOff.clone();
                        hackPoints.add(point);
                        classDefItem.classData.virtualMethods[i].codeOff.value = virtualMeCodeOff;
                    }
                }
            }
        });
    }

    //查找配置文件所在类位置
    private void seekHP(ClassDefs.ClassDef[] classDefItem, List<String> conf, String type, SeekCallBack callBack){
        if (conf == null) {
            return;
        }
        for (int i = 0; i < conf.size(); i++) {
            String classname = conf.get(i);
            boolean isDef = false;
            for (int j = 0; j < classDefItem.length; j++) {
                String className = dexFile.typeIds.getString(dexFile, classDefItem[j].classIdx);
                className = pathToPackages(className); //获取类名
                if (className.equals(classname)) {
                    callBack.doHack(classDefItem[j], this.hackPoints); //具体操作
                    log(type, conf.get(i));
                    isDef = true;
                }
            }
            if (isDef == false) {
                log("warning", "con't find class:" + classname);
            }
        }
    }

    //具体操作回调处理
    interface SeekCallBack {
        void doHack(ClassDefs.ClassDef classDefItem, List<HackPoint> hackPoints);
    }

    //修改header
    private void hackHeader() {
        //修改文件长度
        Header header = dexFile.header;
        header.fileSize = this.dexBuff.length;
        header.write(dexBuff); //需要先修改文件长度，才能计算signature checksum

        //修复 signature 校验
        log("old_signature", binToHex(dexFile.header.signature));
        byte[] signature = signature(dexBuff, SIGNATURE_LEN + SIGNATURE_OFF);
        header.signature = signature;
        log("new_signature", binToHex(signature));
        header.write(dexBuff); //需要先写sinature,才能计算checksum，凸

        //修复 checksum 校验
        log("old_checksum", intToHex(dexFile.header.checksum));
        int checksum = checksum_Lit(dexBuff, CHECKSUM_LEN + CHECKSUM_OFF);
        header.checksum = checksum;
        log("new_checksum", intToHex(checksum));

        header.write(dexBuff);
    }

    //保留修改信息
    private void appendHP() {
        byte[] pointsBuff = new byte[]{};
        for (int i = 0; i < hackPoints.size(); i++) {
            byte[] pointBuff = hackpToBin(hackPoints.get(i));
            pointsBuff = BufferUtil.append(pointsBuff, pointBuff, pointBuff.length);
        }
        dexBuff = BufferUtil.append(dexBuff, pointsBuff, pointsBuff.length);
    }
}
