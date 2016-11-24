package cc.gnaixx.tools.tools;

import cc.gnaixx.tools.model.Uleb128;

/**
 * 名称: StreamUtil
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class StreamUtil {

    public static byte[] subdex(byte[] stream, int off, int len) {
        byte[] sub = new byte[len];
        for (int i = 0; i < len; i++) {
            sub[i] = stream[i + off];
        }
        return sub;
    }

    public static Uleb128 getUleb128(byte[] stream, int off) {
        int value = 0;
        int count = 0;
        byte realVal[] = new byte[4];
        boolean flag = false;
        do {
            byte seg = stream[off];
            if ((seg >> 7) == 1) {
                flag = true;
            }
            seg = stream[off];
            value += ((seg << 1) >> 1) << (7 * count);
            realVal[count] = stream[off];
            count++;
            off++;
        } while (flag);
        return new Uleb128(realVal, value);
    }

    public static byte[] replace(byte[] source, byte[] replacement, int off, int len) {
        for (int i = 0; i < len; i++) {
            source[i+off] = replacement[i];
        }
        return source;
    }
}
