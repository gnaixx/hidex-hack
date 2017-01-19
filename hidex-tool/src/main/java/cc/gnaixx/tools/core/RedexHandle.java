package cc.gnaixx.tools.core;


import com.alibaba.fastjson.JSON;

import cc.gnaixx.tools.model.HackPoint;
import cc.gnaixx.tools.model.Uleb128;
import cc.gnaixx.tools.util.Trans;
import cc.gnaixx.tools.util.Writer;

import static cc.gnaixx.tools.model.DexCon.CHECKSUM_LEN;
import static cc.gnaixx.tools.model.DexCon.CHECKSUM_OFF;
import static cc.gnaixx.tools.model.DexCon.FILE_SIZE_OFF;
import static cc.gnaixx.tools.model.DexCon.MAP_ITEM_LEN;
import static cc.gnaixx.tools.model.DexCon.MAP_OFF_OFF;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_LEN;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_OFF;
import static cc.gnaixx.tools.model.DexCon.UINT_LEN;
import static cc.gnaixx.tools.util.BufferUtil.getUint;
import static cc.gnaixx.tools.util.BufferUtil.replace;
import static cc.gnaixx.tools.util.BufferUtil.subdex;
import static cc.gnaixx.tools.util.Encrypt.checksum;
import static cc.gnaixx.tools.util.Encrypt.checksum_bin;
import static cc.gnaixx.tools.util.Encrypt.signature;
import static cc.gnaixx.tools.util.Log.log;
import static cc.gnaixx.tools.util.Trans.binToHex;
import static cc.gnaixx.tools.util.Trans.binToHex_Lit;
import static cc.gnaixx.tools.util.Trans.intToBin_Lit;

/**
 * 名称: RedexHandle
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/29
 */

public class RedexHandle {
    private byte[] dexBuff;
    private byte[] hackInfoBuff;

    public RedexHandle(byte[] dexBuff) {
        this.dexBuff = dexBuff;
    }

    public byte[] redex() {
        int mapOff = getUint(dexBuff, MAP_OFF_OFF); //获取map_off
        int mapSize = getUint(dexBuff, mapOff); //获取map_size
        int hackInfoStart = mapOff + UINT_LEN + (mapSize * MAP_ITEM_LEN); //获取 hackinfo 开始地址
        int hackInfoLen = dexBuff.length - hackInfoStart; //获取hackinfo 长度
        hackInfoBuff = subdex(dexBuff, hackInfoStart, hackInfoLen); //获取hack数据
        /*for(int i=0; i<hackInfoLen/12; i++){
            byte[] temp = subdex(hackInfoBuff, i*12, 12);
            log("hackInfo", binToHex(temp));
        }*/

        int dexLen = dexBuff.length - hackInfoLen;
        dexBuff = subdex(dexBuff, 0, dexLen); //截取原始dex长度
        HackPoint[] hackPoints = Trans.binToHackP(hackInfoBuff);  //修复hack点
        for (int i = 0; i < hackPoints.length; i++) {
            log("hackPoint", JSON.toJSONString(hackPoints[i]));
            recovery(hackPoints[i]);
        }

        byte[] fileSize = intToBin_Lit(dexLen); //修复文件长度
        replace(dexBuff, fileSize, FILE_SIZE_OFF, UINT_LEN);

        byte[] signature = signature(dexBuff, SIGNATURE_LEN + SIGNATURE_OFF); //修复signature校验
        replace(dexBuff, signature, SIGNATURE_OFF, SIGNATURE_LEN);

        byte[] checksum = checksum_bin(dexBuff, CHECKSUM_LEN + CHECKSUM_OFF); //修复checksum校验
        replace(dexBuff, checksum, CHECKSUM_OFF, CHECKSUM_LEN);

        log("signature", binToHex(signature));
        log("checksum", binToHex_Lit(checksum));
        return this.dexBuff;
    }

    //还原原始值
    private void recovery(HackPoint hackPoint) {
        Writer writer = new Writer(this.dexBuff, hackPoint.offset);
        if (hackPoint.type == HackPoint.USHORT) {
            writer.writeUshort(hackPoint.value);
        }
        else if (hackPoint.type == HackPoint.UINT) {
            writer.writeUint(hackPoint.value);
        }
        else if (hackPoint.type == HackPoint.ULEB128) {
            Uleb128 uleb128 = Trans.intToUleb128(hackPoint.value);
            writer.writeUleb128(uleb128);
        }
    }
}
