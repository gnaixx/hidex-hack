package cc.gnaixx.tools.util;

import cc.gnaixx.tools.model.Uleb128;

import static cc.gnaixx.tools.model.DexCon.UINT_LEN;
import static cc.gnaixx.tools.model.DexCon.USHORT_LEN;

/**
 * 名称: Writer
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/25
 */

public class Writer {
    private byte[] buffer;
    private int offset = 0;

    public Writer(byte[] buffer, int off) {
        this.buffer = buffer;
        this.offset = off;
    }

    public void setStream(byte[] buffer) {
        this.buffer = buffer;
    }

    public void setOff(int off) {
        this.offset = off;
    }

    public void replace(byte[] replacement, int len) {
        for (int i = 0; i < len; i++) {
            this.buffer[i + offset] = replacement[i];
        }
        this.offset += len;
    }

    public void writeUint(int val) {
        for (int i = 0; i < UINT_LEN; i++) {
            buffer[offset + i] = (byte) ((val >> (8 * i)) & 0xff);
        }
        this.offset += UINT_LEN;
    }

    public void writeUshort(int val) {
        for (int i = 0; i < USHORT_LEN; i++) {
            buffer[offset + i] = (byte) ((val >> (8 * i)) & 0xff);
        }
        this.offset = USHORT_LEN;
    }

    public void writeUleb128(Uleb128 val){
        byte realVal[] = val.getOrigValue();
        int len = val.getLength();
        replace(realVal, len);
    }
}
