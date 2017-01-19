package cc.gnaixx.tools.util;

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
    public static byte[] checksum_bin(byte[] data, int off) {
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

    public static int checksum(byte[] data, int off) {
        int len = data.length - off;
        Adler32 adler32 = new Adler32();
        adler32.reset();
        adler32.update(data, off, len);
        long checksum = adler32.getValue();
        return (int)checksum;
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

}
