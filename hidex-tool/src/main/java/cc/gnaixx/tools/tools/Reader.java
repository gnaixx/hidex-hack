package cc.gnaixx.tools.tools;

import cc.gnaixx.tools.model.Uleb128;

/**
 * 名称: Format
 * 描述: 非线程安全
 *
 * @author xiangqing.xue
 * @date 2016/11/21
 */

public class Reader {
    public static final int UINT_LEN        = 4;
    public static final int USHORT_LEN      = 2;
    
    private byte[] stream;
    private int offset = 0;
    
    public Reader(byte[] stream, int off){
        this.stream =stream;
        this.offset = off;
    }

    public void setStream(byte[] stream){
        this.stream = stream;
    }

    public void setOff(int off){
        offset = off;
    }

    public void reset(byte[] stream, int off){
        this.stream = stream;
        this.offset = off;
    }

    public byte[] subdex(int len) {
        byte[] sub = new byte[len];
        for (int i = 0; i < len; i++) {
            sub[i] = stream[i + offset];
        }
        offset += len;
        return sub;
    }

    public int getUint() {
        int value = 0;
        for (int i = 0; i < UINT_LEN; i++) {
            int seg = stream[offset + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << 8 * i;
        }
        offset += UINT_LEN; //int 四个字节
        return value;
    }

    public char getUshort() {
        char value = 0;
        for (int i = 0; i < USHORT_LEN; i++) {
            int seg = stream[offset + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << 8 * i;
        }
        offset += USHORT_LEN; //int 四个字节
        return value;
    }

    public Uleb128 getUleb128() {
        int value = 0;
        int count = 0;
        byte realVal[] = new byte[4];
        boolean flag = false;
        do {
            byte seg = stream[offset];
            if ((seg >> 7) == 1) {
                flag = true;
            }
            seg = stream[offset];
            value += ((seg << 1) >> 1) << (7 * count);
            realVal[count] = stream[offset];
            count++;
            offset++;
        } while (flag);
        return new Uleb128(realVal, value);
    }

}
