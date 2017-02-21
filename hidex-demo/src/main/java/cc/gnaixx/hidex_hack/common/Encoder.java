package cc.gnaixx.hidex_hack.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 名称: Encoder
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2017/2/20
 */

public class Encoder {
    // encrypt by md5
    public static String md5(byte[] data) {
        if(data == null) return "";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(data);
            byte hashValue[] = digest.digest();
            BigInteger bInt = new BigInteger(1, hashValue);
            // Create Hex String
            String md5 = bInt.toString(16).toLowerCase();
            // pad zero
            while(md5.length() < 32 ){
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
