package cc.gnaixx.tools.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

import static cc.gnaixx.tools.tools.Log.log;
import static cc.gnaixx.tools.tools.Tranceform.toHexString;

/**
 * 名称: Encrypt
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/20
 */

public class Encrypt {
    //计算checksum
    public static long checksum(byte[] data, int off, int len) {
        Adler32 adler32 = new Adler32();
        adler32.reset();
        adler32.update(data, off, len);
        long checksum = adler32.getValue();

        data[8] = (byte) checksum;
        data[9] = (byte) (checksum >> 8);
        data[10] = (byte) (checksum >> 16);
        data[11] = (byte) (checksum >> 24);

        log("checksum -> " + Long.toHexString(checksum));
        return checksum;
    }


    //计算signature
    public static byte[] signature(byte[] data, int off, int len) {
        byte[] sinature = SHA1(data, off, len);
        for (int i = 0; i < sinature.length; i++) {
            data[i + 12] = sinature[i];
        }
        log("signature -> " + toHexString(sinature));
        return sinature;
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
