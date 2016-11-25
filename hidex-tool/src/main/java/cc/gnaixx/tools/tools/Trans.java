package cc.gnaixx.tools.tools;

/**
 * 名称: Trans
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/25
 */

public class Trans {
    // 二进制转16进制
    public static String binToHex(byte[] data) {
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
        return hexString.toString().toUpperCase();
    }

    //二进制转16进制
    public static String binToHex_Lit(byte[] data) {
        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        // 字节数组转换为 十六进制 数
        for (int i = data.length - 1; i >= 0; i--) {
            String shaHex = Integer.toHexString(data[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexString.append(0);
            }
            hexString.append(shaHex);
        }
        return hexString.toString().toUpperCase();
    }

    //int转16进制
    public static String intToHex(int integer) {
        byte[] bin = new byte[]{
                (byte) ((integer >> 24) & 0xFF),
                (byte) ((integer >> 16) & 0xFF),
                (byte) ((integer >> 8) & 0xFF),
                (byte) (integer & 0xFF)
        };
        return binToHex(bin);
    }

    //二进制转字符串
    public static String binToStr(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (byte b : data) {
            String z = b == 0 ? "." : new String(new byte[]{b});
            sb.append(z);
        }
        return sb.toString();
    }

    //
    public static int[] charToInt(char[] data) {
        int[] targe = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            targe[i] = (int) data[i];
        }
        return targe;
    }

    //
    public static String pathToPackages(String path) {
        String packageName = path.replaceAll("/", ".");
        packageName = packageName.startsWith("L") ? packageName.substring(1, packageName.length() - 1) : packageName;
        return packageName;
    }
}
