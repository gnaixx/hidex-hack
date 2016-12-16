package cc.gnaixx.tools.core;


import cc.gnaixx.tools.model.HackPoint;
import cc.gnaixx.tools.model.Uleb128;
import cc.gnaixx.tools.tools.Trans;
import cc.gnaixx.tools.tools.Writer;

import static cc.gnaixx.tools.model.DexCon.CHECKSUM_LEN;
import static cc.gnaixx.tools.model.DexCon.CHECKSUM_OFF;
import static cc.gnaixx.tools.model.DexCon.MAP_ITEM_LEN;
import static cc.gnaixx.tools.model.DexCon.MAP_OFF_OFF;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_LEN;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_OFF;
import static cc.gnaixx.tools.model.DexCon.UINT_LEN;
import static cc.gnaixx.tools.tools.BufferUtil.getUint;
import static cc.gnaixx.tools.tools.BufferUtil.replace;
import static cc.gnaixx.tools.tools.BufferUtil.subdex;
import static cc.gnaixx.tools.tools.Encrypt.checksum;
import static cc.gnaixx.tools.tools.Encrypt.signature;
import static cc.gnaixx.tools.tools.Log.log;

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
        int mapOff = getUint(dexBuff, MAP_OFF_OFF);
        int mapSize = getUint(dexBuff, mapOff);
        int hackInfoStart = mapOff + UINT_LEN + (mapSize * MAP_ITEM_LEN);
        int hackInfoLen = dexBuff.length - hackInfoStart;
        hackInfoBuff = subdex(dexBuff, hackInfoStart, hackInfoLen); //获取hack数据
        log("hackInfo", Trans.binToHex(hackInfoBuff));

        dexBuff = subdex(dexBuff, 0, dexBuff.length - hackInfoLen); //恢复长度

        HackPoint[] hackPoints = Trans.binToHackP(hackInfoBuff);  //修复hack点
        for (int i = 0; i < hackPoints.length; i++) {
            recovery(hackPoints[i]);
        }

        byte[] signature = signature(dexBuff, SIGNATURE_LEN + SIGNATURE_OFF); //修复校验
        replace(dexBuff, signature, SIGNATURE_OFF, SIGNATURE_LEN);
        byte[] checksum = checksum(dexBuff, CHECKSUM_LEN + CHECKSUM_OFF);
        replace(dexBuff, checksum, CHECKSUM_OFF, CHECKSUM_LEN);

        return this.dexBuff;
    }

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
