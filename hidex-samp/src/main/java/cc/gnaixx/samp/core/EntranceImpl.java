package cc.gnaixx.samp.core;

//import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cc.gnaixx.hidex_libs.inter.Entrance;

/**
 * 名称: Main
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/16
 */

public class EntranceImpl implements Entrance {


    //静态变量
    private final static String TAG = "HIDEX";
    private final static boolean DEBUG = true;
    private final static boolean RELEASE = false;
    private final static int FLAG = 1;
    public final static int COUNT = 2;
    private final static byte[] NAME = new byte[]{0x47, 0x4E, 0x41, 0x49, 0x58, 0x58};
    private final static String STRING = "NAME:薛祥清";


    //示例变量
    private int instanceFieldsSize = 0;
    private String instanceFieldsName = "gnaixx";


    @Override
    public String md5(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buff = data.getBytes();
            digest.update(buff);
            byte[] result = digest.digest();
            BigInteger bInt = new BigInteger(1, result);
            // Create Hex String
            String md5 = bInt.toString(16).toLowerCase(Locale.getDefault());
            // pad zero
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        if (data != null && key != null) {
            try {
                byte[] tkey = key;
                //生成密钥
                SecretKey ks = new SecretKeySpec(tkey, "AES");
                Cipher ciph = Cipher.getInstance("AES");
                ciph.init(Cipher.ENCRYPT_MODE, ks);
                return byte2Hex(ciph.doFinal(data)).getBytes("utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public byte[] decrypt(byte[] data, byte[] key) {
        if (data != null && key != null) {
            try {
                byte[] tkey = key;
                SecretKey ks = new SecretKeySpec(tkey, "AES");
                Cipher ciph = Cipher.getInstance("AES");
                ciph.init(Cipher.DECRYPT_MODE, ks);
                byte[] by = hex2Byte(new String(data, "utf-8"));
                return ciph.doFinal(by);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public String byte2Hex(byte buf[]) {
        if (buf != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buf.length; i++) {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase(Locale.getDefault()));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public byte[] hex2Byte(String hexStr) {
        if (hexStr != null) {
            if (hexStr.length() < 1)
                return null;
            byte[] result = new byte[hexStr.length() / 2];
            for (int i = 0; i < hexStr.length() / 2; i++) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getStaticFields() {
        JSONObject json = new JSONObject();
        try {
            json.put("TAG", TAG);
            json.put("DEBUG", DEBUG);
            json.put("RELEASE", RELEASE);
            json.put("FLAG", FLAG);
            json.put("COUNT", COUNT);
            json.put("NAME", new String(NAME));
            json.put("STRING", STRING);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
