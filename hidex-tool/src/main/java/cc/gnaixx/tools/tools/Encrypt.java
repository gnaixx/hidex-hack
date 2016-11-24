package cc.gnaixx.tools.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

/**
 * 名称: Encrypt
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/20
 */

public class Encrypt {
    //计算checksum
    public static byte[] checksum(byte[] data, int off) {
        int len = data.length - off;
        Adler32 adler32 = new Adler32();
        adler32.reset();
        adler32.update(data, off, len);
        long checksum = adler32.getValue();
        byte[] checksumbs = new byte[]{
                (byte) checksum,
                (byte) (checksum >> 8),
                (byte) (checksum >> 16),
                (byte) (checksum >> 24)};
        return checksumbs;
    }


    //计算signature
    public static byte[] signature(byte[] data, int off) {
        int len = data.length - off;
        byte[] signature = SHA1(data, off, len);
        return signature;
    }

    //sha1算法
    public static byte[] SHA1(byte[] decript, int off, int len) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript, off, len);
            byte messageDigest[] = digest.digest();
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

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
    public static int[] charToInt(char[] data){
        int[] targe = new int[data.length];
        for(int i=0; i<data.length; i++){
            targe[i] = (int) data[i];
        }
        return targe;
    }
}
