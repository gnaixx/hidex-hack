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

    private int offset = 0;

    public void setOff(int off){
        offset = off;
    }

    public Reader(int off){
        this.offset = off;
    }

    public byte[] subdex(byte[] data, int len) {
        byte[] sub = new byte[len];
        for (int i = 0; i < len; i++) {
            sub[i] = data[i + offset];
        }
        offset += len;
        return sub;
    }

    public static byte[] subdex(byte[] data, int off, int len) {
        byte[] sub = new byte[len];
        for (int i = 0; i < len; i++) {
            sub[i] = data[i + off];
        }
        return sub;
    }

    public int getUint(byte[] data) {
        int value = 0;
        for (int i = 0; i < UINT_LEN; i++) {
            int seg = data[offset + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << 8 * i;
        }
        offset += UINT_LEN; //int 四个字节
        return value;
    }

    public char getUshort(byte[] data) {
        char value = 0;
        for (int i = 0; i < USHORT_LEN; i++) {
            int seg = data[offset + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << 8 * i;
        }
        offset += USHORT_LEN; //int 四个字节
        return value;
    }

    public Uleb128 getUleb128(byte[] data) {
        int value = 0;
        int count = 0;
        byte realVal[] = new byte[4];
        boolean flag = false;
        do {
            byte seg = data[offset];
            if ((seg >> 7) == 1) {
                flag = true;
            }
            seg = data[offset];
            value += ((seg << 1) >> 1) << (7 * count);
            realVal[count] = data[offset];
            count++;
            offset++;
        } while (flag);
        return new Uleb128(realVal, value);
    }

    public static Uleb128 getUleb128(byte[] data, int off){
        int value = 0;
        int count = 0;
        byte realVal[] = new byte[4];
        boolean flag = false;
        do {
            byte seg = data[off];
            if ((seg >> 7) == 1) {
                flag = true;
            }
            seg = data[off];
            value += ((seg << 1) >> 1) << (7 * count);
            realVal[count] = data[off];
            count++;
            off++;
        } while (flag);
        return new Uleb128(realVal, value);
    }
}
