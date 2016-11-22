package cc.gnaixx.tools.tools;

/**
 * 名称: Format
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/21
 */

public class Tranceform {

    public static byte[] subdex(byte[] data, int off, int len) {
        byte[] sub = new byte[len];
        for (int i = 0; i < len; i++) {
            sub[i] = data[i + off];
        }
        return sub;
    }

    public static int getUint(byte[] data, int off) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int seg = data[off + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << 8 * i;
        }
        return value;
    }

    public static int[] getUleb(byte[] data, int off) {
        int value = 0;
        int count = 0;
        boolean flag = false;
        do {
            byte seg = data[off];
            if ((seg >> 7) == 1) {
                flag = true;
            }
            seg = data[off];
            value += ((seg << 1) >> 1) << (7 * count);
            count++;
            off++;
        } while (flag);
        return new int[]{value, count};
    }

    // 16进制
    public static String toHexString(byte[] data) {
        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        // 字节数组转换为 十六进制 数
        for (int i = 0; i < data.length; i++) {
            String shaHex = Integer.toHexString(data[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString();
    }
}
