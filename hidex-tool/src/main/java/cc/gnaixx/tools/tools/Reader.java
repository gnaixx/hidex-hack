package cc.gnaixx.tools.tools;

import cc.gnaixx.tools.model.Uleb128;

import static cc.gnaixx.tools.model.DexCon.UINT_LEN;
import static cc.gnaixx.tools.model.DexCon.USHORT_LEN;

/**
 * 名称: Format
 * 描述: 非线程安全
 *
 * @author xiangqing.xue
 * @date 2016/11/21
 */

public class Reader {
    private byte[] buffer;
    private int    offset = 0;
    
    public Reader(byte[] buffer, int off){
        this.buffer =buffer;
        this.offset = off;
    }

    public void setBuffer(byte[] buffer){
        this.buffer = buffer;
    }

    public void setOff(int off){
       this.offset = off;
    }

    public int getOff(){
        return this.offset;
    }

    public void reset(byte[] buffer, int off){
        this.buffer = buffer;
        this.offset = off;
    }

    public byte[] subdex(int len) {
        byte[] sub = new byte[len];
        for (int i = 0; i < len; i++) {
            sub[i] = buffer[i + offset];
        }
        offset += len;
        return sub;
    }

    public int readUint() {
        int value = 0;
        for (int i = 0; i < UINT_LEN; i++) {
            int seg = buffer[offset + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << (8 * i);
        }
        offset += UINT_LEN; //int 四个字节
        return value;
    }

    public char readUshort() {
        char value = 0;
        for (int i = 0; i < USHORT_LEN; i++) {
            int seg = buffer[offset + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << (8 * i);
        }
        offset += USHORT_LEN; //int 四个字节
        return value;
    }

    public Uleb128 readUleb128() {
        int value = 0;
        int count = 0;
        byte realVal[] = new byte[4];
        boolean flag = false;
        do {
            byte seg = buffer[offset];
            if ((seg >> 7) == 1) {
                flag = true;
            }
            seg = buffer[offset];
            value += ((seg << 1) >> 1) << (7 * count);
            realVal[count] = buffer[offset];
            count++;
            offset++;
        } while (flag);
        return new Uleb128(realVal, value);
    }

}
